package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.LoginEmail.LoginEmailActivityView;
import com.example.weatherapp.databinding.ActivityVerifyEmailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * VerifyEmailActivity prompts the user to verify their email address
 * after registration and provides navigation to the login screen.
 * This activity:
 *   - Displays a message informing the user that email verification is required.
 *   - Provides a button to navigate to LoginEmailActivityView once verification is complete.
 *   - Optionally configures an emergency exit button if it's available in the layout.
 * The UI is set up using ViewBinding via ActivityVerifyEmailBinding.
 */
public class VerifyEmailActivity extends AppCompatActivity {

    private ActivityVerifyEmailBinding binding;

    /**
     * Called when the activity is first created.
     * This method:
     *   - Inflates the view using ActivityVerifyEmailBinding and sets it as the content view.
     *   - Initializes a button that, when clicked, navigates the user to LoginEmailActivityView.
     *   - Initializes and sets up the emergency exit button, if present in the layout.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
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
