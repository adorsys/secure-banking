package org.adorsys.tmjv.key;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;

import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.keystore.FileBasedPrivateKeysSource;
import org.adorsys.jjwk.keystore.KeyStoreParams;
import org.adorsys.jjwk.keystore.PasswordSource;

import com.nimbusds.jose.jwk.JWKSet;

public class ServerKeysProducer {
	
	private ServerKeys holder;

	@PostConstruct
	public void initCredentials() {
		KeyStoreParams keyStoreParams = null;
		try {
			String keyStorFileName = EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_STORE_FILE", false);
			String storeType = EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_STORE_TYPE", false);

			keyStoreParams = new KeyStoreParams() {
				PasswordSource keyStorePasswordSrc = new PasswordSource() {
					char[] passwd;
					@Override
					public char[] getPassword(String entryName) {
						// ToDo Fix password shall not be read as a String.
						if(passwd==null)passwd=EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_STORE_PASSWORD", false).toCharArray();
						return passwd;
					}
					@Override
					public void cleanup() {
						if(passwd!=null)Arrays.fill(passwd, ' ');
						passwd = null;
					}
				};
				PasswordSource keyPasswordSrc = new PasswordSource() {
					char[] passwd;
					@Override
					public char[] getPassword(String entryName) {
						// ToDo Fix password shall not be read as a String.
						if(passwd==null)passwd=EnvProperties.getEnvOrSysProp("TOKEN_MANAGER_KEY_ENTRY_PASSWORD", false).toCharArray();
						return passwd;
					}
					@Override
					public void cleanup() {
						if(passwd!=null)Arrays.fill(passwd, ' ');
						passwd = null;
					}
				};

				@Override
				public String getStoreType() {
					return storeType;
				}
				
				@Override
				public String getKeystoreFilename() {
					return keyStorFileName;
				}
				
				@Override
				public PasswordSource getKeyStorePassword() {
					return keyStorePasswordSrc;
				}
				
				@Override
				public PasswordSource getKeyPassword() {
					return keyPasswordSrc;
				}
			};
			FileBasedPrivateKeysSource keysSource = new FileBasedPrivateKeysSource(keyStoreParams, false);
			JWKSet keys = keysSource.load();
			holder = new ServerKeys(keys);
		} finally {
			if(keyStoreParams!=null){
				keyStoreParams.getKeyPassword().cleanup();
				keyStoreParams.getKeyStorePassword().cleanup();
			}
		}
	}

	@Produces
	private ServerKeys getSignKeyHolder() {
		return holder;
	}
}
