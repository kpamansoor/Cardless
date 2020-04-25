package com.cooltechworks.creditcarddesign.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.cooltechworks.checkoutflow.R;


public class SignUpActivity extends AppCompatActivity {

    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    SecureStorage ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar));

        ss = new SecureStorage(SignUpActivity.this, getApplication());
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(final String pin) {


            AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this)

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirm?")
                    .setMessage("Want to proceed with " + pin)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ss.storData("pin", pin);
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(SignUpActivity.this, "PIN updated, login to proceed", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
        }
    };
}
