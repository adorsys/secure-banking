package org.adorsys.jkeygen.keypair;

import java.security.KeyPair;
import java.util.List;

import org.adorsys.jkeygen.utils.KeyUsageUtils;
import org.adorsys.jkeygen.validation.BatchValidator;
import org.adorsys.jkeygen.validation.KeyValue;
import org.adorsys.jkeygen.validation.ListOfKeyValueBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 * Generate a self signed certificate. Returns the alias of the key pair.
 * 
 * @author fpo
 *
 */
public class SelfSignedCertBuilder {
	
	private String signatureAlgo;
	private Integer notAfterInDays;
	private Integer notBeforeInDays = 0;
	private X500Name subjectDN;	
	private GeneralNames subjectAltNames;
	private boolean ca;
	
	/**
	 * Returns the message key pair subject certificate holder.
	 * 
	 * Following entity must be validated  
	 * 
	 * Will generate a self signed key pair. If there is no UniqueIdentifier in the provided 
	 * subjectDN, the generated public key identifier will be used for that purpose
	 * and for the subjectUniqueID of the certificate. Same applies for the issuer fields.
	 * 
	 * @return KeyPairAndCertificateHolder
	 */
	boolean dirty = false;
	public SelfSignedKeyPairData build(KeyPair keyPair) {
		if(dirty)throw new IllegalStateException("Builder can not be reused");
		dirty=true;
		List<KeyValue> notNullCheckList = ListOfKeyValueBuilder.newBuilder()
			.add("subjectDN", subjectDN)
			.add("signatureAlgo", signatureAlgo)
			.add("notBeforeInDays", notBeforeInDays)
			.add("notAfterInDays", notAfterInDays)
			.add("keyPair", keyPair)
			.build();
		
		List<String> nullList = BatchValidator.filterNull(notNullCheckList);
		if(nullList!=null && !nullList.isEmpty()){
			throw new IllegalArgumentException("Fields can not be null: " + nullList);
		}
		
		CaSignedCertificateBuilder builder = new CaSignedCertificateBuilder()
			.withCa(ca)
			.withNotBeforeInDays(notBeforeInDays)
			.withNotAfterInDays(notAfterInDays)
			.withSubjectDN(subjectDN)
			.withSubjectPublicKey(keyPair.getPublic());
		int[] keyUsages = ca?KeyUsageUtils.getCaKeyUsages():KeyUsageUtils.getStdKeyUsages();
		for (int keyUsage : keyUsages) builder = builder.withKeyUsage(keyUsage);

		if(subjectAltNames!=null)
			builder = builder.withSubjectAltNames(subjectAltNames);
		X509CertificateHolder subjectCert = builder.build(keyPair.getPrivate());

		return new SelfSignedKeyPairData(keyPair, subjectCert);
	}

	public SelfSignedCertBuilder withSubjectDN(X500Name subjectDN) {
		this.subjectDN = subjectDN;
		return this;
	}

	public SelfSignedCertBuilder withSubjectAltNames(GeneralNames subjectAltNames) {
		this.subjectAltNames = subjectAltNames;
		return this;
	}

	public SelfSignedCertBuilder withSignatureAlgo(String signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
		return this;
	}

	public SelfSignedCertBuilder withNotAfterInDays(Integer notAfterInDays) {
		this.notAfterInDays = notAfterInDays;
		return this;
	}

	public SelfSignedCertBuilder withNotBeforeInDays(Integer notBeforeInDays) {
		this.notBeforeInDays = notBeforeInDays;
		return this;
	}

	public  SelfSignedCertBuilder withCa(boolean ca) {
		this.ca = ca;
		return this;
	}	
}
