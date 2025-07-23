package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationButton {
    public static void setupNavigation(Activity activity, BottomNavigationView navView) {
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_plan && !(activity instanceof PlanActivity)) {
                intent = new Intent(activity, PlanActivity.class);
            } else if (itemId == R.id.nav_info && !(activity instanceof InfoActivity)) {
                intent = new Intent(activity, InfoActivity.class);
            } else if (itemId == R.id.nav_support && !(activity instanceof SupportFragment)) {
                intent = new Intent(activity, SupportFragment.class);
            } else if (itemId == R.id.nav_reminders && !(activity instanceof RemindersActivity)) {
                intent = new Intent(activity, RemindersActivity.class);
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
