package com.cooltechworks.creditcarddesign.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.cooltechworks.checkoutflow.R;

public class SignUpActivity extends AppCompatActivity {

    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
        }
    };
}
