package de.adorsys.android.securedevicestorage;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, @NonNull String value, @NonNull Context context) {
        if (!KeystoreTool.keyPairExists()) {
            KeystoreTool.generateKeyPair(context);
        }
        String transformedValue = KeystoreTool.encryptMessage(context, value);
        if (transformedValue != null) {
            setSecureValue(key, transformedValue, context);
        } else {
            String message = context.getString(R.string.message_problem_encryption);
            Log.e(SecurePreferences.class.getName(), message);
            throw new CryptoException(message, null);
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
        return KeystoreTool.decryptMessage(context, result != null
                ? result : context.getString(R.string.message_nothing_found));
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
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair(context);
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