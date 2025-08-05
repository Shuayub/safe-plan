package com.example.weatherapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReminderActivity extends AppCompatActivity {

    private ArrayList<ReminderItem> reminderList;
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);  // Reusing the same layout

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_reminders);

        RecyclerView recyclerView = findViewById(R.id.reminderRecyclerView);
        loadReminders();

        adapter = new ReminderAdapter(reminderList, this, this::showEditReminderDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addReminderButton).setOnClickListener(v -> showAddReminderDialog());
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
                    String time = String.format("%02d:%02d %s",
                            (hour == 0 || hour == 12) ? 12 : hour % 12,
                            minute,
                            hour >= 12 ? "PM" : "AM");

                    reminderList.add(new ReminderItem(freq, time));
                    adapter.notifyDataSetChanged();
                    saveReminders();
                    Toast.makeText(this, freq + " reminder saved!", Toast.LENGTH_SHORT).show();
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
                    reminder.setFrequency(spinner.getSelectedItem().toString());
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    String time = String.format("%02d:%02d %s",
                            (hour == 0 || hour == 12) ? 12 : hour % 12,
                            minute,
                            hour >= 12 ? "PM" : "AM");

                    reminder.setTime(time);
                    adapter.notifyDataSetChanged();
                    saveReminders();
                })
                .setNegativeButton("Delete", (dialog, which) -> {
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

    private long getRepeatInterval(String freq) {
        switch (freq) {
            case "Weekly": return TimeUnit.DAYS.toMillis(7);
            case "Monthly": return TimeUnit.DAYS.toMillis(30);
            default: return TimeUnit.DAYS.toMillis(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reminder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_all) {
            reminderList.clear();
            adapter.notifyDataSetChanged();
            saveReminders();
            Toast.makeText(this, "All reminders cleared", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}