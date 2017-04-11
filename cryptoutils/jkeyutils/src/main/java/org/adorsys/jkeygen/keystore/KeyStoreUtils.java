package org.adorsys.jkeygen.keystore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

import org.adorsys.jkeygen.keypair.CertifiedKeyPairData;
import org.adorsys.jkeygen.utils.V3CertificateUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.X509CertificateHolder;

public class KeyStoreUtils {

	public static KeyStore newKeyStore(String storeType, CallbackHandler keyStorePasswordSrc, List<KeyPairStoreData> keyPairHolders) throws KeyStoreException {

		// Use default type if blank.
		if (StringUtils.isBlank(storeType))
			storeType = KeyStore.getDefaultType();

		KeyStore ks;
		try {
			ks = KeyStore.getInstance(storeType);
		} catch (KeyStoreException e) {
			throw new IllegalStateException(e);
		}
		
		fillKeyStore(ks, keyStorePasswordSrc, keyPairHolders);

		return ks;
	}

	public static void fillKeyStore(final KeyStore ks, CallbackHandler keyStorePasswordSrc, List<KeyPairStoreData> keyPairHolders) throws KeyStoreException {

		for (KeyPairStoreData keyPairHolder : keyPairHolders) {
			List<Certificate> chainList = new ArrayList<>();
			CertifiedKeyPairData certification = keyPairHolder.getCertification();
			if(keyPairHolder.getCertification()!=null){
				if(certification!=null){
					X509CertificateHolder subjectCert = certification.getSubjectCert();
					chainList.add(V3CertificateUtils.getX509JavaCertificate(subjectCert));
					List<X509CertificateHolder> issuerChain = certification.getIssuerChain();
					for (X509CertificateHolder x509CertificateHolder : issuerChain) {
						chainList.add(V3CertificateUtils.getX509JavaCertificate(x509CertificateHolder));
					}
				}
			}
			Certificate[] chain = chainList.toArray(new Certificate[chainList.size()]);
			ks.setKeyEntry(keyPairHolder.getAlias(), keyPairHolder.getKeyPairs().getKeyPair().getPrivate(), 
					PasswordCallbackUtils.getPassword(keyStorePasswordSrc, keyPairHolder.getAlias()), chain);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ks.store(bos, PasswordCallbackUtils.getPassword(keyStorePasswordSrc,"keystore"));
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new IllegalStateException(e);
		}

	}
}
