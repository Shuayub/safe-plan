package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationButton {
    public static void setupNavigation(Activity activity, BottomNavigationView navView) {
        navView.setItemIconTintList(ContextCompat.getColorStateList(activity, R.color.nav_icon_color));
        navView.setItemTextColor(ContextCompat.getColorStateList(activity, R.color.nav_text_color));

        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_info && !(activity instanceof InfoActivity)){
                intent = new Intent(activity, InfoActivity.class);
            } else if (itemId == R.id.nav_support && !(activity instanceof SupportActivity)) {
                intent = new Intent(activity, SupportActivity.class);
            }
             else if (itemId == R.id.nav_reminders && !(activity instanceof ReminderActivity)) {
                intent = new Intent(activity, ReminderActivity.class);
             }
             else if (itemId == R.id.nav_plan && !(activity instanceof TipsActivity)) {
                intent = new Intent(activity, TipsActivity.class);
            }

            if (intent != null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }

            return true;
        });
    }
}
