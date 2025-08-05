package com.example.weatherapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReminderFragment extends Fragment {

    private ArrayList<ReminderItem> reminderList;
    private ReminderAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enables options menu
    }

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
                    String time = String.format("%02d:%02d %s",
                            (hour == 0 || hour == 12) ? 12 : hour % 12,
                            minute,
                            hour >= 12 ? "PM" : "AM");

                    reminderList.add(new ReminderItem(freq, time));
                    adapter.notifyDataSetChanged();
                    saveReminders();
                    Toast.makeText(getContext(), freq + " reminder saved!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

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

    private void saveReminders() {
        SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminderList);
        editor.putString("reminders", json);
        editor.apply();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.reminder_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

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

    private long getRepeatInterval(String freq) {
        switch (freq) {
            case "Weekly": return TimeUnit.DAYS.toMillis(7);
            case "Monthly": return TimeUnit.DAYS.toMillis(30);
            default: return TimeUnit.DAYS.toMillis(1);
        }
    }
}
