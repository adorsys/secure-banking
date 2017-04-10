# Keu Utils

JCA cryptography routines including

* The generation of keys
* The signing of keys
* The creation, storage and loading of keystores.

## Generating a KeyPair

```
		KeyPair keyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assert.assertNotNull(keyPair);
```

## Genetating a Self Signed Certificate

```
		KeyPair keyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assume.assumeNotNull(keyPair);

		X500Name cn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData keyPairData = new SelfSignedCertBuilder().withSubjectDN(cn)
				.withSignatureAlgo("SHA256withRSA")
				.withNotAfterInDays(300).withCa(false).build(keyPair);
		Assert.assertNotNull(keyPairData);
		Assert.assertNotNull(keyPairData.getKeyPair());
		Assert.assertNotNull(keyPairData.getSubjectCert());
```

## Generating a Ca Certificate

```
		X500Name caCn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData caKeyPairData = new SelfSignedCertBuilder().withSubjectDN(caCn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(true).build(caKeyPair);
		Assume.assumeNotNull(caKeyPairData);
		Assume.assumeNotNull(caKeyPairData.getKeyPair());
		Assume.assumeNotNull(caKeyPairData.getSubjectCert());
```

## Signing a Certificate

```
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
```