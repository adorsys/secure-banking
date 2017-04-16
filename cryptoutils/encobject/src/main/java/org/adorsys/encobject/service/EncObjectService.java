package org.adorsys.encobject.service;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;

import org.adorsys.encobject.domain.ContentMetaInfo;
import org.adorsys.encobject.domain.KeyCredentials;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.jclouds.blobstore.BlobStoreContext;

public class EncObjectService {

	private KeystorePersistence keystorePersistence;
	private ObjectPersistence objectPersistence;

	public EncObjectService(BlobStoreContext blobStoreContext) {
		this.keystorePersistence = new KeystorePersistence(blobStoreContext);
		objectPersistence = new ObjectPersistence(blobStoreContext);
	}

	public byte[] readObject(KeyCredentials keyCredentials, ObjectHandle objectHandle)
			throws UnknownKeyStoreException, WrongKeystoreCredentialException, MissingKeystoreAlgorithmException,
			MissingKeystoreProviderException, MissingKeyAlgorithmException, CertificateException, IOException,
			WrongKeyCredentialException, ObjectNotFoundException {
		
		KeyStore keyStore = keystorePersistence.loadKeystore(keyCredentials.getHandle().getContainer(), 
				keyCredentials.getHandle().getName(), new PasswordCallbackHandler(keyCredentials.getStorepass().toCharArray()));
		return objectPersistence.loadObject(objectHandle, keyStore, new PasswordCallbackHandler(keyCredentials.getKeypass().toCharArray()));
	}
	
	public void writeObject(byte[] data, ContentMetaInfo metaIno, ObjectHandle handle, KeyCredentials keyCredentials)
			throws CertificateException, UnknownKeyStoreException, WrongKeystoreCredentialException, 
			MissingKeystoreAlgorithmException, MissingKeystoreProviderException, MissingKeyAlgorithmException, IOException, UnsupportedEncAlgorithmException, WrongKeyCredentialException, UnsupportedKeyLengthException{
		KeyStore keyStore = keystorePersistence.loadKeystore(keyCredentials.getHandle().getContainer(), 
				keyCredentials.getHandle().getName(), new PasswordCallbackHandler(keyCredentials.getStorepass().toCharArray()));
		objectPersistence.storeObject(data, metaIno, handle, keyStore, keyCredentials.getKeyid(), new PasswordCallbackHandler(keyCredentials.getKeypass().toCharArray()), null);
	}
}
