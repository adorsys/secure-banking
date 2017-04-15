package org.adorsys.jjwk.keystore;

import java.security.KeyStore;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;

public class JwkExportTest {
		
	private static KeyStore keyStore;
	
	@BeforeClass
	public static void beforeClass(){
		Ktsu.turnOffEncPolicy();
		keyStore = Ktsu.testKeystore();
	}

	@Test
	public void testPositivExportAllKeys() {
		CallbackHandler callbackHandler = Ktsu.callbackHandlerBuilder().build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
		Assert.assertNotNull(jwkSet);
		// 7 keys. 4 key pairs and 3 private keys
		Assert.assertEquals(7, jwkSet.getKeys().size());
	}

	@Test
	public void testPositiveExportPublicKey() {
		CallbackHandler callbackHandler = Ktsu.callbackHandlerBuilder().build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
		Assume.assumeNotNull(jwkSet);
		JWKSet publicKeys = JwkExport.exportPublicKeys(jwkSet);
		Assert.assertNotNull(publicKeys);
		// 4 public keys from 4 key pais
		Assert.assertEquals(4, publicKeys.getKeys().size());
	}

	@Test
	public void testWrongKeypairPass() {
		CallbackHandler callbackHandler = 
				Ktsu.callbackHandlerBuilder()
				// wrong key pair pass
				.withEntry(Ktsu.keyPairAlias, "keyPairPass".toCharArray())
				.build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
		Assert.assertNotNull(jwkSet);
		// Secret key is exported
		Assert.assertEquals(6, jwkSet.getKeys().size());
	}

	@Test
	public void testWrongSecretKeyPass() {
		CallbackHandler callbackHandler = 
				Ktsu.callbackHandlerBuilder()
				// wrong key pair pass
				.withEntry(Ktsu.secretKeyAlias, "secretKeyPass".toCharArray())
				.build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
		Assert.assertNotNull(jwkSet);
		// Secret key is exported
		Assert.assertEquals(6, jwkSet.getKeys().size());
	}

	@Test
	public void test2WrongSecrets() {
		CallbackHandler callbackHandler = 
				Ktsu.callbackHandlerBuilder()
					.withEntry(Ktsu.keyPairAlias, "keyPairPass".toCharArray())
					.withEntry(Ktsu.secretKeyAlias, "secretKeyPass".toCharArray())
			.build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
		Assert.assertNotNull(jwkSet);
		// Secret key is exported
		Assert.assertEquals(5, jwkSet.getKeys().size());
	}
	
	@Test
	public void testSelectSIgnatureKeys() {
		CallbackHandler callbackHandler = Ktsu.callbackHandlerBuilder().build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
    	JWKMatcher signKeys = new JWKMatcher.Builder().keyUse(KeyUse.SIGNATURE).build();
    	List<JWK> keys = new JWKSelector(signKeys).select(jwkSet);
    	// We have 4 key pairs in the test keystore.
    	Assert.assertEquals(4, keys.size());
	}

	@Test
	public void testSelectSIgnatureKeysNative() {
		CallbackHandler callbackHandler = Ktsu.callbackHandlerBuilder().build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
		Assume.assumeNotNull(jwkSet);
		List<JWK> signKeys = JwkExport.selectSignKeys(jwkSet);
    	// We have 4 key pairs in the test keystore.
    	Assert.assertEquals(4, signKeys.size());
	}
	
	@Test
	public void testSelectKeyPairs() {
		CallbackHandler callbackHandler = Ktsu.callbackHandlerBuilder().build();
		JWKSet jwkSet = JwkExport.exportKeys(keyStore, callbackHandler);
		Assume.assumeNotNull(jwkSet);
		List<JWK> keypairs = JwkExport.selectKeypairs(jwkSet);
		Assert.assertNotNull(keypairs);
		// 4 public keys from 4 key pais
		Assert.assertEquals(4, keypairs.size());
	}

	
}
