package com.cooltechworks.creditcarddesign.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.cooltechworks.checkoutflow.R;
import com.kevalpatel.passcodeview.KeyNamesBuilder;
import com.kevalpatel.passcodeview.PatternView;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.indicators.CircleIndicator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.keys.RoundKey;
import com.kevalpatel.passcodeview.patternCells.CirclePatternCell;
import com.kevalpatel.passcodeview.patternCells.PatternPoint;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PinView pinView = (PinView) findViewById(R.id.pin_view);
        pinView.setCorrectPin(new int[]{1, 2, 3, 4});

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


}

