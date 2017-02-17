package de.adorsys.android.securedevicestoragetest;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.adorsys.android.securedevicestorage.KeystoreTool;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView keyInfoTextView = (TextView) findViewById(R.id.key_info_text_view);
        Button generateKeyButton = (Button) findViewById(R.id.generate_key_button);

        generateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (KeystoreTool.keyPairExists()) {
                    Toast.makeText(MainActivity.this, "EXISTS", Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        KeystoreTool.generateKeyPair(MainActivity.this);
                    }
                }
            }
        });
    }
}
