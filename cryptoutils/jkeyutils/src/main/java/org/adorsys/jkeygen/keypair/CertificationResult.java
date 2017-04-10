package org.adorsys.jkeygen.keypair;

import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;

/**
 * Hold key certificates.
 * 
 * @author fpo
 *
 */
public class CertificationResult {

	private final X509CertificateHolder subjectCert;
	
	private final List<X509CertificateHolder> issuerChain;

	public CertificationResult(X509CertificateHolder subjectCert, List<X509CertificateHolder> issuerChain) {
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
