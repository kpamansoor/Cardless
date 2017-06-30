package com.cooltechworks.creditcarddesign.sample;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import com.cooltechworks.checkoutflow.R;
import com.kevalpatel.passcodeview.KeyNamesBuilder;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.indicators.CircleIndicator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.keys.RoundKey;

public class LoginActivity extends Activity {

    SecureStorage ss;

    TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar));

        ss =  new SecureStorage(LoginActivity.this, getApplication());

        if(ss.readData("pin") == "NULL") {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        }

        message = (TextView) findViewById(R.id.message);


        PinView pinView = (PinView) findViewById(R.id.pin_view);
        if(ss.readData("pin") != "NULL") {
            pinView.setCorrectPin(parseInt(ss.readData("pin").toString()));
        }
//        pinView.setCorrectPin(new int[]{1, 2, 3, 4});

        //Build the desired key shape and pass the theme parameters.
        //REQUIRED
        pinView.setKey(new RoundKey.Builder(pinView)
                .setKeyPadding(R.dimen.key_padding)
                .setKeyStrokeColorResource(R.color.bluishgreen)
                .setKeyStrokeWidth(R.dimen.key_stroke_width)
                .setKeyTextColorResource(R.color.white)
                .setKeyTextSize(R.dimen.key_text_size)
                .build());
        pinView.setIndicator(new CircleIndicator.Builder(pinView)
                .setIndicatorRadius(R.dimen.indicator_radius)
                .setIndicatorFilledColorResource(R.color.white)
                .setIndicatorStrokeColorResource(R.color.bluishgreen)
                .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width)
                .build());
        pinView.setKeyNames(new KeyNamesBuilder()
                .setKeyOne(this, R.string.key_1)
                .setKeyTwo(this, R.string.key_2)
                .setKeyThree(this, R.string.key_3)
                .setKeyFour(this, R.string.key_4)
                .setKeyFive(this, R.string.key_5)
                .setKeySix(this, R.string.key_6)
                .setKeySeven(this, R.string.key_7)
                .setKeyEight(this, R.string.key_8)
                .setKeyNine(this, R.string.key_9)
                .setKeyZero(this, R.string.key_0));

        pinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                //Calls whenever authentication is failed or user is unauthorized.
                //Do something if you want to handle unauthorized user.
            }
        });
    }

    private static int[] parseInt(String str) {
        int i;
        int[] n = new int[str.length()];

        for (i = 0; i < str.length(); i++) {
            n[i]= str.charAt(i) - 48;
        }
        return n;
    }
}

