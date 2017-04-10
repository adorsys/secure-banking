package org.adorsys.jkeygen.keypair;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Date;
import java.util.List;

import org.adorsys.jkeygen.utils.KeyUsageUtils;
import org.adorsys.jkeygen.utils.ProviderUtils;
import org.adorsys.jkeygen.validation.BatchValidator;
import org.adorsys.jkeygen.validation.KeyValue;
import org.adorsys.jkeygen.validation.ListOfKeyValueBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 * Instantiates and stores a key pair and the corresponding self signed
 * certificate. Returns the alias of the key pair.
 * 
 * @author fpo
 *
 */
public class SelfSignedKeyPairBuilder {

	private static Provider provider = ProviderUtils.bcProvider;
	
	private Integer keyLength;
	private String keyAlg;
	private String signatureAlgo;
	private Integer notAfterInDays;
	private Integer notBeforeInDays = 0;
	
	
	private X500Name endEntityName;
	
	private GeneralNames subjectAlternativeNames;
	
	/**
	 * Returns the message key pair subject certificate holder.
	 * 
	 * Following entity must be validated  
	 * 
	 * @return KeyPairAndCertificateHolder
	 */
	boolean dirty = false;
	public SelfSignedKeyPairData build() {
		if(dirty)throw new IllegalStateException("Builder can not be reused");
		dirty=true;
		List<KeyValue> notNullCheckList = ListOfKeyValueBuilder.newBuilder()
			.add("endEntityName", endEntityName)
			.add("keyAlg", keyAlg)
			.add("signatureAlgo", signatureAlgo)
			.add("keyLength", keyLength)
			.add("notBeforeInDays", notBeforeInDays)
			.add("notAfterInDays", notAfterInDays).build();
		List<String> nullList = BatchValidator.filterNull(notNullCheckList);
		if(nullList!=null && !nullList.isEmpty()){
			throw new IllegalArgumentException("Fields can not be null: " + nullList);
		}
		return  generateSelfSignedKeyPair();
	}

	public SelfSignedKeyPairBuilder withEndEntityName(X500Name endEntityName) {
		this.endEntityName = endEntityName;
		return this;
	}
	
	public SelfSignedKeyPairBuilder withKeyLength(Integer keyLength) {
		this.keyLength = keyLength;
		return this;
	}

	public SelfSignedKeyPairBuilder withSubjectAlternativeNames(GeneralNames subjectAlternativeNames) {
		this.subjectAlternativeNames = subjectAlternativeNames;
		return this;
	}

	public SelfSignedKeyPairBuilder withKeyAlg(String keyAlg) {
		this.keyAlg = keyAlg;
		return this;
	}

	public SelfSignedKeyPairBuilder withSignatureAlgo(String signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
		return this;
	}

	public SelfSignedKeyPairBuilder withNotAfterInDays(Integer notAfterInDays) {
		this.notAfterInDays = notAfterInDays;
		return this;
	}

	public SelfSignedKeyPairBuilder withNotBeforeInDays(Integer notBeforeInDays) {
		this.notBeforeInDays = notBeforeInDays;
		return this;
	}

	/**
	 * Will generate a self signed key pair. If there is no UniqueIdentifier in the provided 
	 * subjectDN, the generated public key identifier will be used for that purpose
	 * and for the subjectUniqueID of the certificate. Same applies for the issuer fields.
	 * 
	 * @return KeyPairAndCertificateHolder
	 */
	protected SelfSignedKeyPairData generateSelfSignedKeyPair(){

		// Generate a key pair for the new EndEntity
		KeyPairGenerator kGen;
		try {
			kGen = KeyPairGenerator.getInstance(keyAlg, provider);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}

		kGen.initialize(keyLength);
		KeyPair keyPair = kGen.generateKeyPair();

		Date now = new Date();
		X509CertificateBuilder builder = new X509CertificateBuilder()
			.withCa(true)
			.withNotBefore(DateUtils.addDays(now, notBeforeInDays))
			.withNotAfter(DateUtils.addDays(now, notAfterInDays))
			.withSubjectDN(endEntityName)
			.withSubjectPublicKey(keyPair.getPublic());
		int[] keyUsages = KeyUsageUtils.getKeyUsageForUserKey();
		for (int keyUsage : keyUsages) {
			builder = builder.withKeyUsage(keyUsage);
		}
		if(subjectAlternativeNames!=null)
			builder = builder.withSubjectAltNames(subjectAlternativeNames);
		X509CertificateHolder subjectCert = builder.build(keyPair.getPrivate());

		return new SelfSignedKeyPairData(keyPair, subjectCert);
	}
	
}
