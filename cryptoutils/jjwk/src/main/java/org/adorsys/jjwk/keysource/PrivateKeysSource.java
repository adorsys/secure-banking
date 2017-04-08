package org.adorsys.jjwk.keysource;

import com.nimbusds.jose.jwk.JWKSet;

/**
 * Plugin interface to load private keys used by a server to sign claims.
 * 
 * @author fpo
 *
 */
public interface PrivateKeysSource {
	
	/**
	 * Load the private keys from the given source
	 * 
	 * @return
	 */
	public JWKSet load();
}
