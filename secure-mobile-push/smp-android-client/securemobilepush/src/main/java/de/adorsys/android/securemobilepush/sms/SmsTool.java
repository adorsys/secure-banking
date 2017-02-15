package de.adorsys.android.securemobilepush.sms;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

public final class SmsTool {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestSMSPermission(@NonNull Activity activity) {
        final String permission = Manifest.permission.RECEIVE_SMS;
        int hasSpecificPermission = ContextCompat.checkSelfPermission(activity, permission);
        if (hasSpecificPermission != PackageManager.PERMISSION_GRANTED
                && !activity.shouldShowRequestPermissionRationale(permission)) {
            activity.requestPermissions(new String[]{permission},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    public static void createSmsConfig(@NonNull String BEGIN_INDEX,
                                       @NonNull String END_INDEX,
                                       @NonNull String... SMS_SENDER_NUMBERS) {
        SmsConfig.BEGIN_INDEX = BEGIN_INDEX;
        SmsConfig.END_INDEX = END_INDEX;
        SmsConfig.SMS_SENDER_NUMBERS = SMS_SENDER_NUMBERS;
    }
}
