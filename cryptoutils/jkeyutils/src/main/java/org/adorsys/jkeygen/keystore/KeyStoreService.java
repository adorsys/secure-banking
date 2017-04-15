package org.adorsys.jkeygen.keystore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jkeygen.keypair.CertificationResult;
import org.adorsys.jkeygen.utils.V3CertificateUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 * Key store manipulation routines.
 * 
 * @author fpo
 *
 */
public class KeyStoreService {

	/**
	 * Create an initializes a new key store. The key store is not yet password protected.
	 * 
	 * @param storeType
	 * @param keyPairHolders
	 * @return
	 * @throws KeyStoreException
	 */
	public static KeyStore newKeyStore(String storeType) throws IOException {

		// Use default type if blank.
		if (StringUtils.isBlank(storeType))storeType = "UBER";
		try {
			KeyStore ks = KeyStore.getInstance(storeType);
			ks.load(null, null);
			return ks;
		} catch (NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Write this key store into a byte array
	 * 
	 * @return
	 * @throws IOException if there was an I/O problem with data
	 * @throws CertificateException if any of the certificates included in the keystore data could not be stored
	 * @throws NoSuchAlgorithmException  if the appropriate data integrity algorithm could not be found
	 * @throws KeyStoreException 
	 */
	public static byte[] toByteArray(KeyStore keystore, String storeId, CallbackHandler storePassSrc) throws NoSuchAlgorithmException, CertificateException, IOException{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			keystore.store(stream, PasswordCallbackUtils.getPassword(storePassSrc, storeId));
		} catch (KeyStoreException e) {
			throw new IllegalStateException("Keystore not initialized.", e);
		}
		return stream.toByteArray();
	}
	
	/**
	 * Loads a key store. Given the store bytes, the store type 
	 * 
	 * @param in : the inputStream from which to read the keystore
	 * @param storeId : The store id. This is passed to the callback handler to identify the requested password record.
	 * @param storeType : the type of this key store. f null, the defaut java keystore type is used.
	 * @param storePassSrc : the callback handler that retrieves the store password. 
	 * @return
	 * @throws KeyStoreException either NoSuchAlgorithmException or NoSuchProviderException
	 * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore cannot be found
	 * @throws CertificateException if any of the certificates in the keystore could not be loaded
	 * @throws UnrecoverableKeyException if a password is required but not given, or if the given password was incorrect
	 * @throws IOException if there is an I/O or format problem with the keystore data
	 */
	public static KeyStore loadKeyStore(InputStream in, String storeId, String storeType, CallbackHandler storePassSrc) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, IOException {

		// Use default type if blank.
		if (StringUtils.isBlank(storeType))storeType = "UBER";

		KeyStore ks = KeyStore.getInstance(storeType);

		try {
			ks.load(in, PasswordCallbackUtils.getPassword(storePassSrc, storeId));
		} catch (IOException e) {
			// catch missing or wrong key.
			if(e.getCause()!=null && (e.getCause() instanceof UnrecoverableKeyException)){
				throw (UnrecoverableKeyException)e.getCause();
			} else if (e.getCause()!=null && (e.getCause() instanceof BadPaddingException)){
				throw new UnrecoverableKeyException(e.getMessage());
			}
			throw e;
		}
		return ks;
	}	
	
	/**
	 * 
	 * @param data : the byte array containing key store data.
	 * @param storeId : The store id. This is passed to the callback handler to identify the requested password record.
	 * @param storeType : the type of this key store. f null, the defaut java keystore type is used.
	 * @param storePassSrc : the callback handler that retrieves the store password. 
	 * @return
	 * @throws KeyStoreException either NoSuchAlgorithmException or NoSuchProviderException
	 * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore cannot be found
	 * @throws CertificateException if any of the certificates in the keystore could not be loaded
	 * @throws UnrecoverableKeyException if a password is required but not given, or if the given password was incorrect
	 * @throws IOException if there is an I/O or format problem with the keystore data
	 */
	public static KeyStore loadKeyStore(byte[] data, String storeId, String storeType, CallbackHandler storePassSrc) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return loadKeyStore(new ByteArrayInputStream(data), storeId, storeType, storePassSrc);
	}	
	
	/**
	 * Put the given entries into a key store. The key store must have been initialized before.
	 * 
	 * @param ks
	 * @param keyEntries
	 */
	public static void fillKeyStore(final KeyStore ks, Collection<KeyEntryData> keyEntries) {
		try {
			for (KeyEntryData keyEntryData : keyEntries) {
				if(keyEntryData instanceof KeyPairData){
					addToKeyStore(ks, (KeyPairData)keyEntryData);
				} else if (keyEntryData instanceof SecretKeyData){
					addToKeyStore(ks, (SecretKeyData)keyEntryData);
				} else if (keyEntryData instanceof TrustedCertData){
					addToKeyStore(ks, (TrustedCertData)keyEntryData);
				} 
			}
		} catch(KeyStoreException ex){
			throw new IllegalStateException(ex.getMessage(), ex);
		}
	}

	private static void addToKeyStore(final KeyStore ks, KeyPairData keyPairHolder) throws KeyStoreException {

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

	public static void addToKeyStore(final KeyStore ks, SecretKeyData secretKeyData) throws KeyStoreException {
		KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKeyData.getSecretKey());
		ProtectionParameter protParam = new KeyStore.PasswordProtection(PasswordCallbackUtils.getPassword(secretKeyData.getPasswordSource(), secretKeyData.getAlias()));
		ks.setEntry(secretKeyData.getAlias(), entry, protParam);
	}
	
	
	private static void addToKeyStore(final KeyStore ks, TrustedCertData trustedCertHolder) throws KeyStoreException {
		ks.setCertificateEntry(trustedCertHolder.getAlias(), V3CertificateUtils.getX509JavaCertificate(trustedCertHolder.getCertificate()));
	}
}

