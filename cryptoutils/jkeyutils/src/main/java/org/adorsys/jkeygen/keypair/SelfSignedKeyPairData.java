package org.adorsys.jkeygen.keypair;

import java.security.KeyPair;

import org.bouncycastle.cert.X509CertificateHolder;

public class SelfSignedKeyPairData {
	
	private final KeyPair keyPair;
	
	private final X509CertificateHolder subjectCert;

	public SelfSignedKeyPairData(KeyPair keyPair, X509CertificateHolder subjectCert) {
		super();
		this.keyPair = keyPair;
		this.subjectCert = subjectCert;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public X509CertificateHolder getSubjectCert() {
		return subjectCert;
	}

}
