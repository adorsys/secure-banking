package org.adorsys.jkeygen.keypair;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.Assert;
import org.junit.Test;

public class SelfSignedKeyPairBuilderTest {

	@Test
	public void testGenKeypair() {
		X500Name cn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		SelfSignedKeyPairData keyPairData = new SelfSignedKeyPairBuilder().withEndEntityName(cn)
				.withKeyAlg("RSA").withSignatureAlgo("SHA256withRSA")
				.withKeyLength(2048).withNotAfterInDays(300).build();

		Assert.assertNotNull(keyPairData);
		Assert.assertNotNull(keyPairData.getKeyPair());
		Assert.assertNotNull(keyPairData.getSubjectCert());

	}
}
