package org.adorsys.jkeygen.keystore;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jkeygen.keypair.CertificationResult;
import org.adorsys.jkeygen.keypair.SelfSignedKeyPairData;

public class KeyPairData extends KeyEntryData {

	private final SelfSignedKeyPairData keyPairs;
	
	private final CertificationResult certification;
	
	public KeyPairData(SelfSignedKeyPairData keyPairs, CertificationResult certification, String alias, CallbackHandler passwordSource) {
		super(alias, passwordSource);
		this.keyPairs = keyPairs;
		this.certification = certification;
	}

	public SelfSignedKeyPairData getKeyPairs() {
		return keyPairs;
	}

	public CertificationResult getCertification() {
		return certification;
	}
}
