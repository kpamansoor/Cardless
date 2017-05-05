package com.cooltechworks.creditcarddesign.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooltechworks.checkoutflow.R;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        initialize();
        listeners();
    }

    private void initialize() {
        addCardButton = (TextView) findViewById(R.id.add_card);
        cardContainer = (LinearLayout) findViewById(R.id.card_container);
        imgSettings = (ImageView) findViewById(R.id.settings);
        sharedpreferences = getSharedPreferences("mysp", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
//        getSupportActionBar().setTitle("Payment");
        populate();
    }

    private void populate() {
        CreditCardView sampleCreditCardView;

        if(!sharedpreferences.getString("csdetails","NULL").equals("NULL")) {
            JSONObject cards;
            JSONArray jsonArr;
            try {
                jsonArr = new JSONArray(sharedpreferences.getString("csdetails","NULL"));
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
    }

    private void addCardListener(final int index, CreditCardView creditCardView) {
        creditCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CreditCardView creditCardView = (CreditCardView) v;
                String cardNumber = creditCardView.getCardNumber();
                String expiry = creditCardView.getExpiry();
                String cardHolderName = creditCardView.getCardHolderName();
                String cvv = creditCardView.getCVV();

                Intent intent = new Intent(MainActivity.this, CardEditActivity.class);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, cardHolderName);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, cardNumber);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, expiry);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_BACK);
                intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, false);

                // start at the CVV activity to edit it as it is not being passed
                intent.putExtra(CreditCardUtils.EXTRA_ENTRY_START_PAGE, CreditCardUtils.CARD_CVV_PAGE);
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

            jsonArr = new JSONArray(sharedpreferences.getString("csdetails","NULL"));
            jsonArr.put(jsonObj);

            editor.putString("csdetails", jsonArr.toString());
            editor.commit();
//            Log.d("Debug",jsonObj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}