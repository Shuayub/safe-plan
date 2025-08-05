package com.example.weatherapp.LoginEmail;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.RegistrationPartTwoActivity;
import com.example.weatherapp.databinding.ActivityLoginEmailBinding;

public class LoginEmailActivityView extends AppCompatActivity {

    public GoogleSignInFragment googleSignInFragment;
    private ActivityLoginEmailBinding binding;
    private LoginEmailActivityPresenter presenter;

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
    }

    public void setOutputText(String text) {
        binding.error.setText(text);
    }

    void sendToNextScreen(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
