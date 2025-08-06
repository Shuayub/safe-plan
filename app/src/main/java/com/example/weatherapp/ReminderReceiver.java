package com.example.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    /**
     * Launches an intent which transfers you to login page once notification is clicked.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // Need to change this to loginScreen.
            Intent launchIntent = new Intent(context, IntroActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            NotificationHelper.showNotification(context, "Reminder", "Check for updates on today's weather!", launchIntent);

        } catch (Exception e) {
            return;
        }
    }
}
