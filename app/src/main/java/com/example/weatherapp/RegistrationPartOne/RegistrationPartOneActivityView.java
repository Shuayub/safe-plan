package com.example.weatherapp.RegistrationPartOne;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.databinding.ActivityRegistrationPartOneBinding;

public class RegistrationPartOneActivityView extends AppCompatActivity {

    private ActivityRegistrationPartOneBinding binding;
    GoogleSignInFragment googleSignInFragment;
    private RegistrationPartOneActivityPresenter presenter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEditor;


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
    }

    void setEmailOutputText(String text) {
        binding.emailError.setText(text);
    }

    void setPswdOutputText(String text) {
        binding.passwordError.setText(text);
    }

    void setConfirmPswdOutputText(String text) {
        binding.confirmPasswordError.setText(text);
    }

    void sendToNextScreen(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
