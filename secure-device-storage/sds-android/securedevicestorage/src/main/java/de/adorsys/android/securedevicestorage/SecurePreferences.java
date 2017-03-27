package de.adorsys.android.securedevicestorage;


import android.content.Context;
import android.content.Intent;
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
            String transformedValue = KeystoreTool.encryptMessage(context, value);
            if (transformedValue != null) {
                setSecureValue(key, transformedValue, context);
            } else {
                Log.e(SecurePreferences.class.getName(),
                        context.getString(R.string.message_problem_encryption));
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException
                | IOException | UnrecoverableEntryException | KeyStoreException | InvalidAlgorithmParameterException
                | InvalidKeyException | CertificateException e) {
            Log.e(SecurePreferences.class.getName(), e.getMessage(), e);
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, boolean value, @NonNull Context context) {
        setValue(key, String.valueOf(value), context);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, float value, @NonNull Context context) {
        setValue(key, String.valueOf(value), context);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, long value, @NonNull Context context) {
        setValue(key, String.valueOf(value), context);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, int value, @NonNull Context context) {
        setValue(key, String.valueOf(value), context);
    }


    @Nullable
    public static String getStringValue(@NonNull String key, @NonNull Context context) {
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

    public static boolean getBooleanValue(@NonNull String key, @NonNull Context context) {
        return Boolean.parseBoolean(getStringValue(key, context));
    }

    public static float getFloatValue(@NonNull String key, @NonNull Context context) {
        return Float.parseFloat(getStringValue(key, context));
    }

    public static float getLongValue(@NonNull String key, @NonNull Context context) {
        return Long.parseLong(getStringValue(key, context));
    }

    public static float getIntValue(@NonNull String key, @NonNull Context context) {
        return Integer.parseInt(getStringValue(key, context));
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


    private static void setSecureValue(@NonNull String key, @NonNull String value, @NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putString(key, value).apply();
    }

    @Nullable
    private static String getSecureValue(@NonNull String key, @NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    private static void clearAllSecureValues(@NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}