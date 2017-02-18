package de.adorsys.android.securedevicestoragetest;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.adorsys.android.securedevicestorage.BuildConfig;
import de.adorsys.android.securedevicestorage.KeystoreTool;
import de.adorsys.android.securedevicestorage.SecurePreferences;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView keyInfoTextView = (TextView) findViewById(R.id.key_info_text_view);
        final Button generateKeyButton = (Button) findViewById(R.id.generate_key_button);

        if (KeystoreTool.keyPairExists()) {
            generateKeyButton.setText("Start Encryption/Decryption");
        } else {
            generateKeyButton.setText("Generate Key and Start Encryption/Decryption");
        }

        generateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (generateKeyButton.getText().toString().equals("Generate Key and Start Encryption/Decryption")) {
                        generateKeyButton.setText("Start Encryption/Decryption");
                    }
                    SecurePreferences.setValue("TAG", "TEST", MainActivity.this);
                    String encryptedMessage = SecurePreferences.getSecureValue("TAG", MainActivity.this);
                    if (BuildConfig.DEBUG) {
                        Log.d("LOGTAG", encryptedMessage + " ");
                    }
                    String decryptedMessage = SecurePreferences.getValue("TAG", MainActivity.this);
                    if (BuildConfig.DEBUG) {
                        Log.d("LOGTAG", decryptedMessage + " ");
                    }
                    keyInfoTextView.setText(getString(R.string.message_encrypted_decrypted,
                            "Test", encryptedMessage, decryptedMessage));
                }
            }
        });
    }
}
