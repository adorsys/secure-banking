package de.adorsys.android.securemobilepush.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import de.adorsys.android.securemobilepush.BuildConfig;

public class SmsReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION_SMS = "intent_action_sms";
    public static final String KEY_SMS_SENDER = "key_sms_sender";
    public static final String KEY_SMS_MESSAGE = "key_sms_message";

    private static final String INTENT_ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(INTENT_ACTION_SMS_RECEIVED)) {
            List<String> smsSenderNumbers = Arrays.asList(BuildConfig.SMS_SENDER_NUMBER);
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages;
            String messageFrom;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        smsMessages = new SmsMessage[pdus.length];
                        for (int i = 0; i < smsMessages.length; i++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i],
                                        bundle.getString("format"));
                            } else {
                                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            }
                            messageFrom = smsMessages[i].getOriginatingAddress();
                            if (smsSenderNumbers.contains(messageFrom)) {
                                String messageBody = getSmsCode(smsMessages[i].getMessageBody());
                                Intent broadcastIntent = new Intent(INTENT_ACTION_SMS);
                                broadcastIntent.putExtra(KEY_SMS_SENDER, messageFrom);
                                broadcastIntent.putExtra(KEY_SMS_MESSAGE, messageBody);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.d(SmsReceiver.class.getName(), e.getMessage());
                    }
                }
            }
        }
    }

    private String getSmsCode(@NonNull String message) {
        int startIndex = message.indexOf(BuildConfig.BEGIN_INDEX);
        int endIndex = message.indexOf(BuildConfig.END_INDEX);

        return message.substring(startIndex, endIndex).replace(BuildConfig.BEGIN_INDEX, "").trim();
    }
}