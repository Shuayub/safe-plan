package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * EmergencyExitButton provides functionality for quick exit from the application.
 */
public class EmergencyExitButton {

    /**
     * Sets up a quick exit functionality using Floating Action Button
     *
     *
     * @param activity The current activity where the button resides in
     * @param button The floating action button that will trigger it
     */
    public static void setupEmergencyExit(Activity activity, FloatingActionButton button) {
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finishAffinity();
        });
    }
}
