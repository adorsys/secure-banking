package de.adorsys.android.securemobilepush.sms;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

public class SmsTool {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private Context context;

    public SmsTool(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Ask for RECEIVE_SMS permission for devices on Android M and later.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestSMSPermission() {
        Activity activity = (Activity) context;
        final String permission = Manifest.permission.RECEIVE_SMS;
        int hasSpecificPermission = ContextCompat.checkSelfPermission(context, permission);
        if (hasSpecificPermission != PackageManager.PERMISSION_GRANTED) {
            if (!activity.shouldShowRequestPermissionRationale(permission)) {
                activity.requestPermissions(new String[]{permission},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }
}
