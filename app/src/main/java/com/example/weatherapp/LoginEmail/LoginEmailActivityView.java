package com.example.weatherapp.LoginEmail;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.EmergencyExitButton;
import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityLoginEmailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * LoginEmailActivityView serves as the View in the MVP architecture for email and Google sign-in.
 * It provides:
 *   - User interface for email/password and Google authentication.
 *   - Delegation of input handling to the LoginEmailActivityPresenter.
 *   - Error output and screen navigation.
 * View elements are accessed via ViewBinding for type-safe interaction.
 */
public class LoginEmailActivityView extends AppCompatActivity {

    public GoogleSignInFragment googleSignInFragment;
    private ActivityLoginEmailBinding binding;
    private LoginEmailActivityPresenter presenter;
    FloatingActionButton exit_button;

    /**
     * Called when the activity is starting.
     * Sets up:
     *   - ViewBinding and layout content.
     *   - Login button click listeners for email/password and Google sign-in.
     *   - Emergency exit logic and UI button if available.
     * @param savedInstanceState Bundle containing saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new LoginEmailActivityPresenter(new LoginEmailActivityModel(this), this);

        binding.LoginButton.setOnClickListener(v -> presenter.initiateEmailLogin(
                        binding.emailEditText.getText().toString(),
                        binding.passwordEditText.getText().toString()
                )
        );

        binding.googleSignInButton.setOnClickListener(v -> {
            presenter.initiateGoogleSignIn();
        });

        exit_button = findViewById(R.id.emergency_exit_button);
        presenter.initiateEmergencyExit();
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }
    }

    /**
     * Displays an error or status message on the UI.
     *
     * @param text the message to show in the error TextView.
     */
    public void setOutputText(String text) {
        binding.error.setText(text);
    }

    /**
     * Starts a new activity and finishes the current one.
     * Typically called after successful login.
     *
     * @param cls the target activity class to navigate to.
     */
    void sendToNextScreen(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
