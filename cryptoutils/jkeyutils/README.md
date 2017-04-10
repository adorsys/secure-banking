# Keu Utils

JCA cryptography routines including

* The generation of keys
* The signing of keys
* The creation, storage and loading of keystores.

## Genetating a self signed certificate

```
	X500Name cn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
	keyPairData = new SelfSignedKeyPairBuilder().withEndEntityName(cn).withKeyAlg("RSA").withKeyLength(2048)
			.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).asCaCert(false).build();
	Assume.assumeNotNull(keyPairData);
	Assume.assumeNotNull(keyPairData.getKeyPair());
	Assume.assumeNotNull(keyPairData.getSubjectCert());
```
