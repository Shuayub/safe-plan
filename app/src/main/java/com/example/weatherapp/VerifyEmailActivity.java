package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.LoginEmail.LoginEmailActivityView;
import com.example.weatherapp.databinding.ActivityVerifyEmailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VerifyEmailActivity extends AppCompatActivity {

    private ActivityVerifyEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.LoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(VerifyEmailActivity.this, LoginEmailActivityView.class);
            startActivity(intent);
            finish();
        });

        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }
    }
}
