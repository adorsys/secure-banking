package org.adorsys.jkeygen.keypair;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.List;

import org.adorsys.jkeygen.utils.ProviderUtils;
import org.adorsys.jkeygen.validation.BatchValidator;
import org.adorsys.jkeygen.validation.KeyValue;
import org.adorsys.jkeygen.validation.ListOfKeyValueBuilder;

/**
 * Instantiates and returns a key pair certificate.
 * 
 * @author fpo
 *
 */
public class KeyPairBuilder {

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
	public KeyPair build() {
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
		// Generate a key pair for the new EndEntity
		KeyPairGenerator kGen;
		try {
			kGen = KeyPairGenerator.getInstance(keyAlg, provider);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}

		kGen.initialize(keyLength);
		return kGen.generateKeyPair();
	}

	public KeyPairBuilder withKeyLength(Integer keyLength) {
		this.keyLength = keyLength;
		return this;
	}

	public KeyPairBuilder withKeyAlg(String keyAlg) {
		this.keyAlg = keyAlg;
		return this;
	}
}
