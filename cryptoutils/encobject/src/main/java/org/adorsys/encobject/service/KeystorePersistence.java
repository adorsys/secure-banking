package org.adorsys.encobject.service;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.domain.keystore.KeystoreData;
import org.adorsys.jkeygen.keystore.KeyStoreService;

import com.google.protobuf.ByteString;

/**
 * Service in charge of loading and storing user keys.
 * 
 * @author fpo
 *
 */
public class KeystorePersistence {
	
	private BlobStoreConnection blobStoreConnection;
	
	public KeystorePersistence(BlobStoreContextFactory blobStoreContextFactory) {
		this.blobStoreConnection = new BlobStoreConnection(blobStoreContextFactory);
	}

	public void saveKeyStore(KeyStore keystore, CallbackHandler storePassHandler, ObjectHandle handle) throws NoSuchAlgorithmException, CertificateException, UnknownContainerException{
		try {
			String storeType = keystore.getType();
			byte[] bs = KeyStoreService.toByteArray(keystore, handle.getName(), storePassHandler);
			KeystoreData keystoreData = KeystoreData.newBuilder().setType(storeType).setKeystore(ByteString.copyFrom(bs)).build();
			blobStoreConnection.putBlob(handle, keystoreData.toByteArray());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public KeyStore loadKeystore(ObjectHandle handle, CallbackHandler handler) throws ObjectNotFoundException, CertificateException, WrongKeystoreCredentialException, MissingKeystoreAlgorithmException, MissingKeystoreProviderException, MissingKeyAlgorithmException, IOException, UnknownContainerException{
		KeystoreData keystoreData = loadKeystoreData(handle);
		return initKeystore(keystoreData, handle.getName(), handler);
	}	
	
	private KeystoreData loadKeystoreData(ObjectHandle handle) throws ObjectNotFoundException, UnknownContainerException{
		byte[] keyStoreBytes = blobStoreConnection.getBlob(handle);
		try {
			return KeystoreData.parseFrom(keyStoreBytes);
		} catch (IOException e) {
			throw new IllegalStateException("Invalid protocol buffer", e);
		}
	}

	private KeyStore initKeystore(KeystoreData keystoreData, String storeid, CallbackHandler handler) throws WrongKeystoreCredentialException, MissingKeystoreAlgorithmException, MissingKeystoreProviderException, MissingKeyAlgorithmException, CertificateException, IOException {
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
}
