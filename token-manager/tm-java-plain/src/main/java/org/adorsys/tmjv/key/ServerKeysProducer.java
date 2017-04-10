package org.adorsys.tmjv.key;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.security.auth.callback.CallbackHandler;

import org.adorsys.envutils.EnvPropPasswordCallbackHandler;
import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.keystore.FileBasedPrivateKeysSource;
import org.adorsys.jjwk.keystore.KeyStoreParams;

import com.nimbusds.jose.jwk.JWKSet;

public class ServerKeysProducer {

	private ServerKeys holder;

	@PostConstruct
	public void initCredentials() {
		KeyStoreParams keyStoreParams = new KeyStoreParams() {

			@Override
			public String getStoreType() {
				return EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_STORE_TYPE", false);
			}

			@Override
			public String getKeystoreFilename() {
				return EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_STORE_FILE", false);
			}

			@Override
			public CallbackHandler getKeyStorePassCallbackHandler() {
				return new EnvPropPasswordCallbackHandler("TOKEN_MANAGER_KEY_STORE_PASSWORD");
			}

			@Override
			public CallbackHandler getKeyPassCallbackHandler() {
				return new EnvPropPasswordCallbackHandler("TOKEN_MANAGER_KEY_ENTRY_PASSWORD");
			}
		};

		FileBasedPrivateKeysSource keysSource = new FileBasedPrivateKeysSource(keyStoreParams);
		JWKSet keys = keysSource.load();
		holder = new ServerKeys(keys);
	}

	@Produces
	private ServerKeys getSignKeyHolder() {
		return holder;
	}
}
