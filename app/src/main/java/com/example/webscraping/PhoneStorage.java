package com.example.webscraping;

import android.content.Context;
import android.content.SharedPreferences;

public class PhoneStorage {
    private static final String PREF_NAME = "PhonePreferences";
    private static final String KEY_PHONE = "latestPhone";

    public static void saveLatestPhone(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PHONE, name);
        editor.apply();
    }

    public static String getLatestPhone(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_PHONE, "");
    }
}
