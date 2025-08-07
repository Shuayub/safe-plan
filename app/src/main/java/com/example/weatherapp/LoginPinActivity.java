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
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * LoginPinActivity allows the user to log in using a previously created PIN.
 * This activity:
 *   - Retrieves the stored PIN from shared preferences.
 *   - Compares the entered PIN with the stored one and navigates to  homeScreenActivity if correct.
 *   - Displays an error if the PIN is incorrect.
 *   - Allows the user to switch to email sign-in via LoginEmailActivityView.
 *   - Sets up an emergency exit button if present in the layout.
 * View access is managed through activityLoginPinBinding using ViewBinding.
 */
public class LoginPinActivity extends AppCompatActivity {

    private ActivityLoginPinBinding binding;
    private SharedPreferences sharedPreferences;

    /**
     * Called when the activity is first created.
     * This method:
     *   - Inflates the view using ViewBinding and sets the content layout.
     *   - Initializes shared preferences for reading stored user data.
     *   - Sets up the emergency exit button if available.
     *   - Handles PIN login validation and navigation to the home screen.
     *   - Provides access to email login as an alternative method.
     *   - Clears error UI when the PIN input is modified.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }

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
