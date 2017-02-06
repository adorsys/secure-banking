package de.adorsys.android.securemobilepushtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import de.adorsys.android.securemobilepush.KeyValues;
import de.adorsys.android.securemobilepush.sms.SmsTool;

public class MainActivity extends AppCompatActivity {
    private TextView smsSenderTextView;
    private TextView smsMessageTextView;
    private LocalBroadcastManager localBroadcastManager;
    /**
     * Set Data received from Broadcast receiver to specific views
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(KeyValues.INTENT_FILTER_SMS)) {
                String receivedTitle = intent.getStringExtra(KeyValues.KEY_SMS_SENDER);
                String receivedMessage = intent.getStringExtra(KeyValues.KEY_SMS_MESSAGE);
                smsSenderTextView.setText(receivedTitle);
                smsMessageTextView.setText(receivedMessage);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmsTool smsTool = new SmsTool(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            smsTool.requestSMSPermission();
        }

        initViews();

        registerReceiver();
    }

    /**
     * UnRegister BroadcastReceiver from Activity to prevent potential memory leaks
     * and not keep receiving sms when app is in background
     */
    @Override
    protected void onPause() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    /**
     * Initialize Views
     */
    private void initViews() {
        smsSenderTextView = (TextView) findViewById(R.id.sms_sender_text_view);
        smsMessageTextView = (TextView) findViewById(R.id.sms_message_text_view);
    }

    /**
     * Register BroadcastReceiver to Activity to get data from Notification in foreground
     */
    private void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KeyValues.INTENT_FILTER_SMS);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
}
