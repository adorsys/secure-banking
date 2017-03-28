package de.adorsys.android.securedevicestorage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
                                @NonNull SecureMethod secureMethod) {
        if (secureMethod.equals(SecureMethod.METHOD_ENCRYPT)) {
            if (!KeystoreTool.keyPairExists()) {
                KeystoreTool.generateKeyPair(context);
            }

            String transformedValue = null;
            transformedValue = KeystoreTool.encryptMessage(context, value);
            if (transformedValue != null) {
                setSecureValue(key, transformedValue, context);
            } else {
                Log.e(SecurePreferences.class.getName(),
                        context.getString(R.string.message_problem_encryption));
            }
        } else {
            byte[] saltBytes = KeystoreTool.calculateSalt();
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
                setSecureValue(SALT_PREFIX + key, salt, context);
            } else {
                Log.e(SecurePreferences.class.getName(),
                        context.getString(R.string.message_problem_hashing));
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, boolean value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) {
        setValue(key, String.valueOf(value), context, secureMethod);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, float value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) {
        setValue(key, String.valueOf(value), context, secureMethod);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, long value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) {
        setValue(key, String.valueOf(value), context, secureMethod);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, int value,
                                @NonNull Context context,
                                @NonNull SecureMethod secureMethod) {
        setValue(key, String.valueOf(value), context, secureMethod);
    }


    @Nullable
    public static String getStringValue(@NonNull String key,
                                  @NonNull Context context,
                                  @NonNull SecureMethod secureMethod) {
        if (secureMethod.equals(SecureMethod.METHOD_ENCRYPT)) {
            String result = getSecureValue(key, context);
                return KeystoreTool.decryptMessage(context, result != null
                        ? result : context.getString(R.string.message_nothing_found));
        } else {
            return getSecureValue(key, context);
        }
    }

    public static boolean getBooleanValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod) {
        return Boolean.parseBoolean(getStringValue(key, context, secureMethod));
    }

    public static float getFloatValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod) {
        return Float.parseFloat(getStringValue(key, context, secureMethod));
    }

    public static float getLongValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod) {
        return Long.parseLong(getStringValue(key, context, secureMethod));
    }

    public static float getIntValue(@NonNull String key, @NonNull Context context, @NonNull SecureMethod secureMethod) {
        return Integer.parseInt(getStringValue(key, context, secureMethod));
    }


    public static void clearAllValues(@NonNull Context context) {
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair(context);
        }
        clearAllSecureValues(context);
    }


    public static boolean compareHashedCredential(@NonNull String currentCredential,
                                                  @NonNull String keyOfSecureCredential,
                                                  @NonNull Context context) {
        String securedCredential = getStringValue(keyOfSecureCredential, context, SecureMethod.METHOD_HASH);
        String salt = getStringValue(SALT_PREFIX + keyOfSecureCredential, context, SecureMethod.METHOD_HASH);
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