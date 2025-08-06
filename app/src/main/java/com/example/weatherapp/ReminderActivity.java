/**
 * ReminderFragment.java
 *
 * This fragment handles the display, creation, editing, and deletion of user-defined reminders.
 * Reminders include a frequency (Daily, Weekly, Monthly) and a time (e.g., 04:30 PM).
 * All reminders are stored using SharedPreferences in JSON format.
 */
package com.example.weatherapp;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
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
import java.util.Calendar;

/**
 * Fragment to manage reminders. Provides UI to add, edit, and delete reminders.
 * Stores reminders in SharedPreferences.
 */
public class ReminderActivity extends Fragment {
    private ArrayList<ReminderItem> reminderList;
    private ReminderAdapter adapter;

    /**
     * Called when the fragment is created.
     * Enables options menu in the toolbar.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Inflates the fragment layout, sets up RecyclerView and "Add Reminder" button.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.reminderRecyclerView);
        loadReminders();
        adapter = new ReminderAdapter(reminderList, requireContext(), this::showEditReminderDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addReminderButton).setOnClickListener(v -> showAddReminderDialog());

        return view;
    }

    /**
     * Shows a dialog allowing the user to create a new reminder with frequency and time.
     * Adds the new reminder to the list and saves it.
     */
    private void showAddReminderDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_reminder, null);
        Spinner spinner = dialogView.findViewById(R.id.frequencySpinner);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.reminder_frequencies, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(getContext())
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
                    scheduleReminder(freq, hour, minute, requestCode);
                    adapter.notifyDataSetChanged();
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
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_reminder, null);
        Spinner spinner = dialogView.findViewById(R.id.frequencySpinner);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
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

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Reminder")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    cancelReminder(reminder.getRequestCode());
                    reminder.setFrequency(spinner.getSelectedItem().toString());

                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    String time = String.format("%02d:%02d %s",(hour == 0 || hour == 12) ? 12 : hour % 12, minute, hour >= 12 ? "PM" : "AM");

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


    /**
     * Loads reminders from SharedPreferences (as JSON).
     * If no reminders exist, initializes an empty list.
     */
    private void loadReminders() {
        SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", 0);
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
        SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminderList);
        editor.putString("reminders", json);
        editor.apply();
    }

    /**
     * Inflate the menu for toolbar actions (e.g., clear all reminders).
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.reminder_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            Toast.makeText(getContext(), "All reminders cleared", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        intent.putExtra("frequency", freq);

        int hourFormat = (hour == 0 || hour == 12) ? 12 : hour % 12;
        String amPm = (hour >= 12) ? "PM" : "AM";
        String timeFormatted = String.format("%02d:%02d %s", hourFormat, minute, amPm);
        intent.putExtra("time", timeFormatted);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

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
        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( getContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

    }

}