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


/**
 * RegistrationPartTwoActivity handles the second step of user registration,
 * where the user is asked to create a secure PIN.
 * Key responsibilities of this activity:
 *   - Validates the entered PIN (must be 4 or 6 digits).
 *   - Stores the PIN and Firebase user UID in shared preferences.
 *   - Navigates the user to SurveyActivity after successful setup.
 *   - Displays error messages for invalid PINs.
 *   - Sets up an emergency exit button, if available.
 * UI components are accessed using ViewBinding via ActivityRegistrationPartTwoBinding.
 */
public class RegistrationPartTwoActivity extends AppCompatActivity {

    private ActivityRegistrationPartTwoBinding binding;
    private FirebaseAuth mAuth;

    /**
     * Called when the activity is first created.
     * This method:
     *   - Initializes ViewBinding and sets the content view.
     *   - Initializes FirebaseAuth instance.
     *   - Sets up the emergency exit button, if found in the layout.
     *   - Handles the logic for PIN submission and validation.
     *   - Clears error states when the PIN input field is modified.
     *
     * @param savedInstanceState If the activity is being re-initialized after being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
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
