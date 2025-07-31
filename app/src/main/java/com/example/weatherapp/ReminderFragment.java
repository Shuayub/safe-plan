package com.example.weatherapp;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class ReminderFragment extends Fragment {

    private Spinner frequencySpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        frequencySpinner = view.findViewById(R.id.frequencySpinner);

        view.findViewById(R.id.setReminderButton).setOnClickListener(v -> {
            String frequency = frequencySpinner.getSelectedItem().toString();
            long repeatInterval = getRepeatInterval(frequency);
            scheduleReminder(repeatInterval);
            Toast.makeText(getContext(), frequency + " reminder set!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private long getRepeatInterval(String freq) {
        switch (freq) {
            case "Weekly": return TimeUnit.DAYS.toMillis(7);
            case "Monthly": return TimeUnit.DAYS.toMillis(30);
            default: return TimeUnit.DAYS.toMillis(1);
        }
    }

    private void scheduleReminder(long repeatMillis) {
        PeriodicWorkRequest reminderRequest = new PeriodicWorkRequest.Builder(ReminderWorker.class, repeatMillis, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("PlanReminder", ExistingPeriodicWorkPolicy.REPLACE, reminderRequest);
    }
}
