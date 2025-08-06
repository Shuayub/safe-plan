/**
 * ReminderFragment.java
 *
 * This fragment handles the display, creation, editing, and deletion of user-defined reminders.
 * Reminders include a frequency (Daily, Weekly, Monthly) and a time (e.g., 04:30 PM).
 * All reminders are stored using SharedPreferences in JSON format.
 */
package com.example.weatherapp;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
     * Fragment to manage reminders. Provides UI to add, edit, and delete reminders.
     * Stores reminders in SharedPreferences.
     */
    public class ReminderActivity extends AppCompatActivity {

        private ArrayList<ReminderItem> reminderList;
        private ReminderAdapter adapter;

        /**
         * Called when the fragment is created.
         * Enables options menu in the toolbar.
         */
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reminder);  // Reusing the same layout

            RecyclerView recyclerView = findViewById(R.id.reminderRecyclerView);
            loadReminders();

            adapter = new ReminderAdapter(reminderList, this, this::showEditReminderDialog);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            findViewById(R.id.addReminderButton).setOnClickListener(v -> showAddReminderDialog());
        }

        /**
         * Shows a dialog allowing the user to create a new reminder with frequency and time.
         * Adds the new reminder to the list and saves it.
         */
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

                        // Format time as 12-hour with AM/PM
                        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d %s",
                                (hour == 0 || hour == 12) ? 12 : hour % 12,
                                minute,
                                hour >= 12 ? "PM" : "AM");

                        // Generate unique ID for each reminder
                        int requestCode = (freq + time).hashCode();

                        ReminderItem item = new ReminderItem(freq, time, requestCode);
                        reminderList.add(item);
                        adapter.notifyDataSetChanged();  // Added this line to refresh UI
                        saveReminders();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        /**
         * Shows a dialog to edit or delete a selected reminder.
         * Updates reminder on save or removes it on delete.
         *
         * @param position Index of the reminder in the list.
         */
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

            // Preset time in the TimePicker
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


        /**
         * Loads reminders from SharedPreferences (as JSON).
         * If no reminders exist, initializes an empty list.
         */
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

        /**
         * Saves the current reminder list to SharedPreferences in JSON format.
         */
        private void saveReminders() {
            SharedPreferences prefs = getSharedPreferences("ReminderPrefs", 0);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(reminderList);
            editor.putString("reminders", json);
            editor.apply();
        }

        /**
         * Handles toolbar item clicks. Clears all reminders if selected.
         */
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
    }