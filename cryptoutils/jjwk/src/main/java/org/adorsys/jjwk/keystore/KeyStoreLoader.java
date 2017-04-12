package org.adorsys.jjwk.keystore;

import java.security.KeyStore;

/**
 * General interface for loading key store.
 * 
 * @author fpo
 *
 */
public interface KeyStoreLoader {
	public KeyStore loadKeyStore();
}
