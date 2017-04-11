package org.adorsys.jkeygen.keystore;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jkeygen.keypair.CertifiedKeyPairData;
import org.adorsys.jkeygen.keypair.SelfSignedKeyPairData;

public class KeyPairStoreData {

	private final SelfSignedKeyPairData keyPairs;
	
	private final CertifiedKeyPairData certification;
	
	private final CallbackHandler passwordSource;
	
	private final String alias;

	public KeyPairStoreData(SelfSignedKeyPairData keyPairs, CertifiedKeyPairData certification,
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

	public CertifiedKeyPairData getCertification() {
		return certification;
	}

	public CallbackHandler getPasswordSource() {
		return passwordSource;
	}

	public String getAlias() {
		return alias;
	}
}
