package org.adorsys.jjwk.keystore;

/**
 * Holds params used to load a keystore and containing keys.
 * 
 * @author fpo
 *
 */
public interface KeyStoreParams {
	
	public String getKeystoreFilename();
	
	public String getStoreType();
	
	public PasswordSource getKeyStorePassword();
	
	public PasswordSource getKeyPassword();

}
