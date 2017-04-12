package org.adorsys.jkeygen.utils;

import java.security.KeyPair;

import org.adorsys.jkeygen.keypair.CaSignedCertificateBuilder;
import org.adorsys.jkeygen.keypair.KeyPairBuilder;
import org.adorsys.jkeygen.keypair.SelfSignedCertBuilder;
import org.adorsys.jkeygen.keypair.SelfSignedKeyPairData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.junit.Assume;
import org.junit.Test;

import junit.framework.Assert;

public class CheckCaCertificateTest {

	@Test
	public void testCaCertificate() {
		KeyPair caKeyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assume.assumeNotNull(caKeyPair);
		
		X500Name caCn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData caKeyPairData = new SelfSignedCertBuilder().withSubjectDN(caCn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(true).build(caKeyPair);
		Assume.assumeNotNull(caKeyPairData);
		Assume.assumeNotNull(caKeyPairData.getKeyPair());
		Assume.assumeNotNull(caKeyPairData.getSubjectCert());
		
		Assert.assertTrue(CheckCaCertificate.isCaCertificate(caKeyPairData.getSubjectCert()));

	}

	@Test
	public void testCaCertificateNegative() {
		KeyPair caKeyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assume.assumeNotNull(caKeyPair);
		
		X500Name caCn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData caKeyPairData = new SelfSignedCertBuilder().withSubjectDN(caCn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(false).build(caKeyPair);
		Assume.assumeNotNull(caKeyPairData);
		Assume.assumeNotNull(caKeyPairData.getKeyPair());
		Assume.assumeNotNull(caKeyPairData.getSubjectCert());
		
		Assert.assertTrue(!CheckCaCertificate.isCaCertificate(caKeyPairData.getSubjectCert()));

	}
	
	@Test
	public void testSigingCertificate() {
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
		

		Assert.assertTrue(CheckCaCertificate.isSigingCertificate(caSignedCertificate, caKeyPairData.getSubjectCert()));
	}

	@Test
	public void testSigingCertificateNegative() {
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
		

		Assert.assertTrue(!CheckCaCertificate.isSigingCertificate(caSignedCertificate, selfSignedKeyPairData.getSubjectCert()));
	}
}
