package org.adorsys.jkeygen.keypair;

import java.security.KeyPair;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

public class CaSignedCertificateBuilderTest {
	
	@Test
	public void testSignCertPositive() {

		KeyPair caKeyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assume.assumeNotNull(caKeyPair);
		
		X500Name caCn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData caKeyPairData = new SelfSignedCertBuilder().withSubjectDN(caCn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(true).build(caKeyPair);
		Assume.assumeNotNull(caKeyPairData);
		Assume.assumeNotNull(caKeyPairData.getKeyPair());
		Assume.assumeNotNull(caKeyPairData.getSubjectCert());

		X500Name userCn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Secure Banking User Cert").build();
		SelfSignedKeyPairData selfSignedKeyPairData = new SelfSignedCertBuilder().withSubjectDN(userCn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(false).build(caKeyPair);
		Assume.assumeNotNull(selfSignedKeyPairData);
		Assume.assumeNotNull(selfSignedKeyPairData.getKeyPair());
		Assume.assumeNotNull(selfSignedKeyPairData.getSubjectCert());

		X509CertificateHolder caSignedCertificate = new CaSignedCertificateBuilder()
				.withSubjectSampleCertificate(selfSignedKeyPairData.getSubjectCert())
				.withIssuerCertificate(caKeyPairData.getSubjectCert())
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(false)
				.build(caKeyPairData.getKeyPair().getPrivate());
		Assert.assertNotNull(caSignedCertificate);
	}

	@Test
	public void testSignCertNoCaCert() {

		KeyPair caKeyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assume.assumeNotNull(caKeyPair);
		
		X500Name caCn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData caKeyPairData = new SelfSignedCertBuilder().withSubjectDN(caCn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(false).build(caKeyPair);
		Assume.assumeNotNull(caKeyPairData);
		Assume.assumeNotNull(caKeyPairData.getKeyPair());
		Assume.assumeNotNull(caKeyPairData.getSubjectCert());

		X500Name userCn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Secure Banking User Cert").build();
		SelfSignedKeyPairData selfSignedKeyPairData = new SelfSignedCertBuilder().withSubjectDN(userCn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(false).build(caKeyPair);
		Assume.assumeNotNull(selfSignedKeyPairData);
		Assume.assumeNotNull(selfSignedKeyPairData.getKeyPair());
		Assume.assumeNotNull(selfSignedKeyPairData.getSubjectCert());
		
		try {
			new CaSignedCertificateBuilder()
					.withSubjectSampleCertificate(selfSignedKeyPairData.getSubjectCert())
					.withIssuerCertificate(caKeyPairData.getSubjectCert())
					.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(false)
					.build(caKeyPairData.getKeyPair().getPrivate());
			Assert.fail("Expecting IllegalArgumentException");
		} catch (IllegalArgumentException e){
			Assert.assertEquals("Invalid issuer certificate", e.getMessage());
		}
	}
}
