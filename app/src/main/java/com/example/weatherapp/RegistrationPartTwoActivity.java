package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.weatherapp.databinding.ActivityRegistrationPartTwoBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationPartTwoActivity extends AppCompatActivity {

    private ActivityRegistrationPartTwoBinding binding;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationPartTwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.FinishButton.setOnClickListener(v -> {
            String pin = binding.pinEditText.getText().toString();

            if (pin.length() == 4 || pin.length() == 6) {
                String userUID = mAuth.getUid();

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor userPinEditor = sharedPreferences.edit();
                userPinEditor.putBoolean("createdPin", true);
                userPinEditor.putString("userPin", pin);
                userPinEditor.putString("userUID", userUID);
                userPinEditor.apply();

                // Navigate to next screen (SurveyActivity)
                Intent intent = new Intent(this, survey.class);
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
