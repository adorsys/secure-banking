package org.adorsys.jjwk.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;

import org.adorsys.jjwk.keysource.PrivateKeysSource;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.PasswordLookup;

/**
 * File based implementation of a {@link PrivateKeysSource}, loading keystore from the file.
 * 
 * @author fpo
 *
 */
public class FileBasedPrivateKeysSource implements PrivateKeysSource{

	private JWKSet privateKeys;

	@Override
	public JWKSet load() {
		return privateKeys;
	}
	
	public FileBasedPrivateKeysSource(KeyStoreParams keyStoreParams, boolean createKeyStore) {
		FileKeyStoreLoader fksl = new FileKeyStoreLoader(keyStoreParams.getKeystoreFilename(),
				keyStoreParams.getStoreType(), keyStoreParams.getKeyStorePassword(), createKeyStore);
		privateKeys = exportPrivateKeys(fksl.loadKeyStore(), keyStoreParams.getKeyPassword());
	}

	private JWKSet exportPrivateKeys(KeyStore keyStore, PasswordSource passwordSource){
		PasswordLookup pwLookup = new PasswordLookup() {
			@Override
			public char[] lookupPassword(String name) {
				return passwordSource.getPassword(name);
			}
		};
		try {
			return JWKSet.load(keyStore, pwLookup);
		} catch (KeyStoreException e) {
			throw new IllegalStateException(e);
		} finally {
			passwordSource.cleanup();
		}
	}
}
