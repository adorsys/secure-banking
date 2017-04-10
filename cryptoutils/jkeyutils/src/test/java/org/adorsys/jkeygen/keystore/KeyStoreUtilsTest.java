package org.adorsys.jkeygen.keystore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.adorsys.jkeygen.keypair.KeyPairBuilder;
import org.adorsys.jkeygen.keypair.SelfSignedCertBuilder;
import org.adorsys.jkeygen.keypair.SelfSignedKeyPairData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class KeyStoreUtilsTest {
	private SelfSignedKeyPairData keyPairData;
	private char[] storePass = "FrancisKeystorePass".toCharArray();
	private char[] keyPass = "FrancisKeyPass".toCharArray();
	private String keyAlias = "FrancisKeyAlias";

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

	}

	
	@Test
	public void testCeateKeystore() {
		try {
			byte[] bs = createKeyStore(storePass, keyAlias, keyPass);
			Assert.assertNotNull(bs);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Assert.fail(e.getMessage());
		}
	}


	@Test
	public void testLoadKeystore() {
		byte[] bs = null;
		try {
			bs = createKeyStore(storePass, keyAlias, keyPass); 
			Assume.assumeNotNull(bs);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Assume.assumeNoException(e);
		}
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			KeyStore keyStore = KeyStoreUtils.loadKeyStore(bis, "FrancisKeyStore", null, new PasswordCallbackHandler(storePass));
			Assert.assertNotNull(keyStore);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test
	public void testBadStorePass() {
		byte[] bs = null;
		try {
			bs = createKeyStore(storePass, keyAlias, keyPass); 
			Assume.assumeNotNull(bs);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Assume.assumeNoException(e);
		}
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			char[] badStorePass = "WrongFrancisKeystorePass".toCharArray();
			KeyStoreUtils.loadKeyStore(bis, "FrancisKeyStore", null, new PasswordCallbackHandler(badStorePass));
			Assert.fail("Expecting UnrecoverableKeyException");
		} catch (IOException e) {
			// expected behavior
		}
	}	
	

	@Test
	public void testLoadKey() {
		byte[] bs = null;
		try {
			bs = createKeyStore(storePass, keyAlias, keyPass); 
			Assume.assumeNotNull(bs);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Assume.assumeNoException(e);
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		KeyStore keyStore = null;
		try {
			keyStore = KeyStoreUtils.loadKeyStore(bis, "FrancisKeyStore", null, new PasswordCallbackHandler(storePass));
		} catch (IOException e1) {
			// noopt
		}
		Assume.assumeNotNull(keyStore);

		try {
			Key key = keyStore.getKey(keyAlias, keyPass);
			Assert.assertNotNull(key);
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test
	public void testBadKeyPass() {
		byte[] bs = null;
		try {
			bs = createKeyStore(storePass, keyAlias, keyPass); 
			Assume.assumeNotNull(bs);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			Assume.assumeNoException(e);
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		KeyStore keyStore = null;
		try {
			keyStore = KeyStoreUtils.loadKeyStore(bis, "FrancisKeyStore", null, new PasswordCallbackHandler(storePass));
		} catch (IOException e1) {
			// noopt
		}
		Assume.assumeNotNull(keyStore);

		try {
			char[] badKeyPass = "WrongFrancisKeyPass".toCharArray();
			keyStore.getKey(keyAlias, badKeyPass);
			Assert.fail("Expecting UnrecoverableKeyException");
		} catch (UnrecoverableKeyException e){
			// Expected result
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			Assert.fail("Expecting UnrecoverableKeyException");
		}
	}	
	
	private byte[] createKeyStore(char[] storePass, String keyAlias, char[] keyPass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
		List<KeyPairStoreData> keyPairHolders = new ArrayList<KeyPairStoreData>();
		CallbackHandler passwordSource = new PasswordCallbackHandler(keyPass);

		KeyPairStoreData keyPairStoreData = new KeyPairStoreData(keyPairData, null, passwordSource, keyAlias);
		keyPairHolders.add(keyPairStoreData);

		KeyStore keyStore = KeyStoreUtils.newKeyStore(null);
		KeyStoreUtils.fillKeyStore(keyStore, keyPairHolders);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		keyStore.store(bos, storePass);
		return bos.toByteArray();
	}
	
	private static class PasswordCallbackHandler implements CallbackHandler {
		private char[] password;
		private PasswordCallbackHandler(char[] password) {
			if (password != null) {
				this.password = (char[]) password.clone();
			}
		}

		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			if (!(callbacks[0] instanceof PasswordCallback)) {
				throw new UnsupportedCallbackException(callbacks[0]);
			} else {
				PasswordCallback passwordCallback = (PasswordCallback) callbacks[0];
				passwordCallback.setPassword(this.password);
			}
		}

		protected void finalize() throws Throwable {
			if (this.password != null) {
				Arrays.fill(this.password, ' ');
			}
			super.finalize();
		}
	}

}
