package org.adorsys.jjwk.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

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
	
	public FileBasedPrivateKeysSource(KeyStoreParams keyStoreParams) {
		FileKeyStoreLoader fksl = new FileKeyStoreLoader(keyStoreParams.getKeystoreFilename(),
				keyStoreParams.getStoreType(), keyStoreParams.getKeyStorePassCallbackHandler());
		privateKeys = exportPrivateKeys(fksl.loadKeyStore(), keyStoreParams.getKeyPassCallbackHandler());
	}

	private JWKSet exportPrivateKeys(KeyStore keyStore, CallbackHandler callbackHandler){
		PasswordLookup pwLookup = new PasswordLookup() {
			@Override
			public char[] lookupPassword(String name) {
				PasswordCallback passwordCallback = new PasswordCallback(name, false);
				try {
					callbackHandler.handle(new Callback[]{passwordCallback});
				} catch (IOException | UnsupportedCallbackException e) {
					throw new IllegalStateException(e);
				}
				char[] password = passwordCallback.getPassword();
				passwordCallback.clearPassword();
				return password;
			}
		};
		try {
			return JWKSet.load(keyStore, pwLookup);
		} catch (KeyStoreException e) {
			throw new IllegalStateException(e);
		}
	}
}
