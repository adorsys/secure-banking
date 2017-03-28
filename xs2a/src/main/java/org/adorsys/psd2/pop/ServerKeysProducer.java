package org.adorsys.psd2.pop;

import java.security.KeyStore;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;

import org.adorsys.psd2.xs2a.utils.EnvProperties;

import com.nimbusds.jose.jwk.JWKSet;

public class ServerKeysProducer {
	private ServerKeysHolder holder;

	@PostConstruct
	public void initCredentials() {
		String keyStoreFile = EnvProperties.getEnvProp("SERVER_KEY_STORE_FILE", false);
		String storeType = EnvProperties.getEnvProp("SERVER_KEY_STORE_TYPE", false);
		char[] keyStorePassword = EnvProperties.getEnvProp("SERVER_KEY_STORE_PASSWORD", false).toCharArray();
		char[] privateKeysPassword = EnvProperties.getEnvProp("SERVER_KEY_ENTRY_PASSWORD", false).toCharArray();

		KeyStore keyStore = KeyStoreLoader.loadKeyStore(keyStoreFile, storeType, keyStorePassword, false);
		FixKeyId.fixKeyId(keyStore, privateKeysPassword);
		JWKSet privateKeySet = JwkExport.exportPrivateKeys(keyStore, privateKeysPassword);
		JWKSet publicKeySet = privateKeySet.toPublicJWKSet();
		holder = new ServerKeysHolder(privateKeySet, publicKeySet);
	}

	@Produces
	private ServerKeysHolder getSignKeyHolder() {
		return holder;
	}
}
