package com.example.weatherapp.RegistrationPartOne;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.LoginEmail.LoginEmailActivity;
import com.example.weatherapp.RegistrationPartTwoActivity;
import com.example.weatherapp.VerifyEmailActivity;
import com.example.weatherapp.databinding.ActivityRegistrationPartOneBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class RegistrationPartOneActivity extends AppCompatActivity {

    private ActivityRegistrationPartOneBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInFragment googleSignInFragment;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationPartOneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        // Attach the GoogleSignInFragment
        googleSignInFragment = new GoogleSignInFragment();
        getSupportFragmentManager().beginTransaction()
                .add(googleSignInFragment, "GoogleSignInFragment")
                .commit();

        googleSignInFragment.setCallback(new GoogleSignInFragment.GoogleSignInCallback() {
            @Override
            public void onGoogleSignInSuccess(FirebaseUser user) {
                Intent intent = new Intent(RegistrationPartOneActivity.this, RegistrationPartTwoActivity.class);
                editor.putBoolean("createdAccount", true);
                editor.apply();
                startActivity(intent);
                finish();
            }

            @Override
            public void onGoogleSignInFailure(String reason) {
                Toast.makeText(RegistrationPartOneActivity.this, reason, Toast.LENGTH_LONG).show();
            }
        });

        setupUI();
    }

    private void setupUI() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        binding.googleSignInButton.setOnClickListener(v -> {
            googleSignInFragment.performGoogleSignIn();
        });

        binding.RegisterButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            String confirmPassword = binding.ConfirmPasswordEditText.getText().toString();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailError.setVisibility(View.VISIBLE);
                binding.emailEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                return;
            } else if (password.isEmpty()) {
                binding.passwordError.setVisibility(View.VISIBLE);
                binding.passwordEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                return;
            } else if (!password.equals(confirmPassword)) {
                binding.confirmPasswordError.setVisibility(View.VISIBLE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    sendEmailVerification(user);
                                    editor.putBoolean("createdAccount", true);
                                    editor.apply();
                                }
                            } else {
                                String errorMessage = "Registration failed.";
                                if (task.getException() != null) {
                                    String ex = task.getException().getMessage();
                                    if (ex != null) {
                                        if (ex.contains("email address is already in use")) {
                                            errorMessage = "This email is already registered.";
                                        } else if (ex.contains("Password should be at least 6 characters")) {
                                            errorMessage = "Password should be at least 6 characters long.";
                                        } else if (ex.contains("badly formatted")) {
                                            errorMessage = "Please enter a valid email address.";
                                        }
                                    }
                                }
                                Toast.makeText(RegistrationPartOneActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        binding.emailEditText.setOnKeyListener((v, keyCode, event) -> {
            if (binding.emailError.getVisibility() == View.VISIBLE) {
                binding.emailEditText.setBackgroundTintList(null);
                binding.emailError.setVisibility(View.INVISIBLE);
            }
            return false;
        });

        binding.passwordEditText.setOnKeyListener((v, keyCode, event) -> {
            if (binding.passwordError.getVisibility() == View.VISIBLE) {
                binding.passwordEditText.setBackgroundTintList(null);
                binding.passwordError.setVisibility(View.INVISIBLE);
            }
            return false;
        });

        binding.ConfirmPasswordEditText.setOnKeyListener((v, keyCode, event) -> {
            if (binding.confirmPasswordError.getVisibility() == View.VISIBLE) {
                binding.textViewConfirmPswrd.setBackgroundTintList(null);
                binding.confirmPasswordError.setVisibility(View.INVISIBLE);
            }
            return false;
        });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mAuth.signOut();
                Intent intent = new Intent(RegistrationPartOneActivity.this, VerifyEmailActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegistrationPartOneActivity.this,
                        "Registration successful, but failed to send verification email. Try logging in.",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegistrationPartOneActivity.this, LoginEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
