package org.adorsys.jjwk.selector;

import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

public class JWEEncryptedSelector {

	public static JWEEncrypter geEncrypter(JWK jwk){
		try {
			if(jwk instanceof RSAKey){
				
				return new RSAEncrypter((RSAKey)jwk);
			} else if (jwk instanceof ECKey){
				return new ECDHEncrypter((ECKey)jwk);
			} else if (jwk instanceof OctetSequenceKey){
				OctetSequenceKey octJWK = (OctetSequenceKey) jwk;
				Algorithm algorithm = octJWK.getAlgorithm();
				if (StringUtils.equalsAnyIgnoreCase(algorithm.getName(), "dir")){
					return new DirectEncrypter(octJWK);				
				} else if (StringUtils.startsWithIgnoreCase(algorithm.getName(),"a")){
					return new AESEncrypter(octJWK); 
				}
			}
			throw new IllegalStateException("Unknown Algorithm");
		} catch (JOSEException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
