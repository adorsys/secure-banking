package org.adorsys.encobject.service;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.adorsys.encobject.domain.KeyCredentials;
import org.adorsys.encobject.domain.ObjectInfo;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.jclouds.blobstore.BlobStoreContext;

import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;

public class EncObjectService {
	
	private DefaultJWEDecrypterFactory decrypterFactory = new DefaultJWEDecrypterFactory();
	private KeystorePersistence keystorePersistence;
	private BlobStoreContext blobStoreContext;
	private String container;
	
	public EncObjectService(BlobStoreContext blobStoreContext) {
		this.blobStoreContext = blobStoreContext;
		this.keystorePersistence = new KeystorePersistence(blobStoreContext);
	}


	public ObjectInfo readObjectInfo(KeyCredentials keyCredentials, String handle) throws UnknownKeyStoreException, WrongKeystoreCredentialException, 
		MissingKeystoreAlgorithmException, MissingKeystoreProviderException, MissingKeyAlgorithmException, 
		CertificateException, IOException, WrongKeyCredentialException, ObjectInfoNotFoundException {
		
		// Retrieve key
		String storeid = keyCredentials.getStoreid();
		
		// Load keystore data
		KeyStore ks = keystorePersistence.loadKeystore(container, storeid, new PasswordCallbackHandler(keyCredentials.getStorepass().toCharArray()));
		
		Key key;
		try {
			key = ks.getKey(keyCredentials.getKeyid(), keyCredentials.getKeypass().toCharArray());
		} catch (UnrecoverableKeyException e) {
			throw new WrongKeyCredentialException(e);
		} catch (KeyStoreException e) {
			// Initialization mus have happened
			throw new IllegalStateException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new MissingKeyAlgorithmException(e.getMessage(), e);
		}
//		return loadUnzipObjectInfo(handle, key);
		return null;
	}


	}
