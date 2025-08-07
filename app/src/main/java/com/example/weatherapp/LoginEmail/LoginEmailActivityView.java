package com.example.weatherapp.LoginEmail;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.EmergencyExitButton;
import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityLoginEmailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LoginEmailActivityView extends AppCompatActivity {

    public GoogleSignInFragment googleSignInFragment;
    private ActivityLoginEmailBinding binding;
    private LoginEmailActivityPresenter presenter;
    FloatingActionButton exit_button;

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

    public void setOutputText(String text) {
        binding.error.setText(text);
    }

    void sendToNextScreen(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
