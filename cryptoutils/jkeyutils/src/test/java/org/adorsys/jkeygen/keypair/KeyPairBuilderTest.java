package org.adorsys.jkeygen.keypair;

import java.security.KeyPair;

import org.junit.Assert;
import org.junit.Test;

public class KeyPairBuilderTest {

	@Test
	public void test() {
		KeyPair keyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assert.assertNotNull(keyPair);
	}
	
}
