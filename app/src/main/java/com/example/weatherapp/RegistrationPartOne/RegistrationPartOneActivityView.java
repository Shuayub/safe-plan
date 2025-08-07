package com.example.weatherapp.RegistrationPartOne;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.EmergencyExitButton;
import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityRegistrationPartOneBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * RegistrationPartOneActivityView is the UI screen for the first part of user registration.
 * It allows users to register via:
 *   - Email and password</li>
 *   - Google Sign-In using GoogleSignInFragment
 * This class serves as the "View" in the MVP pattern, delegating logic to
 * RegistrationPartOneActivityPresenter.
 */
public class RegistrationPartOneActivityView extends AppCompatActivity {

    private ActivityRegistrationPartOneBinding binding;
    GoogleSignInFragment googleSignInFragment;
    private RegistrationPartOneActivityPresenter presenter;
    FloatingActionButton exit_button;


    /**
     * Method called when the activity is created.
     * Initializes the view binding, presenter, button click listeners,
     * and emergency exit setup.
     *
     * @param savedInstanceState Bundle containing previous state (if any).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationPartOneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new RegistrationPartOneActivityPresenter(new RegistrationPartOneActivityModel(
                this), this);

        binding.googleSignInButton.setOnClickListener(v -> {
            presenter.initiateGoogleSignIn();
        });

        binding.RegisterButton.setOnClickListener(v -> presenter.initiateEmailLogin(
                        binding.emailEditText.getText().toString(),
                        binding.passwordEditText.getText().toString(),
                        binding.confirmPasswordEditText.getText().toString()
                )
        );

        exit_button = findViewById(R.id.emergency_exit_button);
        presenter.initiateEmergencyExit();
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }
    }

    /**
     * Displays an error message below the email field.
     *
     * @param text the error message to display.
     */
    public void setEmailOutputText(String text) {
        binding.emailError.setText(text);
    }

    /**
     * Displays an error message below the password field.
     *
     * @param text the error message to display.
     */
    public void setPswdOutputText(String text) {
        binding.passwordError.setText(text);
    }

    /**
     * Displays an error message below the confirm password field.
     *
     * @param text the error message to display.
     */
    public void setConfirmPswdOutputText(String text) {
        binding.confirmPasswordError.setText(text);
    }

    /**
     * Navigates to another activity screen.
     *
     * @param cls the class of the destination activity.
     */
    void sendToNextScreen(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
