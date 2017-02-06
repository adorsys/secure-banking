package de.adorsys.android.securemobilepush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    private static final String testNumber = "0900123456";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();        //Get the SMS message passed in
            SmsMessage[] smsMessages;
            String messageFrom;
            if (bundle != null) {
                //Retrieve the SMS message received
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    smsMessages = new SmsMessage[pdus.length];
                    for (int i = 0; i < smsMessages.length; i++) {
                        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        messageFrom = smsMessages[i].getOriginatingAddress();
                        String messageBody = smsMessages[i].getMessageBody();
                        if (messageFrom.equals(testNumber)) {
                            Toast.makeText(context, "" + messageFrom + "   " + messageBody, Toast.LENGTH_SHORT).show();
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