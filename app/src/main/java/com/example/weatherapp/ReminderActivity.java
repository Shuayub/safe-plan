package com.example.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    private ArrayList<ReminderItem> reminderList;
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reminder); // You can rename to activity_reminder if preferred

        RecyclerView recyclerView = findViewById(R.id.reminderRecyclerView);
        loadReminders();
        adapter = new ReminderAdapter(reminderList, this, this::showEditReminderDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addReminderButton).setOnClickListener(v -> showAddReminderDialog());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_reminders);

        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }

    }

    private void showAddReminderDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);
        Spinner spinner = dialogView.findViewById(R.id.frequencySpinner);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_frequencies, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(this)
                .setTitle("Add Reminder")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String freq = spinner.getSelectedItem().toString();
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();

                    @SuppressLint("DefaultLocale")
                    String time = String.format("%02d:%02d %s",
                            (hour == 0 || hour == 12) ? 12 : hour % 12,
                            minute,
                            hour >= 12 ? "PM" : "AM");

                    int requestCode = (freq + time).hashCode();
                    ReminderItem item = new ReminderItem(freq, time, requestCode);
                    reminderList.add(item);
                    scheduleReminder(freq, hour, minute, requestCode);
                    adapter.notifyDataSetChanged();
                    saveReminders();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditReminderDialog(int position) {
        ReminderItem reminder = reminderList.get(position);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);
        Spinner spinner = dialogView.findViewById(R.id.frequencySpinner);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_frequencies, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getPosition(reminder.getFrequency()));

        try {
            String[] parts = reminder.getTime().split(":| ");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            String ampm = parts[2];
            if (ampm.equals("PM") && hour != 12) hour += 12;
            if (ampm.equals("AM") && hour == 12) hour = 0;
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } catch (Exception ignored) {}

        new AlertDialog.Builder(this)
                .setTitle("Edit Reminder")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    cancelReminder(reminder.getRequestCode());
                    reminder.setFrequency(spinner.getSelectedItem().toString());

                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    String time = String.format("%02d:%02d %s",
                            (hour == 0 || hour == 12) ? 12 : hour % 12,
                            minute,
                            hour >= 12 ? "PM" : "AM");

                    reminder.setTime(time);
                    scheduleReminder(reminder.getFrequency(), hour, minute, reminder.getRequestCode());

                    adapter.notifyDataSetChanged();
                    saveReminders();
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    cancelReminder(reminder.getRequestCode());
                    reminderList.remove(position);
                    adapter.notifyDataSetChanged();
                    saveReminders();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void loadReminders() {
        SharedPreferences prefs = getSharedPreferences("ReminderPrefs", 0);
        String json = prefs.getString("reminders", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ReminderItem>>() {}.getType();
            reminderList = gson.fromJson(json, type);
        } else {
            reminderList = new ArrayList<>();
        }
    }

    private void saveReminders() {
        SharedPreferences prefs = getSharedPreferences("ReminderPrefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminderList);
        editor.putString("reminders", json);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reminder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_all) {
            reminderList.clear();
            adapter.notifyDataSetChanged();
            saveReminders();
            Toast.makeText(this, "All reminders cleared", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Calculates the next future timestamp in milliseconds.
     *
     * @param hour The hour of the day in the range 0-23
     * @param minute The minute of the given hour in the range 0-59
     * @return The next trigger for the alarm
     */
    private long calculateNextTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar.getTimeInMillis();
    }

    /**
     * Schedules an exact reminder using AlarmManager based on the specified frequency and time.
     *
     * @param freq The reminder frequency (daily, weekly, monthly)
     * @param hour The hour in which the reminder should trigger
     * @param minute The minute in which the reminder should trigger
     * @param requestCode The unique identifier for the specific reminder
     */
    private void scheduleReminder(String freq, int hour, int minute, int requestCode) {
        long triggerTime = calculateNextTime(hour, minute);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("frequency", freq);

        int hourFormat = (hour == 0 || hour == 12) ? 12 : hour % 12;
        String amPm = (hour >= 12) ? "PM" : "AM";
        String timeFormatted = String.format("%02d:%02d %s", hourFormat, minute, amPm);
        intent.putExtra("time", timeFormatted);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // Opens setting screen so user can allow notifications to be turned on if using android versions greater than version 8.0.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
            Intent alarmIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            alarmIntent.setData(Uri.parse("package:com.example.weatherapp"));
            startActivity(alarmIntent);
            return;
        }
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    /**
     * Cancels a previously scheduled reminder based on its unique identifier
     *
     * @param requestCode The unique identifier for the specific reminder
     */
    private void cancelReminder(int requestCode) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

    }
}
