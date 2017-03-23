package org.adorsys.psd2.pop;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStore.Entry.Attribute;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class JwkKeyConversionTest {
	private static KeyStore keyStore;
	private static char[] keypass = "keypass".toCharArray();
	
	@BeforeClass
	public static void beforeClass(){
		keyStore = KeyStoreLoader.loadKeyStore("src/test/resources/xs2atestkeystore2keys.jks", null, "storepass".toCharArray(), false);
	}

	@Test
	public void test() {
		
		assumeNotNull(keyStore);
		
		try {
			Enumeration<String> aliases = keyStore.aliases();
			
			PasswordProtection passwordProtection = new PasswordProtection(keypass);
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				System.out.println(alias + "\n");
				Entry entry = keyStore.getEntry(alias, passwordProtection);
				if(entry instanceof PrivateKeyEntry){
					PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) entry;
					PrivateKey privateKey = privateKeyEntry.getPrivateKey();
					Certificate certificate = privateKeyEntry.getCertificate();
					PublicKey publicKey = certificate.getPublicKey();
					String algorithm = privateKey.getAlgorithm();
					String algorithm2 = publicKey.getAlgorithm();
					Set<Attribute> attributes = privateKeyEntry.getAttributes();
				}
				Set<Attribute> attributes = entry.getAttributes();
				for (Attribute attribute : attributes) {
					String name = attribute.getName();
					String value = attribute.getValue();
					System.out.println(name + " "  + value);
				}
				Key privateKey = keyStore.getKey(alias, keypass);
				String algorithm = privateKey.getAlgorithm();
				String format = privateKey.getFormat();
				System.out.println(algorithm + " "  + format);
				System.out.println("end \n");
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
			throw new IllegalStateException(e);
		}
	}
}
