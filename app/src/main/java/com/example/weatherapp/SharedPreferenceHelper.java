package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferenceHelper {

    /** Static reference to the SharedPreferences instance */
    public static SharedPreferences sharedPreferences;

    /** Static reference to the SharedPreferences.Editor instance */
    public static SharedPreferences.Editor editor;


    /**
     * Initializes the shared preferences and editor with the given context.
     * This constructor must be called before accessing any other static fields or methods in this class.
     *
     * @param context the application or activity context used to access shared preferences
     */
    public SharedPreferenceHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    /**
     * Checks whether a user has created a PIN.
     *
     * @return true if the "createdPin" flag is set to true in shared preferences;
     *         false otherwise
     */
    public static boolean verifyPinCreated() {
        return (sharedPreferences.getBoolean("createdPin", false));
    }
}
