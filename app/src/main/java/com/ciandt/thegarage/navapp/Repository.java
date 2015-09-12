package com.ciandt.thegarage.navapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;

/**
 * Created by Emerson on 18/08/2015.
 */
public class Repository {
    private SharedPreferences sharedPreferences;
    private Context context;

    public Repository(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Beacons", 6);
    }

    public String get(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void put(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
