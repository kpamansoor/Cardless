package com.cooltechworks.creditcarddesign.sample;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private boolean showAds = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        MobileAds.initialize(this, "ca-app-pub-1243068719441957~6001259830");// Testing
        MobileAds.initialize(this, "ca-app-pub-1243068719441957~6001259828");// Production
        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Testing
        mInterstitialAd.setAdUnitId("ca-app-pub-1243068719441957/7477993022"); // Production
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
                        showAds = true;
                        Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
                        startActivityForResult(intent, CREATE_NEW_CARD);
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.add_auto:
                        showAds = false;
                        onScanPress();
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.change_pin:
                        showAds = true;
                        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.delete_all:
                        showAds = true;
                        JSONObject jsonObj = new JSONObject();
                        JSONArray jsonArr = new JSONArray();
                        if (ss.readData("csdetails") != "NULL") {
                            try {
                                jsonArr = new JSONArray(ss.readData("csdetails"));
                                if (jsonArr.length() > 0) {

                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)

                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Confirm?")
                                            .setMessage("All card details will be wiped permanently. Sure to proceed?")
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    ss.removeData("csdetails");
                                                    finish();
                                                    startActivity(getIntent());
                                                    Toast.makeText(MainActivity.this, "Data deleted permanently", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

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
                        showAds = true;
                        startActivity(new Intent(MainActivity.this, IntroActivity.class).putExtra("howto",true));
                        fabWithOptions.closeOptionsMenu();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void addCardListener(final int index, final CreditCardView creditCardView) {
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
            public boolean onLongClick(final View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, creditCardView);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.card_action, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        final CreditCardView creditCardView = (CreditCardView) view;
                        if(item.getTitle().equals("Copy Card Number")){
                            Toast.makeText(MainActivity.this, "Card no copied to clipboad", Toast.LENGTH_SHORT).show();
                            ClipData clip = ClipData.newPlainText("text", creditCardView.getCardNumber());
                            clipboard.setPrimaryClip(clip);
                        }else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Confirm?")
                                    .setMessage("This card will be deleted permanently. Sure to proceed?")
                                    .setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            JSONObject jsonObj = new JSONObject();
                                            JSONArray jsonArr = new JSONArray();
                                            if (ss.readData("csdetails") != "NULL") {
                                                try {
                                                    jsonArr = new JSONArray(ss.readData("csdetails"));
                                                    for (int j=0; j < jsonArr.length(); j++) {
                                                        if(jsonArr.getJSONObject(j).get("number").equals(creditCardView.getCardNumber())){
                                                            jsonArr.remove(j);
                                                        }
                                                    }
                                                    ss.storData("csdetails",jsonArr.toString());
                                                    Toast.makeText(MainActivity.this, "Card deleted", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(getIntent());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu

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
        if (mInterstitialAd.isLoaded() && showAds) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInterstitialAd.show();
                }
            }, 10000);

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