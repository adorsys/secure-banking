package de.adorsys.android.securedevicestorage;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import static android.content.ContentValues.TAG;

public class KeystoreTool {
    private static final String KEY_ALIAS = "androidKeyPair";
    private static final String KEY_ENCRYPTION_ALGORITHM = "RSA";
    private static final String KEY_KEYSTORE_NAME = "AndroidKeyStore";

    public static boolean keyPairExists() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void generateKeyPair(@NonNull Context context) {
        try {
            // Create new key if needed
            if (keyPairExists()) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.MONTH, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(KEY_ALIAS)
                        .setSubject(new X500Principal("CN=SecureDeviceStorage, O=Adorsys, C=Germany"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME);
                generator.initialize(spec);

                generator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void deleteKeyPair() {

    }

    public static PublicKey getPublicKey() {
        return null;
    }

    public static PrivateKey getPrivateKey() {
        return null;
    }
}
