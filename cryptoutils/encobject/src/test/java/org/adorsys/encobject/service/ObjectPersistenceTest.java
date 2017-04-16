package org.adorsys.encobject.service;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.encobject.domain.ContentMetaInfo;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.params.EncryptionParams;
import org.adorsys.encobject.utils.TestFsBlobStoreFactory;
import org.adorsys.encobject.utils.TestKeyUtils;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.jclouds.blobstore.BlobStoreContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;

public class ObjectPersistenceTest {

	private static String container = KeystorePersistenceTest.class.getSimpleName();
	private static BlobStoreContext storeContext;
	private static ObjectPersistence objectInfoPersistence;
	private static KeyStore keyStore;
	private static CallbackHandler secretKeyPassHandler;
	private static String secretKeyAlias = "mainKey";
	private static EncryptionParams encParams;
	
	@BeforeClass
	public static void beforeClass(){
		TestKeyUtils.turnOffEncPolicy();
		storeContext = TestFsBlobStoreFactory.getTestBlobStoreContext();
		Assume.assumeNotNull(storeContext);
		objectInfoPersistence = new ObjectPersistence(storeContext);
		
		char[] secretKeyPass = "aSimpleSecretPass".toCharArray();
		
		String storeAlias = "mainKeyStore";
		char[] storePass = "aSimpleStorePass".toCharArray();
		keyStore = TestKeyUtils.testSecretKeystore(storeAlias, storePass, secretKeyAlias, secretKeyPass);
		Assume.assumeNotNull(keyStore);
		secretKeyPassHandler = new PasswordCallbackHandler(secretKeyPass);
		encParams = new EncryptionParams.Builder()
				.setEncAlgo(JWEAlgorithm.A256GCMKW)
				.setEncMethod(EncryptionMethod.A256GCM).build();
		
	}

	@AfterClass
	public static void afterClass(){
		storeContext.getBlobStore().deleteContainer(container);
		storeContext.close();
	} 

	@Test
	public void testStoreObject() throws UnsupportedEncAlgorithmException, UnsupportedEncodingException, WrongKeyCredentialException, UnsupportedKeyLengthException {
		byte[] data = "This is a sample String to be stored.".getBytes("UTF-8");
		ContentMetaInfo metaIno = null;
		String name = UUID.randomUUID().toString();
		ObjectHandle handle = new ObjectHandle(container, name);
		objectInfoPersistence.storeObject(data, metaIno, handle, keyStore, secretKeyAlias, secretKeyPassHandler, encParams);
		Assert.assertTrue(TestFsBlobStoreFactory.existsOnFs(container, name));
	}

	@Test
	public void testLoadObjectInfo() {
		String dataStr = "This is a sample String to be stored.";
		ContentMetaInfo metaIno = null;
		String name = UUID.randomUUID().toString();
		ObjectHandle handle = new ObjectHandle(container, name);
		try {
			objectInfoPersistence.storeObject(dataStr.getBytes("UTF-8"), metaIno, handle, keyStore, secretKeyAlias, secretKeyPassHandler, encParams);
		} catch (UnsupportedEncodingException | UnsupportedEncAlgorithmException | WrongKeyCredentialException | UnsupportedKeyLengthException e) {
			Assume.assumeNoException(e);
		}
		Assume.assumeTrue(TestFsBlobStoreFactory.existsOnFs(container, name));
		
		try {
			byte[] bs = objectInfoPersistence.loadObject(new ObjectHandle(container, name), keyStore, secretKeyPassHandler);
			String byteStr = new String(bs, "UTF-8");
			Assert.assertEquals(dataStr, byteStr);
		} catch (ObjectNotFoundException | WrongKeyCredentialException | UnsupportedEncodingException e) {
			org.junit.Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLoadObjectInfoWrongKey() {
		String dataStr = "This is a sample String to be stored.";
		ContentMetaInfo metaIno = null;
		String name = UUID.randomUUID().toString();
		ObjectHandle handle = new ObjectHandle(container, name);
		try {
			objectInfoPersistence.storeObject(dataStr.getBytes("UTF-8"), metaIno, handle, keyStore, secretKeyAlias, secretKeyPassHandler, encParams);
		} catch (UnsupportedEncodingException | UnsupportedEncAlgorithmException | WrongKeyCredentialException | UnsupportedKeyLengthException e) {
			Assume.assumeNoException(e);
		}
		Assume.assumeTrue(TestFsBlobStoreFactory.existsOnFs(container, name));

		try {
			byte[] bs = objectInfoPersistence.loadObject(new ObjectHandle(container, name), keyStore, secretKeyPassHandler);
			String byteStr = new String(bs, "UTF-8");
			Assert.assertEquals(dataStr, byteStr);
		} catch (ObjectNotFoundException | WrongKeyCredentialException | UnsupportedEncodingException e) {
			org.junit.Assert.fail(e.getMessage());
		}

		
		char[] wrongKeyPass = "wrongSecretPass".toCharArray();
		CallbackHandler secondSecretKeyPassHandler = new PasswordCallbackHandler(wrongKeyPass);
		KeyStore ks = TestKeyUtils.testSecretKeystore("wrongStore", "wrongStorePass".toCharArray(), secretKeyAlias, wrongKeyPass);
		
		try {
			objectInfoPersistence.loadObject(new ObjectHandle(container, name), ks, secondSecretKeyPassHandler);
			org.junit.Assert.fail("Expecting a security exception");
		} catch (ObjectNotFoundException e) {
			org.junit.Assert.fail("Not expecting this exception");
		} catch (WrongKeyCredentialException e) {
			// Noop. COrrect outcome
		} catch(RuntimeException e){
			org.junit.Assert.fail("Not expecting this exception");
		}
		

	}
}
