package org.adorsys.jkeygen.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;

public class KeystoreBuilder {
	private String storeType;
	private String storeId;
	private Map<String, KeyEntryData> keyEntries = new HashMap<>();
	
	public KeystoreBuilder withStoreType(String storeType) {
		this.storeType = storeType;
		return this;
	}
	public KeystoreBuilder withStoreId(String storeId) {
		this.storeId = storeId;
		return this;
	}
	public KeystoreBuilder withKeyEntry(KeyEntryData keyEntry) {
		this.keyEntries.put(keyEntry.getAlias(), keyEntry);
		return this;
	}
	
	public byte[] build(CallbackHandler storePassSrc) throws IOException, NoSuchAlgorithmException, CertificateException{
		KeyStore ks = KeyStoreService.newKeyStore(storeType);
		KeyStoreService.fillKeyStore(ks, keyEntries.values());
		return KeyStoreService.toByteArray(ks, storeId, storePassSrc);
	}
}
