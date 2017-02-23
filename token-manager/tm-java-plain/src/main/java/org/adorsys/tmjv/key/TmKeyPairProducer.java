package org.adorsys.tmjv.key;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;

import org.adorsys.tmjv.env.EnvProperties;
import org.apache.commons.lang3.StringUtils;

public class TmKeyPairProducer {
	
	private TmKeyPair tmSignKey;
	
	@PostConstruct
	public void initCredentials(){
		
		String keyStoreFile = EnvProperties.getEnvProp("TOKEN_MANAGER_KEY_STORE_FILE", false);
		String storeType = EnvProperties.getEnvProp("TOKEN_MANAGER_KEY_STORE_TYPE", false);
		char[] keyStorePassword = EnvProperties.getEnvProp("TOKEN_MANAGER_KEY_STORE_PASSWORD", false).toCharArray();
		String signKeyAlias = EnvProperties.getEnvProp("TOKEN_MANAGER_KEY_ENTRY_ALIAS", false);
		char[] signKeyPassword = EnvProperties.getEnvProp("TOKEN_MANAGER_KEY_ENTRY_PASSWORD", false).toCharArray();

		KeyStore keyStore = loadKeyStore(keyStoreFile, storeType,keyStorePassword);
		try {
			RSAPrivateKey privateKey = (RSAPrivateKey)keyStore.getKey(signKeyAlias, signKeyPassword);
			Certificate[] certs = keyStore.getCertificateChain(signKeyAlias);
			
			if (privateKey == null || certs==null || certs.length==0)
				throw new IllegalStateException(
						"can not read token manager signing key. ");
			RSAPublicKey publicKey= (RSAPublicKey)certs[0].getPublicKey();
			tmSignKey = new TmKeyPair(privateKey,publicKey);
		} catch (UnrecoverableKeyException | KeyStoreException
				| NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Produces
	private TmKeyPair getTmSignKey(){
		return tmSignKey;
	}

	private KeyStore loadKeyStore(String keyStorFile, String storeType,
			char[] keyStorePassword) {
		try {
			if (StringUtils.isBlank(storeType))
				storeType = KeyStore.getDefaultType();
			KeyStore ks = KeyStore.getInstance(storeType);
			java.io.FileInputStream fis = null;
			try {
				fis = new java.io.FileInputStream(keyStorFile);
				ks.load(fis, keyStorePassword);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
			return ks;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
