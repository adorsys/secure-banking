package org.adorsys.jjwk.selector;

import java.security.Key;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

public class JWEEncryptedSelector {
	
	public static JWEEncrypter geEncrypter(Key key, JWEAlgorithm encAlgo, EncryptionMethod encMethod) throws UnsupportedEncAlgorithmException, UnsupportedKeyLengthException{
		if(key instanceof RSAPublicKey) return new RSAEncrypter((RSAPublicKey) key);
		if(key instanceof ECPublicKey){
			try {
				return new ECDHEncrypter((ECPublicKey) key);
			} catch (JOSEException e) {
				throw new UnsupportedEncAlgorithmException(e.getMessage(),e);
			}
		}
		if(key instanceof SecretKey){
			if(AESEncrypter.SUPPORTED_ALGORITHMS.contains(encAlgo) && AESEncrypter.SUPPORTED_ENCRYPTION_METHODS.contains(encMethod)){
				try {
					return new AESEncrypter((SecretKey) key);
				} catch (KeyLengthException e) {
					throw new UnsupportedKeyLengthException(e.getMessage(), e);
				} 
			}

			if(DirectEncrypter.SUPPORTED_ALGORITHMS.contains(encAlgo) && DirectEncrypter.SUPPORTED_ENCRYPTION_METHODS.contains(encMethod)){
				try {
					return new DirectEncrypter((SecretKey) key);
				} catch (KeyLengthException e) {
					throw new UnsupportedKeyLengthException(e.getMessage(), e);
				}				
			}
		}
		throw new UnsupportedEncAlgorithmException("Unknown Algorithm");
	}
	
	
	public static JWEEncrypter geEncrypter(JWK jwk, JWEAlgorithm encAlgo, EncryptionMethod encMethod) throws UnsupportedEncAlgorithmException, KeyExtractionException, UnsupportedKeyLengthException{
		if(jwk instanceof RSAKey){ 
			try {
				return new RSAEncrypter((RSAKey) jwk);
			} catch (JOSEException e) {
				throw new KeyExtractionException(e.getMessage(), e);
			}
		}
		
		if (jwk instanceof ECKey){
			try {
				return new ECDHEncrypter((ECKey)jwk);
			} catch (JOSEException e) {
				throw new UnsupportedEncAlgorithmException(e.getMessage(),e);
			}
		}

		if (jwk instanceof OctetSequenceKey){
			OctetSequenceKey octJWK = (OctetSequenceKey) jwk;
			if(AESEncrypter.SUPPORTED_ALGORITHMS.contains(encAlgo) && AESEncrypter.SUPPORTED_ENCRYPTION_METHODS.contains(encMethod)){
				try {
					return new AESEncrypter(octJWK);
				} catch (KeyLengthException e) {
					throw new UnsupportedKeyLengthException(e.getMessage(), e);
				}
			}

			if(DirectEncrypter.SUPPORTED_ALGORITHMS.contains(encAlgo) && DirectEncrypter.SUPPORTED_ENCRYPTION_METHODS.contains(encMethod)){
				try {
					return new DirectEncrypter(octJWK);
				} catch (KeyLengthException e) {
					throw new UnsupportedKeyLengthException(e.getMessage(), e);
				}
			}
		}

		throw new UnsupportedEncAlgorithmException("Unknown Algorithm");
	}
}
