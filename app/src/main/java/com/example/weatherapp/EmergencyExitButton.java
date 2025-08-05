package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EmergencyExitButton {
    public static void setupEmergencyExit(Activity activity, FloatingActionButton button) {
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finishAffinity();
        });
    }
}
