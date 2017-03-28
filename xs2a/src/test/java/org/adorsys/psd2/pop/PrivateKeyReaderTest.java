package org.adorsys.psd2.pop;

import java.security.Key;
import java.security.KeyStore;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class PrivateKeyReaderTest {
	
	private static KeyStore ks;
	
	@BeforeClass
	public static void beforeClass(){
		ks = KeyStoreLoader.loadKeyStore("src/test/resources/xs2atestkeystore1key.jks", null, "storepaass".toCharArray(), false);
	}

	@Test
	public void test() {
		assumeNotNull(ks);
		
		List<Key> keys = PrivateKeyReader.exportKeys(ks, "keypass".toCharArray());
		assertNotNull(keys);
		assertTrue(keys.size()==1);
	}

}
