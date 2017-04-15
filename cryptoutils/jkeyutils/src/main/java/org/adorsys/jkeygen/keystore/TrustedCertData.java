package org.adorsys.jkeygen.keystore;

import org.bouncycastle.cert.X509CertificateHolder;

public class TrustedCertData extends KeyEntryData {

	private final X509CertificateHolder certificate;
	
	public TrustedCertData(X509CertificateHolder certificate, String alias) {
		super(alias, null);
		this.certificate = certificate;
	}

	public X509CertificateHolder getCertificate() {
		return certificate;
	}
}
