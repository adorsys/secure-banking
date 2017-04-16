package org.adorsys.encobject.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.encobject.domain.ContentMetaInfo;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.params.EncParamSelector;
import org.adorsys.encobject.params.EncryptionParams;
import org.adorsys.jjwk.selector.JWEEncryptedSelector;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.adorsys.jkeygen.keystore.PasswordCallbackUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;

import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEHeader.Builder;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;

public class ObjectPersistence {

	private DefaultJWEDecrypterFactory decrypterFactory = new DefaultJWEDecrypterFactory();
	
	private BlobStoreContext blobStoreContext;

	public ObjectPersistence(BlobStoreContext blobStoreContext) {
		super();
		this.blobStoreContext = blobStoreContext;
	}
	
	public void storeObject(byte[] data, ContentMetaInfo metaIno, ObjectHandle handle, KeyStore keyStore, String keyID, CallbackHandler keyPassHandler, EncryptionParams encParams) throws UnsupportedEncAlgorithmException, WrongKeyCredentialException, UnsupportedKeyLengthException {
		// We accept empty meta info
		if(metaIno==null) metaIno=new ContentMetaInfo();
		
		// Retrieve the key.
		Key key = readKey(keyStore, keyID, keyPassHandler);
		
		// Encryption params is optional. If not provided, we select an encryption param based on the key selected.
		if(encParams==null){
			encParams=EncParamSelector.selectEncryptionParams(key);
		}
		
		Builder headerBuilder = new JWEHeader.Builder(encParams.getEncAlgo(), encParams.getEncMethod()).keyID(keyID);

		// content type
		String contentTrype = metaIno.getContentTrype();
		if(StringUtils.isNotBlank(contentTrype)){
			headerBuilder = headerBuilder.contentType(contentTrype);
		}
		
		String zip = metaIno.getZip();
		if(StringUtils.isNotBlank(zip)){
			headerBuilder = headerBuilder.compressionAlgorithm(CompressionAlgorithm.DEF);
		} else {
			if(StringUtils.isNotBlank(contentTrype) && StringUtils.containsIgnoreCase(contentTrype, "text")){
				headerBuilder = headerBuilder.compressionAlgorithm(CompressionAlgorithm.DEF);
			}
		}
		
		Map<String, Object> addInfos = metaIno.getAddInfos();
		// exp
		if(metaIno.getExp()!=null){
			if(addInfos==null) addInfos = new HashMap<>();
			addInfos.put("exp", metaIno.getExp().getTime());
		}
		
		if(addInfos!=null){
			headerBuilder = headerBuilder.customParams(addInfos);
		}
		
		JWEHeader header = headerBuilder.build();
		
		JWEEncrypter jweEncrypter = JWEEncryptedSelector.geEncrypter(key, encParams.getEncAlgo(), encParams.getEncMethod());
		
		JWEObject jweObject = new JWEObject(header,new Payload(data));
		
		try {
			jweObject.encrypt(jweEncrypter);
		} catch (JOSEException e) {
			throw new IllegalStateException("Encryption error", e);
		}
		
		String jweEncryptedObject = jweObject.serialize();

		BlobStore blobStore = blobStoreContext.getBlobStore();
		blobStore.createContainerInLocation(null, handle.getContainer());
		// add blob
		Blob blob;
		try {
			blob = blobStore.blobBuilder(handle.getName())
			.payload(jweEncryptedObject.getBytes("UTF-8"))
			.build();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unsupported content type", e);
		}
		blobStore.putBlob(handle.getContainer(), blob);
	}

	public byte[] loadObject(ObjectHandle handle, KeyStore keyStore, CallbackHandler keyPassHandler) throws ObjectNotFoundException, WrongKeyCredentialException{

		BlobStore blobStore = blobStoreContext.getBlobStore();
		Blob blob;
		try {
			blob = blobStore.getBlob(handle.getContainer(), handle.getName());
			if(blob==null)  throw new ObjectNotFoundException(handle.getContainer() + "/" + handle.getName());
		} catch (ContainerNotFoundException e){
			throw new ObjectNotFoundException(handle.getContainer());			
		}
		
		InputStream jweEncryptedStream;
		try {
			jweEncryptedStream = blob.getPayload().openStream();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		String jweEncryptedObject;
		try {
			jweEncryptedObject = IOUtils.toString(jweEncryptedStream, "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException("Unsupported content type", e);
		}
		JWEObject jweObject;
		try {
			jweObject = JWEObject.parse(jweEncryptedObject);
		} catch (ParseException e) {
			throw new IllegalStateException("Can not parse jwe object", e);
		}
		String keyID = jweObject.getHeader().getKeyID();
		Key key = readKey(keyStore, keyID, keyPassHandler);

		JWEDecrypter decrypter;
		try {
			decrypter = decrypterFactory.createJWEDecrypter(jweObject.getHeader(), key);
		} catch (JOSEException e) {
			throw new IllegalStateException("No suitable key found", e);
		}
		try {
			jweObject.decrypt(decrypter);
		} catch (JOSEException e) {
			throw new WrongKeyCredentialException(e);
		}
		return jweObject.getPayload().toBytes();
	}
	
	/*
	 * Retrieves the key with the given keyID from the keystore. The key password will be retrieved by
	 * calling the keyPassHandler.
	 */
	private Key readKey(KeyStore keyStore, String keyID, CallbackHandler keyPassHandler) throws WrongKeyCredentialException {
		try {
			return keyStore.getKey(keyID, PasswordCallbackUtils.getPassword(keyPassHandler, keyID));
		} catch (UnrecoverableKeyException e) {
			throw new WrongKeyCredentialException(e);
		} catch (KeyStoreException e) {
			throw new WrongKeyCredentialException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}		
	}
}
