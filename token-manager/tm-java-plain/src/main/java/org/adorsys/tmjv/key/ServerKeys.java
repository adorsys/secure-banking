package org.adorsys.tmjv.key;

import com.nimbusds.jose.jwk.JWKSet;

public class ServerKeys {

	private final JWKSet keys;

	public ServerKeys(JWKSet keys) {
		this.keys = keys;
	}

	public JWKSet getKeys() {
		return keys;
	}
}
