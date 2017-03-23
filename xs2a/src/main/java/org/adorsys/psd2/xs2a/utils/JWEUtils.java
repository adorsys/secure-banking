package org.adorsys.psd2.xs2a.utils;

import java.io.IOException;
import java.security.Key;
import java.text.ParseException;

import org.adorsys.psd2.common.domain.JweEncryptionSpec;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

public class JWEUtils {
	final static DefaultJWEDecrypterFactory decrypterFactory = new DefaultJWEDecrypterFactory();
//	final static DefaultJWE
	final static ObjectMapper mapper = new ObjectMapper();
	public static <T> T decrypt(String jweString, Class<T> modelKlass) {
		try {
			JWEObject jweObject = JWEObject.parse(jweString);
			JWEHeader header = jweObject.getHeader();
			String keyID = header.getKeyID();
			Key key = KeyResolver.resolve(keyID);
			JWEDecrypter jweDecrypter = decrypterFactory.createJWEDecrypter(header, key);
			jweObject.decrypt(jweDecrypter);
			Payload payload = jweObject.getPayload();
			String content = payload.toString();
			return mapper.readValue(content, modelKlass);
		} catch (ParseException | JOSEException | IOException e){
			throw new IllegalArgumentException(e);
		}
	}
	
	public static String encrypt(Object object, JweEncryptionSpec encSpec){
		try {
			JWEAlgorithm jweAlgorithm = JWEAlgorithm.parse(encSpec.getAlgo());
			EncryptionMethod encryptionMethod = EncryptionMethod.parse(encSpec.getEnc());
			JWK jwk = JWK.parse(encSpec.getKey());
			JWEHeader header = new JWEHeader.Builder(jweAlgorithm, encryptionMethod).build();
			JWEEncrypter encrypter = geEncrypter(jwk);
			byte[] valueAsBytes = mapper.writeValueAsBytes(object);
			JWEObject jweObject = new JWEObject(header,new Payload(valueAsBytes));
			jweObject.encrypt(encrypter);
			return jweObject.serialize();
		} catch (JsonProcessingException | ParseException | JOSEException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
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
