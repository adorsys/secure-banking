package org.adorsys.jkeygen.keystore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jkeygen.keypair.KeyPairBuilder;
import org.adorsys.jkeygen.keypair.SelfSignedCertBuilder;
import org.adorsys.jkeygen.keypair.SelfSignedKeyPairData;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.adorsys.jkeygen.secretkey.SecretKeyBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyStoreServiceTest {
	private static final String KEY_STORE_NAME = "FrancisKeyStore";
	private static final char[] storePass = "FrancisKeystorePass".toCharArray();

	private static final char[] keyPairPass = "FrancisKeyPairPass".toCharArray();
	private static final String keyPairAlias = "FrancisKeyPairAlias";

	private static final char[] secretKeyPass = "FrancisSecretKeyPass".toCharArray();
	private static final String secretKeyAlias = "FrancisSecretKeyAlias";
	
	private SelfSignedKeyPairData keyPairData;
	
	private SecretKey secretKey; 
	
	@BeforeClass
	public static void beforeClass(){
		// Warning: do not do this for productive code. Download and install the jce unlimited strength policy file
		// see http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
		try {
	        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
	        field.setAccessible(true);
	        field.set(null, java.lang.Boolean.FALSE);
	    } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
	        ex.printStackTrace(System.err);
	    }		
	}
	
	@Before
	public void before() {
		KeyPair keyPair = new KeyPairBuilder().withKeyAlg("RSA").withKeyLength(2048).build();
		Assume.assumeNotNull(keyPair);
		
		X500Name cn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Francis Pouatcha").build();
		keyPairData = new SelfSignedCertBuilder().withSubjectDN(cn)
				.withSignatureAlgo("SHA256withRSA").withNotAfterInDays(300).withCa(false).build(keyPair);
		Assume.assumeNotNull(keyPairData);
		Assume.assumeNotNull(keyPairData.getKeyPair());
		Assume.assumeNotNull(keyPairData.getSubjectCert());
		
		secretKey = new SecretKeyBuilder().withKeyAlg("AES").withKeyLength(256).build();
		Assume.assumeNotNull(secretKey);

	}
	
	@Test
	public void testCeateKeystore() {
		CallbackHandler keyPassHandler = new PasswordCallbackHandler(keyPairPass);
		CallbackHandler storePassHandler = new PasswordCallbackHandler(storePass);

		KeyPairData keyPairStoreData = new KeyPairData(keyPairData, null, keyPairAlias, keyPassHandler);

		CallbackHandler secretKeyPassHandler = new PasswordCallbackHandler(secretKeyPass);
		SecretKeyData secretKeyData = new SecretKeyData(secretKey, secretKeyAlias, secretKeyPassHandler);
		try {
			new KeystoreBuilder()
				.withKeyEntry(keyPairStoreData)
				.withKeyEntry(secretKeyData)
				.withStoreId("sampleKeystore")
				.build(storePassHandler);
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			Assert.fail(e.getMessage());
		}
	}


	@Test
	public void testLoadKeystore(){
		byte[] bs = createKeyStore();
		Assume.assumeNotNull(bs);

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			KeyStore keyStore = KeyStoreService.loadKeyStore(bis, KEY_STORE_NAME, null, new PasswordCallbackHandler(storePass));
			Assert.assertNotNull(keyStore);
		} catch (IOException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test
	public void testBadStorePass(){
		byte[] bs = createKeyStore();
		Assume.assumeNotNull(bs);

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			char[] badStorePass = "WrongFrancisKeystorePass".toCharArray();
			KeyStoreService.loadKeyStore(bis, KEY_STORE_NAME, null, new PasswordCallbackHandler(badStorePass));
			Assert.fail("Expecting UnrecoverableKeyException");
		} catch (UnrecoverableKeyException e) {
			// expected behavior
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Assert.fail("Expecting UnrecoverableKeyException");
		}
	}	

	@Test
	public void testLoadKeyPair(){
		byte[] bs = createKeyStore();
		Assume.assumeNotNull(bs);

		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		KeyStore keyStore = null;
		try {
			keyStore = KeyStoreService.loadKeyStore(bis, KEY_STORE_NAME, null, new PasswordCallbackHandler(storePass));
		} catch (IOException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			// noopt
		}
		Assume.assumeNotNull(keyStore);

		try {
			Key key = keyStore.getKey(keyPairAlias, keyPairPass);
			Assert.assertNotNull(key);
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			Assert.fail(e.getMessage());
		}
	}	

	@Test
	public void testLoadSecretKey(){
		byte[] bs = createKeyStore();
		Assume.assumeNotNull(bs);

		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		KeyStore keyStore = null;
		try {
			keyStore = KeyStoreService.loadKeyStore(bis, KEY_STORE_NAME, null, new PasswordCallbackHandler(storePass));
		} catch (IOException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			// noopt
		}
		Assume.assumeNotNull(keyStore);

		try {
			Key key = keyStore.getKey(secretKeyAlias, secretKeyPass);
			Assert.assertNotNull(key);
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test
	public void testBadKeyPairPass(){
		byte[] bs = createKeyStore();
		Assume.assumeNotNull(bs);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		KeyStore keyStore = null;
		try {
			keyStore = KeyStoreService.loadKeyStore(bis, KEY_STORE_NAME, null, new PasswordCallbackHandler(storePass));
		} catch (IOException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException e1) {
			// noopt
		}
		Assume.assumeNotNull(keyStore);

		try {
			char[] badKeyPass = "WrongFrancisKeyPass".toCharArray();
			keyStore.getKey(keyPairAlias, badKeyPass);
			Assert.fail("Expecting UnrecoverableKeyException");
		} catch (UnrecoverableKeyException e){
			// Expected result
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			Assert.fail("Expecting UnrecoverableKeyException");
		}
	}	

	
	@Test
	public void testBadSecretKeyPass(){
		byte[] bs = createKeyStore();
		Assume.assumeNotNull(bs);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		KeyStore keyStore = null;
		try {
			keyStore = KeyStoreService.loadKeyStore(bis, KEY_STORE_NAME, null, new PasswordCallbackHandler(storePass));
		} catch (IOException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException e1) {
			// noopt
		}
		Assume.assumeNotNull(keyStore);

		try {
			char[] badKeyPass = "WrongFrancisSecretKeyPass".toCharArray();
			keyStore.getKey(secretKeyAlias, badKeyPass);
			Assert.fail("Expecting UnrecoverableKeyException");
		} catch (UnrecoverableKeyException e){
			// Expected result
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			Assert.fail("Expecting UnrecoverableKeyException");
		}
	}	
	
	private byte[] createKeyStore() {
		CallbackHandler keyPassHandler = new PasswordCallbackHandler(keyPairPass);
		CallbackHandler storePassHandler = new PasswordCallbackHandler(storePass);

		KeyPairData keyPairStoreData = new KeyPairData(keyPairData, null, keyPairAlias, keyPassHandler);

		CallbackHandler secretKeyPassHandler = new PasswordCallbackHandler(secretKeyPass);
		SecretKeyData secretKeyData = new SecretKeyData(secretKey, secretKeyAlias, secretKeyPassHandler);

		try {
			return new KeystoreBuilder()
					.withKeyEntry(keyPairStoreData)
					.withKeyEntry(secretKeyData)
					.withStoreId("sampleKeystore")
					.build(storePassHandler);
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			return null;
		}
	}
}
