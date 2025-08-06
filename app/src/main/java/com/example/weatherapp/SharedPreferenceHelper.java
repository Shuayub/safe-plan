package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferenceHelper {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public static boolean verifyPinCreated() {
        return (sharedPreferences.getBoolean("createdPin", false));
    }
}
