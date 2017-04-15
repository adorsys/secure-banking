package org.adorsys.jkeygen.keystore;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;

public class SecretKeyData extends KeyEntryData {

	private final SecretKey secretKey;
	private final String keyAlgo;

	public SecretKeyData(SecretKey secretKey, String alias, CallbackHandler passwordSource) {
		super(alias, passwordSource);
		this.secretKey = secretKey;
		this.keyAlgo = secretKey.getAlgorithm();
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public String getKeyAlgo() {
		return keyAlgo;
	}
	
}
