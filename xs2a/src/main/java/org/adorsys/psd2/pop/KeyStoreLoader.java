package org.adorsys.psd2.pop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class KeyStoreLoader {
	
	public static KeyStore loadKeyStore(String keyStorFileName, String storeType, char[] keyStorePassword, boolean create) {

		// Use default type if blank.
		if (StringUtils.isBlank(storeType))
			storeType = KeyStore.getDefaultType();
		
		KeyStore ks;
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
				ks.load(fis, keyStorePassword);
			} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
				throw new IllegalStateException(e);
			} finally {
				IOUtils.closeQuietly(fis);
			}
		} else if (create){
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(keyStorFile);
				ks.store(new FileOutputStream(keyStorFile) , keyStorePassword);
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
				throw new IllegalStateException(e);
			} finally {
				IOUtils.closeQuietly(fos);
			}
		} else {
			return null;
		}
		return ks;
	}
}
