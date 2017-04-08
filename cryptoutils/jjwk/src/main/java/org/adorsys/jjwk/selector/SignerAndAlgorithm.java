package org.adorsys.jjwk.selector;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

/**
 * Base on the key type, selects a signature algorithm and instantiates the 
 * corresponding signer.
 * 
 * For the moment we do not support hmac.
 * 
 * For the moment, only RS256 and ES256 are supported. Will be extended to support
 * RS384, ES384, RS512 and ES512 
 * 
 * @author fpo
 *
 */
public class SignerAndAlgorithm {
	
    private final JWSSigner signer;
    private final JWSAlgorithm jwsAlgorithm;
	public SignerAndAlgorithm(JWK jwk) {
		try {
	        if(jwk instanceof RSAKey){
	        	jwsAlgorithm = JWSAlgorithm.RS256;
	        	signer = new RSASSASigner((RSAKey) jwk);
	        } else if (jwk instanceof ECKey){
	        	jwsAlgorithm = JWSAlgorithm.ES256;
	        	signer = new ECDSASigner((ECKey) jwk);
	        } else {
				throw new IllegalStateException("Key provided is not an assymetric key. This framwork consumes either RSA od DSA keys.");
	        }
		} catch (JOSEException ex){
			throw new IllegalStateException(ex);
		}
	}
	public JWSSigner getSigner() {
		return signer;
	}
	public JWSAlgorithm getJwsAlgorithm() {
		return jwsAlgorithm;
	}
}
