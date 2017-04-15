package org.adorsys.encobject.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.encobject.domain.keystore.KeystoreData;
import org.adorsys.jkeygen.keystore.KeyStoreService;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;

import com.google.protobuf.ByteString;

/**
 * Service in charge of loading and storing user keys.
 * 
 * @author fpo
 *
 */
public class KeystorePersistence {
	
	private BlobStoreContext blobStoreContext;
	
	public KeystorePersistence(BlobStoreContext blobStoreContext) {
		super();
		this.blobStoreContext = blobStoreContext;
	}

	public void saveStore(KeyStore keystore, CallbackHandler storePassHandler, String container, String storeid) throws NoSuchAlgorithmException, CertificateException{
		String storeType = keystore.getType();
		try {
			byte[] bs = KeyStoreService.toByteArray(keystore, storeid, storePassHandler);
			KeystoreData keystoreData = KeystoreData.newBuilder().setType(storeType).setKeystore(ByteString.copyFrom(bs)).build();
			BlobStore blobStore = blobStoreContext.getBlobStore();
			blobStore.createContainerInLocation(null, container);
			// add blob
			Blob blob = blobStore.blobBuilder(storeid)
			.payload(keystoreData.toByteArray())
			.build();
			blobStore.putBlob(container, blob);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public KeyStore loadKeystore(String container, String storeid, CallbackHandler handler) throws UnknownKeyStoreException, CertificateException, WrongKeystoreCredentialException, MissingKeystoreAlgorithmException, MissingKeystoreProviderException, MissingKeyAlgorithmException, IOException{
		KeystoreData keystoreData = loadKeystoreData(container, storeid);
		return loadKeystore(keystoreData, storeid, handler);
	}	

	private KeyStore loadKeystore(KeystoreData keystoreData, String storeid, CallbackHandler handler) throws WrongKeystoreCredentialException, MissingKeystoreAlgorithmException, MissingKeystoreProviderException, MissingKeyAlgorithmException, CertificateException, IOException {
		try {
			return KeyStoreService.loadKeyStore(keystoreData.getKeystore().toByteArray(), storeid, keystoreData.getType(), handler);
		} catch (UnrecoverableKeyException e) {
			throw new WrongKeystoreCredentialException(e);
		} catch (KeyStoreException e) {
			if(e.getCause()!=null){
				Throwable cause = e.getCause();
				if(cause instanceof NoSuchAlgorithmException){
					throw new MissingKeystoreAlgorithmException(cause.getMessage(), cause);
				}
				if(cause instanceof NoSuchProviderException){
					throw new MissingKeystoreProviderException(cause.getMessage(), cause);
				}
			}
			throw new IllegalStateException("Unidentified keystore exception", e);
		} catch (NoSuchAlgorithmException e) {
			throw new MissingKeyAlgorithmException(e.getMessage(), e);
		}
	}
	
	private KeystoreData loadKeystoreData(String container, String storeid) throws UnknownKeyStoreException{
		BlobStore blobStore = blobStoreContext.getBlobStore();
		Blob blob;
		try {
			blob = blobStore.getBlob(container, storeid);
			if(blob==null)  throw new UnknownKeyStoreException(container + "/" + storeid);
		} catch (ContainerNotFoundException e){
			throw new UnknownKeyStoreException(container);			
		}
		
		InputStream inputStream;
		try {
			inputStream = blob.getPayload().openStream();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		try {
			return KeystoreData.parseFrom(inputStream);
		} catch (IOException e) {
			throw new IllegalStateException("Invalid protocol buffer", e);
		}
	}
}
