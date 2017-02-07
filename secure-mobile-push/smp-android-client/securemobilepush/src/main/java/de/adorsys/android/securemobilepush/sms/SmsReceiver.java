package de.adorsys.android.securemobilepush.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import de.adorsys.android.securemobilepush.BuildConfig;

public class SmsReceiver extends BroadcastReceiver {
    public static final String INTENT_FILTER_SMS = "intentFilterSms";
    public static final String KEY_SMS_SENDER = "smsSender";
    public static final String KEY_SMS_MESSAGE = "smsMessage";

    /**
     * Retrieve the SMS message received and if it is sent from a specific number send a broadcast
     * containing the senderNumber & message
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages;
            String messageFrom;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    smsMessages = new SmsMessage[pdus.length];
                    for (int i = 0; i < smsMessages.length; i++) {
                        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        messageFrom = smsMessages[i].getOriginatingAddress();
                        String messageBody = smsMessages[i].getMessageBody();
                        if (messageFrom.equals(BuildConfig.smsSenderNumber)) {
                            Intent broadcastIntent = new Intent(INTENT_FILTER_SMS);
                            broadcastIntent.putExtra(KEY_SMS_SENDER, messageFrom);
                            broadcastIntent.putExtra(KEY_SMS_MESSAGE, messageBody);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
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
}