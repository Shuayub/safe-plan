package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.weatherapp.LoginEmail.LoginEmailActivityView;
import com.example.weatherapp.databinding.ActivityLoginPinBinding;

public class LoginPinActivity extends AppCompatActivity {

    private ActivityLoginPinBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        binding.LoginButton.setOnClickListener(v -> {
            String pin = binding.pinEditText.getText().toString();
            String storedPin = sharedPreferences.getString("userPin", null);

            if (pin.equals(storedPin)) {
                Intent intent = new Intent(LoginPinActivity.this, homeScreenActivity.class);
                startActivity(intent);
                finish(); // Optional
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

        binding.emailSignInButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPinActivity.this, LoginEmailActivityView.class);
            startActivity(intent);
        });
    }
}
