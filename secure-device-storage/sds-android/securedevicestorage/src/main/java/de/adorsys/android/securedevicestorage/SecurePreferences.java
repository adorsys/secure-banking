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

public class SecurePreferences {

    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, @NonNull String value, @NonNull Context context) {
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
    }

    @Nullable
    public static String getValue(@NonNull String key, @NonNull Context context) {

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

    @SuppressLint("CommitPrefEdits")
    private static void setSecureValue(@NonNull String key, @NonNull String value, @NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }

    @Nullable
    public static String getSecureValue(@NonNull String key, @NonNull Context context) {
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