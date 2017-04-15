package org.adorsys.encobject.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import javax.crypto.SecretKey;

import org.adorsys.encobject.domain.ObjectInfo;
import org.adorsys.encobject.domain.mapper.ObjectInfoMapper;
import org.adorsys.encobject.domain.pbf.ObjectInfoData;
import org.adorsys.jjwk.selector.JWEEncryptedSelector;
import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import com.nimbusds.jose.jwk.OctetSequenceKey;

public class ObjectInfoPersistence {

	private DefaultJWEDecrypterFactory decrypterFactory = new DefaultJWEDecrypterFactory();
	
	private BlobStoreContext blobStoreContext;

	public ObjectInfoPersistence(BlobStoreContext blobStoreContext) {
		super();
		this.blobStoreContext = blobStoreContext;
	}
	
	public void storeObject(ObjectInfo objectInfo, String container, String handle, SecretKey key, String encAlgo, String encMethod){
		ObjectInfoData objectInfoData = ObjectInfoMapper.toPbf(objectInfo);
		
		JWEAlgorithm jweAlgorithm = JWEAlgorithm.parse(encAlgo);
		EncryptionMethod encryptionMethod = EncryptionMethod.parse(encMethod);
		JWEHeader header = new JWEHeader.Builder(jweAlgorithm, encryptionMethod).build();
		
		Algorithm alg = JWEAlgorithm.parse(key.getAlgorithm());
		OctetSequenceKey jwk = new OctetSequenceKey.Builder(key).algorithm(alg).build();		
		JWEEncrypter jweEncrypter = JWEEncryptedSelector.geEncrypter(jwk);
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try {
			objectInfoData.writeTo(byteStream);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		JWEObject jweObject = new JWEObject(header,new Payload(byteStream.toByteArray()));
		
		try {
			jweObject.encrypt(jweEncrypter);
		} catch (JOSEException e) {
			throw new IllegalStateException("Encryption error", e);
		}
		
		String jweEncryptedObject = jweObject.serialize();
		
		;

		BlobStore blobStore = blobStoreContext.getBlobStore();
		blobStore.createContainerInLocation(null, container);
		// add blob
		Blob blob;
		try {
			blob = blobStore.blobBuilder(handle)
			.payload(jweEncryptedObject.getBytes("UTF-8"))
			.build();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unsupported content type", e);
		}
		blobStore.putBlob(container, blob);
	}

	public ObjectInfo loadObjectInfo(String container, String handle, SecretKey key) throws ObjectInfoNotFoundException, WrongKeyCredentialException{

		BlobStore blobStore = blobStoreContext.getBlobStore();
		Blob blob;
		try {
			blob = blobStore.getBlob(container, handle);
			if(blob==null)  throw new ObjectInfoNotFoundException(container + "/" + handle);
		} catch (ContainerNotFoundException e){
			throw new ObjectInfoNotFoundException(container);			
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
		Payload payload = jweObject.getPayload();

		ObjectInfoData objectInfoData;
		try {
			objectInfoData = ObjectInfoData.parseFrom(payload.toBytes());
		} catch (IOException e) {
			throw new IllegalStateException("Invalid protocol buffer", e);
		}
		return ObjectInfoMapper.fromPbf(objectInfoData);
	}
}
