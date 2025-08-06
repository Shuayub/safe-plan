package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.LoginEmail.LoginEmailActivityView;
import com.example.weatherapp.RegistrationPartOne.RegistrationPartOneActivityView;
import com.example.weatherapp.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityIntroBinding binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new SharedPreferenceHelper(this);

        binding.GetStartedButton.setOnClickListener(v -> {
            boolean createdAccount = SharedPreferenceHelper.sharedPreferences.getBoolean("createdAccount", false);
            boolean createdPin = SharedPreferenceHelper.sharedPreferences.getBoolean("createdPin", false);
            Intent intent;
            if (createdPin) {
                intent = new Intent(IntroActivity.this, LoginPinActivity.class);
            } else if (createdAccount){
                intent = new Intent(IntroActivity.this, LoginEmailActivityView.class);
            }
            else {
                intent = new Intent(IntroActivity.this, RegistrationPartOneActivityView.class);
            }
            startActivity(intent);
            finish();
        });
    }
}
