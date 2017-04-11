package org.adorsys.jkeygen.keypair;

import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;

public class CertifiedKeyPairData {

	private final X509CertificateHolder subjectCert;
	
	private final List<X509CertificateHolder> issuerChain;

	public CertifiedKeyPairData(X509CertificateHolder subjectCert, List<X509CertificateHolder> issuerChain) {
		super();
		this.subjectCert = subjectCert;
		this.issuerChain = issuerChain;
	}

	public X509CertificateHolder getSubjectCert() {
		return subjectCert;
	}

	public List<X509CertificateHolder> getIssuerChain() {
		return issuerChain;
	}

}
