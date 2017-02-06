package de.adorsys.android.securemobilepushtest;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.adorsys.android.securemobilepush.SmsTool;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmsTool smsTool = new SmsTool(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            smsTool.requestSMSPermission();
        }
    }
}
