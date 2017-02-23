package org.adorsys.tmjv.key;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class TmKeyPair {
	private final RSAPrivateKey privateKey;
	private final RSAPublicKey publicKey;
	public TmKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
		super();
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}
}
