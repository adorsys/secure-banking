package org.adorsys.jkeygen.keystore;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jkeygen.keypair.CertificationResult;
import org.adorsys.jkeygen.keypair.SelfSignedKeyPairData;

public class KeyPairStoreData {

	private final SelfSignedKeyPairData keyPairs;
	
	private final CertificationResult certification;
	
	private final CallbackHandler passwordSource;
	
	private final String alias;

	public KeyPairStoreData(SelfSignedKeyPairData keyPairs, CertificationResult certification,
			CallbackHandler passwordSource, String alias) {
		super();
		this.keyPairs = keyPairs;
		this.certification = certification;
		this.passwordSource = passwordSource;
		this.alias = alias;
	}

	public SelfSignedKeyPairData getKeyPairs() {
		return keyPairs;
	}

	public CertificationResult getCertification() {
		return certification;
	}

	public CallbackHandler getPasswordSource() {
		return passwordSource;
	}

	public String getAlias() {
		return alias;
	}
}
