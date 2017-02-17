package de.adorsys.android.securedevicestorage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SharedPreferencesUtil {

    @SuppressLint("CommitPrefEdits")
    public static void setValue(@NonNull String key, @NonNull String value, @NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(key, value).commit();
    }

    @Nullable
    public static String getValue(@NonNull String key, @NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    @SuppressLint("CommitPrefEdits")
    public static void clearAllValues(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().commit();
    }
}
