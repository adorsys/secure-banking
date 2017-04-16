package org.adorsys.encobject.params;

import java.security.Key;

import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;

/**
 * {@link EncParamSelector} selects an encryption algorithm and an encryption method based on the 
 * key used for encryption.
 * 
 * @author fpo
 *
 */
public class EncParamSelector {
	public static EncryptionParams selectEncryptionParams(Key key) throws UnsupportedEncAlgorithmException{
		String algorithm = key.getAlgorithm();
		if(StringUtils.equalsAnyIgnoreCase("AES", algorithm)){
			return new EncryptionParams.Builder().setEncAlgo(JWEAlgorithm.A256GCMKW).setEncMethod(EncryptionMethod.A256GCM).build();
		}
		throw new UnsupportedEncAlgorithmException(algorithm);
	}
}
