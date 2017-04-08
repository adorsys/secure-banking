package org.adorsys.jjwk.selector;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

/**
 * Randomly pick a key pair among a list of key pairs of this server.
 * 
 * @author fpo
 *
 */
public class KeyPairRandomSelector {
	/**
	 * Select a random key by random picking a number between 0 (inclusive) and size exclusive;
	 * 
	 * @param serverKeys
	 * @return
	 */
    public static JWK randomKey(JWKSet serverKeys){
    	List<JWK> keys = serverKeys.getKeys();
    	int nextInt = RandomUtils.nextInt(0, keys.size());
    	return keys.get(nextInt);
    }

}
