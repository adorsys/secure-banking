package org.adorsys.jkeygen.keystore;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jkeygen.keypair.CertificationResult;
import org.adorsys.jkeygen.utils.V3CertificateUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.X509CertificateHolder;

public class KeyStoreUtils {

	/**
	 * Create an initializes a new keystore. The keystore is not yet password protected.
	 * 
	 * @param storeType
	 * @param keyPairHolders
	 * @return
	 * @throws KeyStoreException
	 */
	public static KeyStore newKeyStore(String storeType) throws IOException {

		// Use default type if blank.
		if (StringUtils.isBlank(storeType))
			storeType = KeyStore.getDefaultType();

		try {
			KeyStore ks = KeyStore.getInstance(storeType);
			ks.load(null, null);
			return ks;
		} catch (NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Loads a key store. Given the store bytes, the store type 
	 * 
	 * @param in : the inputStream from which to read the keystore
	 * @param storeId : The store id. This is passed to the callback handler to identify the requested password record.
	 * @param storeType : the type of this key store. f null, the defaut java keystore type is used.
	 * @param storePassSrc : the callback handler that retrieves the store password. 
	 * @return
	 * @throws KeyStoreException
	 * @throws IOException 
	 */
	public static KeyStore loadKeyStore(InputStream in, String storeId, String storeType, CallbackHandler storePassSrc) throws IOException {

		// Use default type if blank.
		if (StringUtils.isBlank(storeType))
			storeType = KeyStore.getDefaultType();

		try {
			KeyStore ks = KeyStore.getInstance(storeType);
			ks.load(in, PasswordCallbackUtils.getPassword(storePassSrc, storeId));
			return ks;
		} catch (NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
			throw new IllegalStateException(e);
		}
	}	

	/**
	 * Put the given entries into a key store. The key store must have been initialized before.
	 * 
	 * @param ks
	 * @param keyPairHolders
	 * @throws KeyStoreException
	 */
	public static void fillKeyStore(final KeyStore ks, List<KeyPairStoreData> keyPairHolders) throws KeyStoreException {

		for (KeyPairStoreData keyPairHolder : keyPairHolders) {
			List<Certificate> chainList = new ArrayList<>();
			CertificationResult certification = keyPairHolder.getCertification();
			X509CertificateHolder subjectCert = certification!=null?certification.getSubjectCert():keyPairHolder.getKeyPairs().getSubjectCert();
			chainList.add(V3CertificateUtils.getX509JavaCertificate(subjectCert));
			if(certification!=null){
				List<X509CertificateHolder> issuerChain = certification.getIssuerChain();
				for (X509CertificateHolder x509CertificateHolder : issuerChain) {
					chainList.add(V3CertificateUtils.getX509JavaCertificate(x509CertificateHolder));
				}
			}
			Certificate[] chain = chainList.toArray(new Certificate[chainList.size()]);
			ks.setKeyEntry(keyPairHolder.getAlias(), keyPairHolder.getKeyPairs().getKeyPair().getPrivate(), 
					PasswordCallbackUtils.getPassword(keyPairHolder.getPasswordSource(), keyPairHolder.getAlias()), chain);
		}
	}
}
