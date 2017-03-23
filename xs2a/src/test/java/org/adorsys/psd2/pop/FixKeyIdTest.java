package org.adorsys.psd2.pop;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

public class FixKeyIdTest {

	private static final char[] keypass = "keypass".toCharArray();
	private static KeyStore ks;

	@Before
	public void before() {
		ks = KeyStoreLoader.loadKeyStore("src/test/resources/xs2atestkeystore2keys.jks", null,
				"storepass".toCharArray(), false);
	}

	@Test
	public void testChangeKeyId() throws JOSEException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		assumeNotNull(ks);
		JWKSet exportKeys = JwkExport.exportPublicKeys(ks, keypass);
		List<JWK> keys = exportKeys.getKeys();
		for (JWK jwk : keys) {
			String expectedKeyId = jwk.computeThumbprint().toString().toLowerCase();
			assertTrue(!expectedKeyId.equals(jwk.getKeyID()));
		}
		boolean fixKeyId = FixKeyId.fixKeyId(ks, keypass);
		assertTrue(fixKeyId);
		exportKeys = JwkExport.exportPublicKeys(ks, keypass);
		keys = exportKeys.getKeys();
		for (JWK jwk : keys) {
			String expectedKeyId = jwk.computeThumbprint().toString().toLowerCase();
			assertTrue(expectedKeyId.equals(jwk.getKeyID()));
		}
	}

	@Test
	public void testIdempotent() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, JOSEException{
		assumeNotNull(ks);
		boolean fixKeyId = FixKeyId.fixKeyId(ks, keypass);
		assertTrue(fixKeyId);
		fixKeyId = FixKeyId.fixKeyId(ks, keypass);
		assertTrue(!fixKeyId);
	}
}
