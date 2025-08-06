package com.example.weatherapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/**
 * NotificationHelper.java
 *
 * This helper class is responsible for creating and displaying notifications within the app.
 */

public class NotificationHelper {
    private static final String CHANNEL_ID = "reminder_channel";

    /**
     * Displays the notification to be triggered with the specified title and message
     *
     * @param context The application context used to access system services
     * @param title   The title text to be displayed
     * @param message The message text to be displayed
     * @param launchIntent The intent that specifies what activity should open when the user taps the notification
     */
    public static void showNotification(Context context, String title, String message, Intent launchIntent) {
        // Setting manager to the notification manager of the current context.
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Need to create a notification channel for android 8.0 and higher. This creates that channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Reminders", NotificationManager.IMPORTANCE_HIGH);
            if (manager != null)
                manager.createNotificationChannel(channel);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,launchIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.reminder_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // This sets the notification
        if (manager != null)
            manager.notify((int) System.currentTimeMillis(), builder.build());

    }
}
