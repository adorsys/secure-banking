package org.adorsys.jjwk.keystore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

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
	public FileKeyStoreLoader(String keyStorFileName, String storeType, CallbackHandler keyStorePasswordSrc) {

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
			PasswordCallback passwordCallback = new PasswordCallback(keyStorFileName, false);
			try {
				keyStorePasswordSrc.handle(new PasswordCallback[]{passwordCallback});
				fis = new java.io.FileInputStream(keyStorFileName);
				ks.load(fis, passwordCallback.getPassword());
			} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
				throw new IllegalStateException(e);
			} catch (UnsupportedCallbackException e) {
				throw new IllegalStateException(e);
			} finally {
				passwordCallback.clearPassword();
				IOUtils.closeQuietly(fis);
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
