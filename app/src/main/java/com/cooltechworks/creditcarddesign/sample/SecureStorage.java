package com.cooltechworks.creditcarddesign.sample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.security.GeneralSecurityException;

import in.co.ophio.secure.core.KeyStoreKeyGenerator;
import in.co.ophio.secure.core.ObscuredPreferencesBuilder;

/**
 * Created by L4208412 on 30/6/2017.
 */

public class SecureStorage {

    Context context;
    Application application;
    String key,PREFS_NAME = "cardless";
    SharedPreferences sharedPreferences;

    public SecureStorage(Context context, Application application) {
        this.context = context;
        try {
            key = KeyStoreKeyGenerator.get(application,
                    context.getPackageName())
                    .loadOrGenerateKeys();
            sharedPreferences = new ObscuredPreferencesBuilder()
                    .setApplication(application)
                    .obfuscateValue(true)
                    .obfuscateKey(true)
                    .setSharePrefFileName(PREFS_NAME)
                    .setSecret(key)  //secret key we generated in step 1.
                    .createSharedPrefs();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void storData(String key,String value) {
        sharedPreferences.edit().putString(key, value).commit();

    }

    public  void removeData(String key) {
        sharedPreferences.edit().remove(key).commit();

    }

    public  String readData(String key) {
        return sharedPreferences.getString(key, "NULL");

    }
}
