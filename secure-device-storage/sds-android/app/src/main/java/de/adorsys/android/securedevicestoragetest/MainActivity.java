package de.adorsys.android.securedevicestoragetest;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.adorsys.android.securedevicestorage.SecureMethod;
import de.adorsys.android.securedevicestorage.SecurePreferences;

/**
 * @author Drilon Reçica
 * @since 2/17/17.
 */

public class MainActivity extends AppCompatActivity {
    private static final String KEY = "TEMPTAG";
    private static final String TAG = "LOGTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText input = (EditText) findViewById(R.id.plain_message_edit_text);
        final TextView keyInfoTextView = (TextView) findViewById(R.id.key_info_text_view);
        final Button generateKeyButton = (Button) findViewById(R.id.generate_key_button);
        final Button clearPreferencesButton = (Button) findViewById(R.id.clear_preferences_button);

        generateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(input.getText())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (generateKeyButton.getText().toString()
                                .equals(getString(R.string.button_generate_encrypt))) {
                            generateKeyButton.setText(R.string.button_encrypt);
                        }
                        SecurePreferences.setValue(KEY, input.getText().toString(),
                                MainActivity.this,
                                SecureMethod.METHOD_ENCRYPT);

                        String decryptedMessage = SecurePreferences.getValue(KEY,
                                MainActivity.this,
                                SecureMethod.METHOD_ENCRYPT);
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, decryptedMessage + " ");
                        }

                        Log.d("LOGTAG", "" +SecurePreferences.compareHashedCredential("test", KEY, MainActivity.this));
                        Log.d("LOGTAG", "" +SecurePreferences.compareHashedCredential("teSt", KEY, MainActivity.this));

                        keyInfoTextView.setText(getString(R.string.message_encrypted_decrypted,
                                input.getText().toString(), decryptedMessage));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearPreferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecurePreferences.clearAllValues(MainActivity.this);
                Toast.makeText(MainActivity.this, "SecurePreferences cleared and KeyPair deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}