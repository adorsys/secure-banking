package org.adorsys.jkeygen.keypair;

import java.security.KeyPair;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

public class SelfSignedCertBuilderTest extends SelfSignedCertBuilder {

	@Test
	public void test() {
		KeyPair keyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assume.assumeNotNull(keyPair);

		X500Name cn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData keyPairData = new SelfSignedCertBuilder().withSubjectDN(cn)
				.withSignatureAlgo("SHA256withRSA")
				.withNotAfterInDays(300).withCa(false).build(keyPair);
		Assert.assertNotNull(keyPairData);
		Assert.assertNotNull(keyPairData.getKeyPair());
		Assert.assertNotNull(keyPairData.getSubjectCert());
	}

}
