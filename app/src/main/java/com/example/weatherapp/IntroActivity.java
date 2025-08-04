package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.weatherapp.LoginEmail.LoginEmailActivity;
import com.example.weatherapp.RegistrationPartOne.RegistrationPartOneActivity;
import com.example.weatherapp.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityIntroBinding binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        binding.GetStartedButton.setOnClickListener(v -> {
            boolean createdAccount = sharedPreferences.getBoolean("createdAccount", false);
            boolean createdPin = sharedPreferences.getBoolean("createdPin", false);
            Intent intent;
            if (createdAccount && createdPin) {
                intent = new Intent(IntroActivity.this, LoginPinActivity.class);
            } else if (createdAccount){
                intent = new Intent(IntroActivity.this, LoginEmailActivity.class);
            }
            else {
                intent = new Intent(IntroActivity.this, RegistrationPartOneActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }
}
