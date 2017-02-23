package de.adorsys.android.securedevicestorage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Drilon Reçica
 * @since 2/17/17.
 */

public class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";
    private static final String KEY_CHARSET = "UTF-8";

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, @NonNull String value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) {
        if (secureMethod.equals(SecureMethod.METHOD_ENCRYPT)) {
            try {
                if (!KeystoreTool.keyPairExists()) {
                    KeystoreTool.generateKeyPair(context);
                }
            } catch (CertificateException | NoSuchAlgorithmException | IOException
                    | KeyStoreException | UnrecoverableKeyException
                    | InvalidAlgorithmParameterException | NoSuchProviderException e) {
                Log.e(SecurePreferences.class.getName(), e.getMessage(), e);
                return;
            }

            String transformedValue = null;
            try {
                transformedValue = KeystoreTool.encryptMessage(context, value);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException
                    | IOException | UnrecoverableEntryException | KeyStoreException
                    | InvalidKeyException | CertificateException e) {

                Log.e(SecurePreferences.class.getName(), e.getMessage(), e);
            }
            if (transformedValue != null) {
                setSecureValue(key, transformedValue, context);
            } else {
                Log.e(SecurePreferences.class.getName(),
                        context.getString(R.string.message_problem_encryption));
            }
        } else {
            byte[] saltBytes = KeystoreTool.calculateSalt(value);
            String salt;
            try {
                if (saltBytes != null) {
                    salt = new String(saltBytes, 0, saltBytes.length, KEY_CHARSET);
                } else {
                    Log.e(SecurePreferences.class.getName(),
                            context.getString(R.string.message_problem_salt_creation));
                    return;
                }
            } catch (UnsupportedEncodingException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(SecurePreferences.class.getName(), e.getMessage(), e);
                }
                return;
            }
            String hashedValue = KeystoreTool.getSHA512(value, salt);
            if (hashedValue != null) {
                setSecureValue(key, hashedValue, context);
            } else {
                Log.e(SecurePreferences.class.getName(),
                        context.getString(R.string.message_problem_hashing));
            }
        }
    }

    @Nullable
    public static String getValue(@NonNull String key,
                                  @NonNull Context context,
                                  @NonNull SecureMethod secureMethod) {

        if (secureMethod.equals(SecureMethod.METHOD_ENCRYPT)) {
            String result = getSecureValue(key, context);
            try {
                return KeystoreTool.decryptMessage(context, result != null
                        ? result : context.getString(R.string.message_nothing_found));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException |
                    UnrecoverableEntryException | IOException | CertificateException |
                    InvalidKeyException | KeyStoreException e) {

                Log.e(SecurePreferences.class.getName(), e.getMessage(), e);
                return null;
            }
        } else {
            return getSecureValue(key, context);
        }
    }

    public static void clearAllValues(@NonNull Context context) {
        try {
            if (KeystoreTool.keyPairExists()) {
                KeystoreTool.deleteKeyPair(context);
            }
        } catch (CertificateException | NoSuchAlgorithmException | IOException
                | UnrecoverableKeyException | KeyStoreException e) {
            if (BuildConfig.DEBUG) {
                Log.e(SecurePreferences.class.getName(), e.getMessage(), e);
            }
        }
        clearAllSecureValues(context);
    }

    public static boolean compareHashedCredential(@NonNull String currentCredential,
                                                  @NonNull String securedCredential,
                                                  @NonNull Context context) {

        byte[] saltBytes = KeystoreTool.calculateSalt(currentCredential);
        String salt;
        try {
            if (saltBytes != null) {
                salt = new String(saltBytes, 0, saltBytes.length, KEY_CHARSET);
            } else {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            if (BuildConfig.DEBUG) {
                Log.e(SecurePreferences.class.getName(), e.getMessage(), e);
            }
            return false;
        }

        String hashedCurrentCredential = KeystoreTool.getSHA512(currentCredential, salt);
        if (hashedCurrentCredential != null) {
            return hashedCurrentCredential.equals(securedCredential);
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(SecurePreferences.class.getName(),
                        context.getString(R.string.message_problem_hashing));
            }
            return false;
        }
    }

    @SuppressLint("CommitPrefEdits")
    private static void setSecureValue(@NonNull String key, @NonNull String value, @NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }

    @Nullable
    private static String getSecureValue(@NonNull String key, @NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    @SuppressLint("CommitPrefEdits")
    private static void clearAllSecureValues(@NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
}