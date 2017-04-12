package org.adorsys.jjwk.keystore;

import javax.security.auth.callback.CallbackHandler;

/**
 * Holds params used to load a keystore and containing keys.
 * 
 * @author fpo
 *
 */
public interface KeyStoreParams {
	
	public String getKeystoreFilename();
	
	public String getStoreType();
	
	public CallbackHandler getKeyStorePassCallbackHandler();
	
	public CallbackHandler getKeyPassCallbackHandler();

}
