package org.adorsys.encobject.service;

import java.util.UUID;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;

import org.adorsys.encobject.domain.ObjectInfo;
import org.adorsys.encobject.utils.TestFsBlobStoreFactory;
import org.adorsys.encobject.utils.TestKeyUtils;
import org.adorsys.jkeygen.keystore.SecretKeyData;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.jclouds.blobstore.BlobStoreContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;

public class ObjectInfoPersistenceTest {

	private static String container = KeystorePersistenceTest.class.getSimpleName();
	private static BlobStoreContext storeContext;
	private static ObjectInfoPersistence objectInfoPersistence;
	private static SecretKey secretKey;

	@BeforeClass
	public static void beforeClass(){
		TestKeyUtils.turnOffEncPolicy();
		storeContext = TestFsBlobStoreFactory.getTestBlobStoreContext();
		Assume.assumeNotNull(storeContext);
		objectInfoPersistence = new ObjectInfoPersistence(storeContext);
		
		char[] secretKeyPass = "aSimpleSecretPass".toCharArray();
		String secretKeyAlias = "mainKey";
		CallbackHandler secretKeyPassHandler = new PasswordCallbackHandler(secretKeyPass);

		SecretKeyData secretKeyData = TestKeyUtils.newSecretKey(secretKeyAlias, secretKeyPassHandler);
		Assume.assumeNotNull(secretKeyData);
		secretKey = secretKeyData.getSecretKey();
	}

	@AfterClass
	public static void afterClass(){
		storeContext.getBlobStore().deleteContainer(container);
		storeContext.close();
	} 

	@Test
	public void testStoreObject() {
		ObjectInfo objectInfo = new ObjectInfo();
		objectInfo.setChecksum("Sample checksum");
		objectInfo.setCompAlg("Gzip");
		objectInfo.setDesc("Test object info");
		objectInfo.setEncAlg("AES256");
		String handle = UUID.randomUUID().toString();
		objectInfoPersistence.storeObject(objectInfo, container, handle , secretKey, JWEAlgorithm.A256GCMKW.getName(), EncryptionMethod.A256GCM.getName());
		Assert.assertTrue(TestFsBlobStoreFactory.existsOnFs(container, handle));
	}

	@Test
	public void testLoadObjectInfo() {
		ObjectInfo objectInfo = new ObjectInfo();
		objectInfo.setChecksum("Sample checksum");
		objectInfo.setCompAlg("Gzip");
		objectInfo.setDesc("Test object info");
		objectInfo.setEncAlg("AES256");
		String handle = UUID.randomUUID().toString();
		objectInfoPersistence.storeObject(objectInfo, container, handle , secretKey, JWEAlgorithm.A256GCMKW.getName(), EncryptionMethod.A256GCM.getName());
		Assume.assumeTrue(TestFsBlobStoreFactory.existsOnFs(container, handle));
		
		try {
			ObjectInfo loadedObjectInfo = objectInfoPersistence.loadObjectInfo(container, handle, secretKey);
			Assert.assertTrue(objectInfo.equals(loadedObjectInfo));
		} catch (ObjectInfoNotFoundException | WrongKeyCredentialException e) {
			org.junit.Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLoadObjectInfoWrongKey() {
		ObjectInfo objectInfo = new ObjectInfo();
		objectInfo.setChecksum("Sample checksum");
		objectInfo.setCompAlg("Gzip");
		objectInfo.setDesc("Test object info");
		objectInfo.setEncAlg("AES256");
		String handle = UUID.randomUUID().toString();
		objectInfoPersistence.storeObject(objectInfo, container, handle , secretKey, JWEAlgorithm.A256GCMKW.getName(), EncryptionMethod.A256GCM.getName());
		Assume.assumeTrue(TestFsBlobStoreFactory.existsOnFs(container, handle));
		
		try {
			ObjectInfo loadedObjectInfo = objectInfoPersistence.loadObjectInfo(container, handle, secretKey);
			Assert.assertTrue(objectInfo.equals(loadedObjectInfo));
		} catch (ObjectInfoNotFoundException | WrongKeyCredentialException e) {
			org.junit.Assert.fail(e.getMessage());
		}
		
		char[] secretKeyPass = "aSimpleSecretPass".toCharArray();
		String secretKeyAlias = "secondKey";
		CallbackHandler secretKeyPassHandler = new PasswordCallbackHandler(secretKeyPass);
		SecretKeyData secondSecretKeyData = TestKeyUtils.newSecretKey(secretKeyAlias, secretKeyPassHandler);
		Assume.assumeNotNull(secondSecretKeyData);
		try {
			objectInfoPersistence.loadObjectInfo(container, handle, secondSecretKeyData.getSecretKey());
			org.junit.Assert.fail("Expecting a security exception");
		} catch (ObjectInfoNotFoundException e) {
			org.junit.Assert.fail("Not expecting this exception");
		} catch (WrongKeyCredentialException e) {
			// Noop
		}
		

	}
}
