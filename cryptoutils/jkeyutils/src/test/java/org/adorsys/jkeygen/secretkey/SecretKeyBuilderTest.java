package org.adorsys.jkeygen.secretkey;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

public class SecretKeyBuilderTest {

	@Test
	public void test() {
		SecretKey secretKey = new SecretKeyBuilder().withKeyAlg("AES").withKeyLength(512).build();
		Assert.assertNotNull(secretKey);
	}

}
