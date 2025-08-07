package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.LoginEmail.LoginEmailActivityView;
import com.example.weatherapp.RegistrationPartOne.RegistrationPartOneActivityView;
import com.example.weatherapp.databinding.ActivityIntroBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * IntroActivity is the initial landing screen of the app after the user comes from the weather Screen.
 * It determines the user's next step based on previously saved shared preferences.
 * It also initializes an optional emergency exit button if available in the layout.
 * The layout is inflated using ViewBinding via.
 */
public class IntroActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting.
     * This method:
     *      - Initializes ViewBinding
     *      - Checks for and sets up the emergency exit button if present
     *      - Determines the navigation flow based on shared preferences:
     *              - createdAccount
     *              - createdPin
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityIntroBinding binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new SharedPreferenceHelper(this);


        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }
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
