package com.example.weatherapp.RegistrationPartOne;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.EmergencyExitButton;
import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityRegistrationPartOneBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RegistrationPartOneActivityView extends AppCompatActivity {

    private ActivityRegistrationPartOneBinding binding;
    GoogleSignInFragment googleSignInFragment;
    private RegistrationPartOneActivityPresenter presenter;
    FloatingActionButton exit_button;


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

    public void setEmailOutputText(String text) {
        binding.emailError.setText(text);
    }

    public void setPswdOutputText(String text) {
        binding.passwordError.setText(text);
    }

    public void setConfirmPswdOutputText(String text) {
        binding.confirmPasswordError.setText(text);
    }

    void sendToNextScreen(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
