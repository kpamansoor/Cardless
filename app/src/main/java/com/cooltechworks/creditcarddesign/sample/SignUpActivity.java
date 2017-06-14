package com.cooltechworks.creditcarddesign.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.cooltechworks.checkoutflow.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity {

    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedpreferences = getSharedPreferences("mysp", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(final String pin) {
            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Confirm?")
                    .setContentText("Want to proceed with "+pin)
                    .setConfirmText("Confirm ,Login!")
                    .setCancelText("Change")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            editor.putString("pin", pin);
                            editor.commit();
                            sDialog.dismissWithAnimation();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(SignUpActivity.this,"PIN Created, login to proceed",Toast.LENGTH_LONG).show();
                        }
                    })
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
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
