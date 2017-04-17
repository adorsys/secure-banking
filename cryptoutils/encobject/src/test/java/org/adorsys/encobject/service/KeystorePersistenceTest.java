package org.adorsys.encobject.service;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.utils.TestFsBlobStoreFactory;
import org.adorsys.encobject.utils.TestKeyUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeystorePersistenceTest {
	private static String container = KeystorePersistenceTest.class.getSimpleName();
	private static BlobStoreContextFactory storeContextFactory;
	private static KeystorePersistence keystorePersistence;
	private static ContainerPersistence containerPersistence;

	@BeforeClass
	public static void beforeClass(){
		TestKeyUtils.turnOffEncPolicy();
		storeContextFactory = new TestFsBlobStoreFactory();
		keystorePersistence = new KeystorePersistence(storeContextFactory);
		containerPersistence = new ContainerPersistence(storeContextFactory);
		
		try {
			containerPersistence.creteContainer(container);
		} catch (ContainerExistsException e) {
			Assume.assumeNoException(e);
		}

	}
	
	@AfterClass
	public static void afterClass(){
		try {
			if(containerPersistence!=null && containerPersistence.containerExists(container))
				containerPersistence.deleteContainer(container);
		} catch (UnknownContainerException e) {
			Assume.assumeNoException(e);
		}
	} 

	@Test
	public void testStoreKeystore() throws NoSuchAlgorithmException, CertificateException, UnknownContainerException {
		String storeid = "sampleKeyStorePersistence";
		char[] storePass = "aSimplePass".toCharArray();
		KeyStore keystore = TestKeyUtils.testSecretKeystore(storeid, storePass, "mainKey", "aSimpleSecretPass".toCharArray());
		Assume.assumeNotNull(keystore);
		keystorePersistence.saveKeyStore(keystore, TestKeyUtils.callbackHandlerBuilder(storeid, storePass).build(), new ObjectHandle(container, storeid));
		Assert.assertTrue(TestFsBlobStoreFactory.existsOnFs(container, storeid));
	}
	
	@Test
	public void testLoadKeystore(){
		String container = "KeystorePersistenceTest";
		String storeid = "sampleKeyStorePersistence";
		char[] storePass = "aSimplePass".toCharArray();
		KeyStore keystore = TestKeyUtils.testSecretKeystore(storeid, storePass, "mainKey", "aSimpleSecretPass".toCharArray());
		Assume.assumeNotNull(keystore);
		try {
			keystorePersistence.saveKeyStore(keystore, TestKeyUtils.callbackHandlerBuilder(storeid, storePass).build(), new ObjectHandle(container, storeid));
		} catch (NoSuchAlgorithmException | CertificateException | UnknownContainerException e) {
			Assume.assumeNoException(e);
		}
		
		KeyStore loadedKeystore = null;
		try {
			loadedKeystore = keystorePersistence.loadKeystore(new ObjectHandle(container, storeid), TestKeyUtils.callbackHandlerBuilder(storeid, storePass).build());
		} catch (CertificateException | ObjectNotFoundException | WrongKeystoreCredentialException
				| MissingKeystoreAlgorithmException | MissingKeystoreProviderException | MissingKeyAlgorithmException
				| IOException | UnknownContainerException e) {
			Assume.assumeNoException(e);
		}
		Assert.assertNotNull(loadedKeystore);
		Key key = null;
		try {
			key = loadedKeystore.getKey("mainKey", "aSimpleSecretPass".toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(key);
		
	}

}
