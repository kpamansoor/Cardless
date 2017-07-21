package com.cooltechworks.creditcarddesign.sample;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.checkoutflow.R;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by mansoor on 5/3/17.
 */

public class MainActivity extends AppCompatActivity {

    private final int CREATE_NEW_CARD = 0;
    private final int MY_SCAN_REQUEST_CODE = 1;
    private final int MY_SCAN_RESULT_CODE = 13274384;
    private LinearLayout cardContainer;
    private TextView addCardButton;
    private ImageView imgSettings;
    private InterstitialAd mInterstitialAd;
    SecureStorage ss;
    OptionsFabLayout fabWithOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-1243068719441957~6001259830");// Testing
//        MobileAds.initialize(this, "ca-app-pub-1243068719441957~6001259828");// Production
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Testing
//        mInterstitialAd.setAdUnitId("ca-app-pub-1243068719441957/7477993022"); // Production
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        ss =  new SecureStorage(MainActivity.this, getApplication());

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar));

        initialize();
        listeners();
    }

    private void initialize() {
        imgSettings = (ImageView) findViewById(R.id.imgSettings);
        addCardButton = (TextView) findViewById(R.id.add_card);
        cardContainer = (LinearLayout) findViewById(R.id.card_container);
        fabWithOptions = (OptionsFabLayout) findViewById(R.id.fab_l);

        //Set mini fab's colors.
        fabWithOptions.setMiniFabsColors(
                R.color.logo_color,
                R.color.logo_color,
                R.color.green_fab,
                R.color.green_fab,
                R.color.green_fab);
        populate();
    }

    private void populate() {
        CreditCardView sampleCreditCardView;

        if (!ss.readData("csdetails").equals("NULL")) {
            JSONObject cards;
            JSONArray jsonArr;
            try {
                jsonArr = new JSONArray(ss.readData("csdetails"));
                for (int i = 0; i < jsonArr.length(); i++) {
                    cards = (JSONObject) jsonArr.get(i);

                    String name = cards.getString("name");
                    String cvv = cards.getString("cvv");
                    String expiry = cards.getString("expiry");
                    String cardNumber = cards.getString("number");

                    sampleCreditCardView = new CreditCardView(this);
                    sampleCreditCardView.setCVV(cvv);
                    sampleCreditCardView.setCardHolderName(name);
                    sampleCreditCardView.setCardExpiry(expiry);
                    sampleCreditCardView.setCardNumber(cardNumber);

                    cardContainer.addView(sampleCreditCardView);
                    int index = cardContainer.getChildCount() - 1;
                    addCardListener(index, sampleCreditCardView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    private void listeners() {
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onScanPress(v);
//                Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
//                startActivityForResult(intent, CREATE_NEW_CARD);
            }
        });



        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Log.i("Ads", "onAdClosed");
            }
        });

        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Main fab clicked!", Toast.LENGTH_SHORT).show();
                if(fabWithOptions.isOptionsMenuOpened())
                    fabWithOptions.closeOptionsMenu();
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.add_manual:
                        Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
                        startActivityForResult(intent, CREATE_NEW_CARD);
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.add_auto:
                        onScanPress();
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.change_pin:
                        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.delete_all:
                        JSONObject jsonObj = new JSONObject();
                        JSONArray jsonArr = new JSONArray();
                        if (ss.readData("csdetails") != "NULL") {
                            try {
                                jsonArr = new JSONArray(ss.readData("csdetails"));
                                if (jsonArr.length() > 0) {
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Confirm?")
                                            .setContentText("All card details will be wiped permanently. Sure to proceed?")
                                            .setConfirmText("Delete")
                                            .setCancelText("Cancel")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    ss.removeData("csdetails");
                                                    finish();
                                                    startActivity(getIntent());
                                                    Toast.makeText(MainActivity.this, "Data deleted permanently", Toast.LENGTH_SHORT).show();
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
                                } else
                                    Toast.makeText(MainActivity.this, "No card details found", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else
                            Toast.makeText(MainActivity.this, "No card details found", Toast.LENGTH_SHORT).show();
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.howto:
                        startActivity(new Intent(MainActivity.this, IntroActivity.class).putExtra("howto",true));
                        fabWithOptions.closeOptionsMenu();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void addCardListener(final int index, CreditCardView creditCardView) {
        creditCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CreditCardView creditCardView = (CreditCardView) v;
                creditCardView.showBack();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        creditCardView.showFront();
                    }
                }, 1000);
            }
        });

        creditCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Card no copied to clipboad", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                final CreditCardView creditCardView = (CreditCardView) view;
                ClipData clip = ClipData.newPlainText("text", creditCardView.getCardNumber());
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            String name = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            if (reqCode == CREATE_NEW_CARD) {

                CreditCardView creditCardView = new CreditCardView(this);

                creditCardView.setCVV(cvv);
                creditCardView.setCardHolderName(name);
                creditCardView.setCardExpiry(expiry);
                creditCardView.setCardNumber(cardNumber);

                cardContainer.addView(creditCardView);
                int index = cardContainer.getChildCount() - 1;
                addCardListener(index, creditCardView);
                saveCard(creditCardView);

            }
        }else if (resultCode == MY_SCAN_RESULT_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                CreditCardView creditCardView = new CreditCardView(this);

                creditCardView.setCVV(scanResult.cvv);
                creditCardView.setCardHolderName(scanResult.cardholderName);
                creditCardView.setCardExpiry(scanResult.expiryMonth + "/" + scanResult.expiryYear);
                creditCardView.setCardNumber(scanResult.cardNumber);

                cardContainer.addView(creditCardView);
                int index = cardContainer.getChildCount() - 1;
                addCardListener(index, creditCardView);
                saveCard(creditCardView);
            }
        }

    }

    private void saveCard(CreditCardView creditCardView) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();

        try {
            jsonObj.put("name", creditCardView.getCardHolderName());
            jsonObj.put("number", creditCardView.getCardNumber());
            jsonObj.put("expiry", creditCardView.getExpiry());
            jsonObj.put("cvv", creditCardView.getCVV());

            if (ss.readData("csdetails") != "NULL")
                jsonArr = new JSONArray(ss.readData("csdetails"));
            jsonArr.put(jsonObj);

            ss.storData("csdetails",jsonArr.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_INSTRUCTIONS, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        if(fabWithOptions.isOptionsMenuOpened())
            fabWithOptions.closeOptionsMenu();
        else
            finish();
    }
}