package de.adorsys.cse.crypt;

import de.adorsys.cse.jwk.JWK;


public class SecretCredentialEncryptorImpl implements SecretCredentialEncryptor {
    private static final SymmetricEncryptor symmetricEncryptor = new SymmetricEncryptor();
    private static final RSAEncryptor rsaEncryptor = new RSAEncryptor();

    private JWK publicKey;

    public SecretCredentialEncryptorImpl(JWK publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("publicKey cannot be null");
        }
        if (!publicKey.getKeyType().equals("RSA")) {
            throw new IllegalArgumentException("provided public key is not RSA-based public key");
        }
        this.publicKey = publicKey;
    }

    @Override
    public String encrypt(String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("secret cannot be null or empty");
        }
        throw new UnsupportedOperationException("not implemented yet");
/*
        CryptoPair pair = symmetricEncryptor.encrypt(secret.getBytes());
        pair = rsaEncryptor.encryptSecretKey(pair, publicKey);

        return pair.encodeToBase64();
*/
    }
}
