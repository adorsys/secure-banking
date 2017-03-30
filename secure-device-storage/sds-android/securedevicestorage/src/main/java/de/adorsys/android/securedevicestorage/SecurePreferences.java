package de.adorsys.android.securedevicestorage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Drilon Reçica
 * @since 2/17/17.
 */
public class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";
    private static final String SALT_PREFIX = "SALT OF ";
    private static final String KEY_CHARSET = "UTF-8";

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key,
                                @NonNull String value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) throws CryptoException {
        if (secureMethod.equals(SecureMethod.METHOD_ENCRYPT)) {
            if (!KeystoreTool.keyPairExists()) {
                KeystoreTool.generateKeyPair(context);
            }

            String transformedValue = KeystoreTool.encryptMessage(context, value);
            if (!TextUtils.isEmpty(transformedValue)) {
                setSecureValue(key, transformedValue, context);
            } else {
                throw new CryptoException(context.getString(R.string.message_problem_encryption), null);
            }
        } else {
            byte[] saltBytes = KeystoreTool.calculateSalt();
            String salt;
            try {
                if (saltBytes != null) {
                    salt = new String(saltBytes, 0, saltBytes.length, KEY_CHARSET);
                } else {
                    throw new CryptoException(context.getString(R.string.message_problem_salt_creation), null);
                }
            } catch (UnsupportedEncodingException e) {
                throw new CryptoException(e.getMessage(), e);
            }
            String hashedValue = KeystoreTool.getSHA512(value, salt);
            if (hashedValue != null) {
                setSecureValue(key, hashedValue, context);
                setSecureValue(SALT_PREFIX + key, salt, context);
            } else {
                throw new CryptoException(context.getString(R.string.message_problem_hashing), null);
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, boolean value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) throws CryptoException {
        setValue(key, String.valueOf(value), context, secureMethod);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, float value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) throws CryptoException {
        setValue(key, String.valueOf(value), context, secureMethod);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, long value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) throws CryptoException {
        setValue(key, String.valueOf(value), context, secureMethod);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, int value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) throws CryptoException {
        setValue(key, String.valueOf(value), context, secureMethod);
    }


    @Nullable
    public static String getStringValue(@NonNull String key,
                                        @NonNull Context context,
                                        @NonNull SecureMethod secureMethod,
                                        @Nullable String defValue) {
        if (secureMethod.equals(SecureMethod.METHOD_ENCRYPT)) {
            String result = getSecureValue(key, context);
            try {
                if (!TextUtils.isEmpty(result)) {
                    return KeystoreTool.decryptMessage(context, result);
                } else {
                    return defValue;
                }
            } catch (CryptoException e) {
                return defValue;
            }
        } else {
            return getSecureValue(key, context);
        }
    }

    public static boolean getBooleanValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod, boolean defValue) {
        return Boolean.parseBoolean(getStringValue(key, context, secureMethod, String.valueOf(defValue)));
    }

    public static float getFloatValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod, float defValue) {
        return Float.parseFloat(getStringValue(key, context, secureMethod, String.valueOf(defValue)));
    }

    public static float getLongValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod, long defValue) {
        return Long.parseLong(getStringValue(key, context, secureMethod, String.valueOf(defValue)));
    }

    public static float getIntValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod, int defValue) {
        return Integer.parseInt(getStringValue(key, context, secureMethod, String.valueOf(defValue)));
    }


    public static void clearAllValues(@NonNull Context context) throws CryptoException {
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair(context);
        }
        clearAllSecureValues(context);
    }


    public static boolean compareHashedCredential(@NonNull String currentCredential,
                                                  @NonNull String keyOfSecureCredential,
                                                  @NonNull Context context) throws CryptoException {
        String securedCredential = getStringValue(keyOfSecureCredential, context, SecureMethod.METHOD_HASH, null);
        String salt = getStringValue(SALT_PREFIX + keyOfSecureCredential, context, SecureMethod.METHOD_HASH, null);
        String hashedCurrentCredential = KeystoreTool.getSHA512(currentCredential, salt);

        if (hashedCurrentCredential != null) {
            return hashedCurrentCredential.equals(securedCredential);
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(SecurePreferences.class.getName(), context.getString(R.string.message_problem_hashing));
            }
            return false;
        }
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
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

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    private static void clearAllSecureValues(@NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
}