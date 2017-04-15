package org.adorsys.jkeygen.secretkey;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.adorsys.jkeygen.utils.ProviderUtils;
import org.adorsys.jkeygen.validation.BatchValidator;
import org.adorsys.jkeygen.validation.KeyValue;
import org.adorsys.jkeygen.validation.ListOfKeyValueBuilder;

public class SecretKeyBuilder {

	private static Provider provider = ProviderUtils.bcProvider;
	
	private Integer keyLength;
	private String keyAlg;
	
	/**
	 * Returns the message key pair subject certificate holder.
	 * 
	 * Following entity must be validated  
	 * 
	 * @return KeyPairAndCertificateHolder
	 */
	boolean dirty = false;
	public SecretKey build() {
		if(dirty)throw new IllegalStateException("Builder can not be reused");
		dirty=true;
		List<KeyValue> notNullCheckList = ListOfKeyValueBuilder.newBuilder()
				.add("keyAlg", keyAlg)
				.add("keyLength", keyLength)
				.build();
		List<String> nullList = BatchValidator.filterNull(notNullCheckList);
		if(nullList!=null && !nullList.isEmpty()){
			throw new IllegalArgumentException("Fields can not be null: " + nullList);
		}
		KeyGenerator kGen;
		try {
			kGen = KeyGenerator.getInstance(keyAlg, provider);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}

		kGen.init(keyLength);
		return kGen.generateKey();
	}

	public SecretKeyBuilder withKeyLength(Integer keyLength) {
		this.keyLength = keyLength;
		return this;
	}

	public SecretKeyBuilder withKeyAlg(String keyAlg) {
		this.keyAlg = keyAlg;
		return this;
	}

}
