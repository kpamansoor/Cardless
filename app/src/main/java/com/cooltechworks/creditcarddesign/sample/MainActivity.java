package com.cooltechworks.creditcarddesign.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.checkoutflow.R;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mansoor on 5/3/17.
 */

public class MainActivity extends AppCompatActivity {

    private final int CREATE_NEW_CARD = 0;
    private SharedPreferences sharedpreferences;
    private LinearLayout cardContainer;
    private SharedPreferences.Editor editor;
    private TextView addCardButton;
    private ImageView imgSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        sharedpreferences = getSharedPreferences("mysp", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
//        getSupportActionBar().setTitle("Payment");
        populate();
    }

    private void populate() {
        CreditCardView sampleCreditCardView;

        if (!sharedpreferences.getString("csdetails", "NULL").equals("NULL")) {
            JSONObject cards;
            JSONArray jsonArr;
            try {
                jsonArr = new JSONArray(sharedpreferences.getString("csdetails", "NULL"));
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

                Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
                startActivityForResult(intent, CREATE_NEW_CARD);
            }
        });

        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, imgSettings);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Change PIN"))
                            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                        else if (item.getTitle().equals("Delete All")) {

                            JSONObject jsonObj = new JSONObject();
                            JSONArray jsonArr = new JSONArray();
                            if (sharedpreferences.getString("csdetails", "NULL") != "NULL") {
                                try {
                                    jsonArr = new JSONArray(sharedpreferences.getString("csdetails", "NULL"));
                                    if (jsonArr.length() > 0) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Confirm?")
                                                .setContentText("All card details will be wiped permanently. Sure to proceed?")
                                                .setConfirmText("Delete")
                                                .setCancelText("Cancel")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {

                                                        editor.remove("csdetails");
                                                        editor.commit();
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

                        } else if (item.getTitle().equals("How to use")) {

                        }

                        return true;
                    }

                });

                popup.show();//showing popup menu
            }

        });
    }

    private void addCardListener(final int index, CreditCardView creditCardView) {
        creditCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CreditCardView creditCardView = (CreditCardView) v;
//                String cardNumber = creditCardView.getCardNumber();
//                String expiry = creditCardView.getExpiry();
//                String cardHolderName = creditCardView.getCardHolderName();
//                String cvv = creditCardView.getCVV();
//
//                Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
//                intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, cardHolderName);
//                intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, cardNumber);
//                intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, expiry);
//                intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_BACK);
//                intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, false);
//
//                // start at the CVV activity to edit it as it is not being passed
//                intent.putExtra(CreditCardUtils.EXTRA_ENTRY_START_PAGE, CreditCardUtils.CARD_CVV_PAGE);
                creditCardView.showBack();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        creditCardView.showFront();
                    }
                }, 1000);

//                startActivityForResult(intent, index);
            }
        });

        creditCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "log press found", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
//            Debug.printToast("Result Code is OK", getApplicationContext());

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

            } else {

                CreditCardView creditCardView = (CreditCardView) cardContainer.getChildAt(reqCode);

                creditCardView.setCardExpiry(expiry);
                creditCardView.setCardNumber(cardNumber);
                creditCardView.setCardHolderName(name);
                creditCardView.setCVV(cvv);

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

            if (sharedpreferences.getString("csdetails", "NULL") != "NULL")
                jsonArr = new JSONArray(sharedpreferences.getString("csdetails", "NULL"));
            jsonArr.put(jsonObj);

            editor.putString("csdetails", jsonArr.toString());
            editor.commit();
//            Log.d("Debug",jsonObj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}