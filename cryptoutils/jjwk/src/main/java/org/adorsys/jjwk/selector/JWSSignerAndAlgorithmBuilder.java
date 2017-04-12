package org.adorsys.jjwk.selector;

import java.util.LinkedHashSet;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jca.JCASupport;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

/**
 * Base on the key type and key size, selects a signature algorithm and instantiates the 
 * corresponding signer.
 * 
 * This is only for asymetric signature so we do not support HMAC
 * 
 * For the moment, only RS256 and ES256 are supported. Will be extended to support
 * RS384, ES384, RS512 and ES512 
 * 
 * @author fpo
 *
 */
public class JWSSignerAndAlgorithmBuilder {
	
	public static JWSSignerAndAlgorithm build(JWK jwk) {
		return build(jwk, null);
	}

	/**
	 * Select a signer and an algorithm for use.
	 * 
	 * @param jwk
	 * @param algoPrefs
	 * @return
	 */
	public static JWSSignerAndAlgorithm build(JWK jwk, LinkedHashSet<JWSAlgorithm> algoPrefs) {
	    JWSSigner signer = null;
	    JWSAlgorithm jwsAlgorithm = null;
		try {
			// First check to see if JWK brings necessary algo information.
			Algorithm alg = jwk.getAlgorithm();
			if(alg!=null){
				if(alg instanceof JWSAlgorithm) {
					jwsAlgorithm = (JWSAlgorithm) alg;
				} else {
					throw new IllegalArgumentException("Provided JWK does not contain an algoritm");				
				}
			}
			
			// The auto select algo and instantiate corresponding signer.
			if(jwk instanceof RSAKey){
				if(algoPrefs==null) algoPrefs = JWSAlgorithm.Family.RSA;
				jwsAlgorithm = selectAlgoPref(JWSAlgorithm.Family.RSA, algoPrefs);
				if(jwsAlgorithm!=null)signer = new RSASSASigner((RSAKey) jwk);
			} else if (jwk instanceof ECKey) {
				if(algoPrefs==null) algoPrefs = JWSAlgorithm.Family.EC;
				jwsAlgorithm = selectAlgoPref(JWSAlgorithm.Family.EC, algoPrefs);
				if(jwsAlgorithm!=null)signer = new ECDSASigner((ECKey) jwk);
			} else if (jwk instanceof OctetSequenceKey){
				if(algoPrefs==null) algoPrefs = JWSAlgorithm.Family.HMAC_SHA;
				jwsAlgorithm = selectAlgoPref(JWSAlgorithm.Family.HMAC_SHA, algoPrefs);
				if(jwsAlgorithm!=null)signer = new MACSigner((OctetSequenceKey) jwk);
			} else {
				throw new IllegalStateException("Key provided is not a signature key. Key must be of type JWS RSAKey or ECKey or OctetSequenceKey");
			}
			
			if(signer==null){
				throw new IllegalStateException("Key provided is not an assymetric key. This framwork consumes either RSA od DSA keys.");
	        }
		} catch (JOSEException ex){
			throw new IllegalStateException(ex);
		}
		
		return new JWSSignerAndAlgorithm(signer, jwsAlgorithm);
	}
	
	private static JWSAlgorithm selectAlgoPref(JWSAlgorithm.Family family, LinkedHashSet<JWSAlgorithm> algoPrefs){
		for (JWSAlgorithm algPref : algoPrefs) {
			if(family.contains(algPref)){
				if(JCASupport.isSupported(algPref)){
					return algPref;
				}
			}
		}
		return null;
	}
}
