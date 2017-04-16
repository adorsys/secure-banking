package org.adorsys.encobject.utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.security.KeyStore;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jjwk.keystore.JwkExport;
import org.adorsys.jkeygen.keystore.KeyStoreService;
import org.adorsys.jkeygen.keystore.KeystoreBuilder;
import org.adorsys.jkeygen.keystore.SecretKeyData;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.adorsys.jkeygen.pwd.PasswordMapCallbackHandler;
import org.adorsys.jkeygen.secretkey.SecretKeyBuilder;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

public class TestKeyUtils {
	
	public static void turnOffEncPolicy(){
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

	
	public static KeyStore testSecretKeystore(String storeName, char[] storePass, String secretKeyAlias, char[] secretKeyPass){
		try {
			CallbackHandler storePassHandler = new PasswordCallbackHandler(storePass);
			CallbackHandler secretKeyPassHandler = new PasswordCallbackHandler(secretKeyPass);
			
			byte[] bs = new KeystoreBuilder()
					.withKeyEntry(newSecretKey(secretKeyAlias, secretKeyPassHandler))
					.withStoreId(storeName)
					.build(storePassHandler);

			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			return KeyStoreService.loadKeyStore(bis, storeName, null, storePassHandler);
		} catch (Exception e) {
			return null;
		}
	}

	public static SecretKeyData newSecretKey(String alias, CallbackHandler secretKeyPassHandler){
		SecretKey secretKey = new SecretKeyBuilder().withKeyAlg("AES").withKeyLength(256).build();	
		return new SecretKeyData(secretKey, alias, secretKeyPassHandler);
	}
	
	public static PasswordMapCallbackHandler.Builder callbackHandlerBuilder(String secretKeyAlias, char[] secretKeyPass){
		return new PasswordMapCallbackHandler.Builder()
				.withEntry(secretKeyAlias, secretKeyPass);
	}
	
	public static JWK readKeyAsJWK(KeyStore keyStore, String alias, CallbackHandler callbackHandler){
		JWKSet exportKeys = JwkExport.exportKeys(keyStore, callbackHandler);
		return JwkExport.selectKey(exportKeys, alias);
	}
}
