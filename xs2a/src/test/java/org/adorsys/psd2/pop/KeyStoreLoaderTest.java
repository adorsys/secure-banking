package org.adorsys.psd2.pop;

import java.security.KeyStore;

import org.junit.Assert;
import org.junit.Test;

public class KeyStoreLoaderTest {

	@Test
	public void test() {
		KeyStore ks = KeyStoreLoader.loadKeyStore("src/test/resources/xs2atestkeystore1key.jks", null, "storepaass".toCharArray(), false);
		Assert.assertNotNull(ks);
	}

}
