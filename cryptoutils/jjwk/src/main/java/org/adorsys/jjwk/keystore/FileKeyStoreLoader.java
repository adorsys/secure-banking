package org.adorsys.jjwk.keystore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A simple key store loader that load an existing key store from file or create 
 * one that is stored in the filed if specified.
 * 
 * @author fpo
 *
 */
public class FileKeyStoreLoader implements KeyStoreLoader {
	
	private KeyStore ks;
	
	/**
	 * Instantiate a {@link FileKeyStoreLoader} or create one.
	 * 
	 * @param keyStorFileName
	 * @param storeType
	 * @param keyStorePasswordSrc
	 * @param create
	 */
	public FileKeyStoreLoader(String keyStorFileName, String storeType, PasswordSource keyStorePasswordSrc, boolean create) {

		// Use default type if blank.
		if (StringUtils.isBlank(storeType))
			storeType = KeyStore.getDefaultType();
		
		try {
			ks = KeyStore.getInstance(storeType);
		} catch (KeyStoreException e) {
			throw new IllegalStateException(e);
		}
		File keyStorFile = new File(keyStorFileName);
		if (keyStorFile.exists()) {
			java.io.FileInputStream fis = null;
			try {
				fis = new java.io.FileInputStream(keyStorFileName);
				ks.load(fis, keyStorePasswordSrc.getPassword(null));
			} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
				throw new IllegalStateException(e);
			} finally {
				keyStorePasswordSrc.cleanup();
				IOUtils.closeQuietly(fis);
			}
		} else if (create){
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(keyStorFile);
				ks.store(fos, keyStorePasswordSrc.getPassword(null));
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
				throw new IllegalStateException(e);
			} finally {
				keyStorePasswordSrc.cleanup();
				IOUtils.closeQuietly(fos);
			}
		} else {
			throw new IllegalStateException("Key store no found on the given address");
		}		
	}

	@Override
	public KeyStore loadKeyStore() {
		return ks;
	}
}
