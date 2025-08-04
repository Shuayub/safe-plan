package com.example.weatherapp.LoginEmail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.RegistrationPartTwoActivity;
import com.example.weatherapp.databinding.ActivityLoginEmailBinding;
import com.example.weatherapp.tips;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginEmailActivity extends AppCompatActivity implements GoogleSignInFragment.GoogleSignInCallback {

    private ActivityLoginEmailBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInFragment googleSignInFragment;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        setupGoogleSignIn();

        binding.LoginButton.setOnClickListener(v -> handleEmailLogin());

        binding.googleSignInButton.setOnClickListener(v -> {
            if (googleSignInFragment != null) {
                googleSignInFragment.performGoogleSignIn();
            }
        });

        binding.emailEditText.setOnKeyListener((v, keyCode, event) -> {
            if (binding.emailError.getVisibility() == View.VISIBLE) {
                binding.emailEditText.setBackgroundTintList(null);
                binding.emailError.setVisibility(View.INVISIBLE);
            }
            return false;
        });
    }

    private void handleEmailLogin() {
        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailError.setVisibility(View.VISIBLE);
            binding.emailEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        if (sharedPreferences.getBoolean("createdPin", false)) {
                            startActivity(new Intent(this, tips.class));
                        }
                        else {
                            startActivity(new Intent(this, RegistrationPartTwoActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupGoogleSignIn() {
        googleSignInFragment = new GoogleSignInFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(googleSignInFragment, "GoogleSignInFragment")
                .commitNow();
        googleSignInFragment.setCallback(this);
    }

    // GoogleSignInCallback methods
    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
        if (sharedPreferences.getBoolean("createdPin", false)) {
            startActivity(new Intent(this, tips.class));
        }
        else {
            startActivity(new Intent(this, RegistrationPartTwoActivity.class));
        }
        finish();
    }

    @Override
    public void onGoogleSignInFailure(String reason) {
        Toast.makeText(this, "Google Sign-In failed: " + reason, Toast.LENGTH_SHORT).show();
    }
}
