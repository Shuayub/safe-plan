package com.example.weatherapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.databinding.ActivityRegistrationPartTwoBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationPartTwoActivity extends AppCompatActivity {

    private ActivityRegistrationPartTwoBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationPartTwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }

        mAuth = FirebaseAuth.getInstance();

        binding.FinishButton.setOnClickListener(v -> {
            String pin = binding.pinEditText.getText().toString();

            if (pin.length() == 4 || pin.length() == 6) {
                String userUID = mAuth.getUid();

                SharedPreferenceHelper.editor.putBoolean("createdPin", true);
                SharedPreferenceHelper.editor.putString("userPin", pin);
                SharedPreferenceHelper.editor.putString("userUID", userUID);
                SharedPreferenceHelper.editor.apply();

                // Navigate to next screen (SurveyActivity)
                Intent intent = new Intent(this, SurveyActivity.class);
                startActivity(intent);
                finish();

            } else {
                binding.pinError.setVisibility(View.VISIBLE);
                binding.pinEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        });

        binding.pinEditText.setOnKeyListener((v, keyCode, event) -> {
            if (binding.pinError.getVisibility() == View.VISIBLE) {
                binding.pinEditText.setBackgroundTintList(null);
                binding.pinError.setVisibility(View.INVISIBLE);
            }
            return false;
        });
    }
}
