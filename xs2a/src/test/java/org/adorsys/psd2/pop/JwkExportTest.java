package org.adorsys.psd2.pop;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.PasswordLookup;
import com.nimbusds.jose.util.Base64URL;

import junit.framework.Assert;

public class JwkExportTest {

	private static KeyStore ks;

	@BeforeClass
	public static void beforeClass() {
		ks = KeyStoreLoader.loadKeyStore("src/test/resources/xs2atestkeystore2keys.jks", null,
				"storepass".toCharArray(), false);
	}

	@Test
	public void testExportKeys() {
		assumeNotNull(ks);
		JWKSet exportKeys = JwkExport.exportPrivateKeys(ks, "keypass".toCharArray());
		List<JWK> keys = exportKeys.getKeys();
		assertNotNull(keys);
		assertTrue(keys.size() == 2);
		verbose(keys);
	}

	@Test
	public void testEexportPublicKeys() {
		assumeNotNull(ks);
		JWKSet exportKeys = JwkExport.exportPublicKeys(ks, "keypass".toCharArray());
		List<JWK> keys = exportKeys.getKeys();
		assertNotNull(keys);
		assertTrue(keys.size() == 2);
		verbose(keys);
	}

	private void verbose(List<JWK> keys) {
		for (JWK jwk : keys) {
			try {
				Base64URL thumbprint = jwk.computeThumbprint();
				String jsonString = thumbprint.toJSONString();
				String string = thumbprint.toString();
				BigInteger decodeToBigInteger = thumbprint.decodeToBigInteger();
				String decodeToString = thumbprint.decodeToString();
				
				String keyID = jwk.getKeyID();
				KeyType keyType = jwk.getKeyType();
				KeyUse keyUse = jwk.getKeyUse();
				Algorithm algorithm = jwk.getAlgorithm();
				Set<KeyOperation> keyOperations = jwk.getKeyOperations();
				boolean private1 = jwk.isPrivate();
				LinkedHashMap<String,?> requiredParams = jwk.getRequiredParams();
			} catch (JOSEException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
