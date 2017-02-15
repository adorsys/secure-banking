package de.adorsys.android.securemobilepush.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
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
            List<String> smsSenderNumbers = Arrays.asList(SmsConfig.SMS_SENDER_NUMBERS);
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages;
            String messageFrom = null;
            if (bundle != null) {
                try {
                    //PDU = protocol data unit
                    //A PDU is a “protocol data unit”, which is the industry format for an SMS message.
                    //Because SMSMessage reads/writes them you should'nt need to dissect them.
                    //A large message might be broken into many, which is why it is an array of objects.
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        smsMessages = new SmsMessage[pdus.length];
                        // If the sent message is longer than 160 characters  it will be broken down
                        // in to chunks of 153 characters before being received on the device.
                        // To rectify that receivedMessage is the result of appending every single
                        // short message into one large one for our usage. see:
                        //http://www.textanywhere.net/faq/is-there-a-maximum-sms-message-length
                        String receivedMessage = "";
                        for (int i = 0; i < smsMessages.length; i++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i],
                                        bundle.getString("format"));
                                receivedMessage = receivedMessage + smsMessages[i].getMessageBody();
                            } else {
                                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                receivedMessage = receivedMessage + smsMessages[i].getMessageBody();
                            }
                            messageFrom = smsMessages[i].getOriginatingAddress();
                        }
                        if (!TextUtils.isEmpty(messageFrom)
                                && smsSenderNumbers.contains(messageFrom)) {
                            sendBroadcast(context, messageFrom, receivedMessage);
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.d(SmsReceiver.class.getName(), e.getMessage());
                    }
                    sendBroadcast(context, null, null);
                }
            }
        }
    }

    private void sendBroadcast(@NonNull Context context,
                               @Nullable String messageFrom,
                               @Nullable String smsMessage) {
        Intent broadcastIntent = new Intent(INTENT_ACTION_SMS);

        broadcastIntent.putExtra(KEY_SMS_SENDER, messageFrom);
        broadcastIntent.putExtra(KEY_SMS_MESSAGE, smsMessage != null
                ? getSmsCode(smsMessage) : null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
    }

    @NonNull
    private String getSmsCode(@NonNull String message) {
        int startIndex = message.indexOf(SmsConfig.BEGIN_INDEX);
        int endIndex = message.indexOf(SmsConfig.END_INDEX);

        return message.substring(startIndex, endIndex).replace(SmsConfig.BEGIN_INDEX, "").trim();
    }
}