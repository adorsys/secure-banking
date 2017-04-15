package org.adorsys.tmjv.key;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.security.auth.callback.CallbackHandler;

import org.adorsys.envutils.EnvPropPasswordCallbackHandler;
import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.keystore.JwkExport;
import org.adorsys.jkeygen.keystore.KeyStoreService;

public class ServerKeysProducer {

	private ServerKeys holder;

	@PostConstruct
	public void initCredentials() {
		
		String storeType = EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_STORE_TYPE", false);
		String keystoreFilename = EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_STORE_FILE", false);
		CallbackHandler keyStorePassCallbackHandler = new EnvPropPasswordCallbackHandler("TOKEN_MANAGER_KEY_STORE_PASSWORD");
		CallbackHandler keyPassCallbackHandler = new EnvPropPasswordCallbackHandler("TOKEN_MANAGER_KEY_ENTRY_PASSWORD");

		KeyStore keyStore;
		try {
			keyStore = KeyStoreService.loadKeyStore(new FileInputStream(keystoreFilename), "tmjv", storeType, keyStorePassCallbackHandler);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| IOException e) {
			throw new IllegalStateException(e);
		}
		holder = new ServerKeys(JwkExport.exportKeys(keyStore, keyPassCallbackHandler));
	}

	@Produces
	private ServerKeys getSignKeyHolder() {
		return holder;
	}
}
