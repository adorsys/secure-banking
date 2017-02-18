package de.adorsys.android.securedevicestorage;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

class KeystoreTool {

    private static final String KEY_ALIAS = "adorsysKeyPair";
    private static final String KEY_ENCRYPTION_ALGORITHM = "RSA";
    private static final String KEY_KEYSTORE_NAME = "AndroidKeyStore";
    private static final String KEY_CIPHER_NAME = "AndroidOpenSSL";
    private static final String KEY_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding";

    static boolean keyPairExists() throws CertificateException, NoSuchAlgorithmException,
            IOException, KeyStoreException, UnrecoverableKeyException {

        return getKeyStoreInstance().getKey(KEY_ALIAS, null) != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    static void generateKeyPair(@NonNull Context context)
            throws InvalidAlgorithmParameterException,
            NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException,
            CertificateException, KeyStoreException, IOException {

        // Create new key if needed
        if (!keyPairExists()) {
//            Calendar start = Calendar.getInstance();
//            Calendar end = Calendar.getInstance();
//            end.add(Calendar.MONTH, 1);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(new X500Principal("CN=SecureDeviceStorage, O=Adorsys, C=Germany"))
                    .setSerialNumber(BigInteger.ONE)
//                    .setStartDate(start.getTime())
//                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator generator
                    = KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME);
            generator.initialize(spec);

            generator.generateKeyPair();
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(KeystoreTool.class.getName(),
                        context.getString(R.string.message_keypair_already_exists));
            }
        }
    }

    @Nullable
    private static RSAPublicKey getPublicKey(@NonNull Context context) throws UnrecoverableEntryException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {

        if (keyPairExists()) {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStoreInstance().getEntry(KEY_ALIAS, null);
            return (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(KeystoreTool.class.getName(), context.getString(R.string.message_keypair_does_not_exist));
            }
            return null;
        }
    }

    @Nullable
    private static RSAPrivateKey getPrivateKey(@NonNull Context context) throws UnrecoverableEntryException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {

        if (keyPairExists()) {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStoreInstance().getEntry(KEY_ALIAS, null);
            return (RSAPrivateKey) privateKeyEntry.getPrivateKey();
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(KeystoreTool.class.getName(), context.getString(R.string.message_keypair_does_not_exist));
            }
            return null;
        }
    }

    static void deleteKeyPair(@NonNull Context context) throws UnrecoverableKeyException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {

        if (keyPairExists()) {
            // Delete Key from Keystore
            getKeyStoreInstance().deleteEntry(KEY_ALIAS);
        } else {
            Log.e(KeystoreTool.class.getName(),
                    context.getString(R.string.message_keypair_does_not_exist));
        }
    }

    private static KeyStore getKeyStoreInstance() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {

        // Get the AndroidKeyStore instance
        KeyStore keyStore = KeyStore.getInstance(KEY_KEYSTORE_NAME);

        // Relict of the JCA API - you have to call load even
        // if you do not have an input stream you want to load or it'll crash
        keyStore.load(null);

        return keyStore;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Nullable
    static String encryptMessage(@NonNull Context context, @NonNull String plainMessage)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
            IOException, UnrecoverableEntryException, KeyStoreException,
            CertificateException, InvalidKeyException {

        Cipher input = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_NAME);
        input.init(Cipher.ENCRYPT_MODE, getPublicKey(context));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(
                outputStream, input);
        cipherOutputStream.write(plainMessage.getBytes("UTF-8"));
        cipherOutputStream.close();

        byte[] values = outputStream.toByteArray();
        return Base64.encodeToString(values, Base64.DEFAULT);
    }

    @Nullable
    static String decryptMessage(@NonNull Context context, @NonNull String encryptedMessage) throws
            NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
            IOException, UnrecoverableEntryException, KeyStoreException,
            CertificateException, InvalidKeyException {

        Cipher output = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_NAME);
        output.init(Cipher.DECRYPT_MODE, getPrivateKey(context));

        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(Base64.decode(encryptedMessage, Base64.DEFAULT)), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }

        return new String(bytes, 0, bytes.length, "UTF-8");
    }
}
