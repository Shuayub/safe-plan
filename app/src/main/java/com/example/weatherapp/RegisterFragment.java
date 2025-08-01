package com.example.weatherapp;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;

import androidx.credentials.CredentialManager;

import androidx.credentials.Credential;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;

import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weatherapp.databinding.RegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.util.Patterns;
import android.widget.Toast;

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class RegisterFragment extends GoogleSignIn {

    private RegistrationBinding binding;
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;
    private Executor executor;

    @Override
    public Context get_context() {
        return requireContext();
    }
    @Override
    public Activity get_activity() {
        return requireActivity();
    }
    @Override
    public void onSuccess() {
        NavHostFragment.findNavController(RegisterFragment.this)
                .navigate(R.id.action_register_to_create_pin);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = RegistrationBinding.inflate(inflater, container, false);
        createRequest();
        mAuth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGoogleSignIn();
            }
        });

        binding.RegisterButton.setOnClickListener(v -> {
                    String email = binding.emailEditText.getText().toString();
                    String password = binding.passwordEditText.getText().toString();
                    String confirmPassword = binding.ConfirmPasswordEditText.getText().toString();

                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.emailError.setVisibility(View.VISIBLE);
                        binding.emailEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        return;
                    }
                    else if (password.isEmpty()) {
                        binding.passwordError.setVisibility(View.VISIBLE);
                        binding.passwordEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        return;
                    }
                    else if (!password.equals(confirmPassword)) {
                        binding.confirmPasswordError.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Create user with email and password
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Registration successful, send email verification
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            sendEmailVerification(user);
                                        }
                                    } else {
                                        // Registration failed
                                        String errorMessage = "Registration failed.";
                                        if (task.getException() != null) {
                                            String exceptionMessage = task.getException().getMessage();
                                            if (exceptionMessage != null) {
                                                if (exceptionMessage.contains("email address is already in use")) {
                                                    errorMessage = "This email is already registered. Please use a different email or try logging in.";
                                                } else if (exceptionMessage.contains("Password should be at least 6 characters")) {
                                                    errorMessage = "Password should be at least 6 characters long.";
                                                } else if (exceptionMessage.contains("The email address is badly formatted")) {
                                                    errorMessage = "Please enter a valid email address.";
                                                }
                                            }
                                        }
                                        Toast.makeText(RegisterFragment.this.getContext(), errorMessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
        );

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

    // New method to send email verification
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mAuth.signOut();

                            NavHostFragment.findNavController(RegisterFragment.this)
                                    .navigate(R.id.action_register_to_verify);
                        } else {
                            Toast.makeText(RegisterFragment.this.getContext(),
                                    "Registration successful, but failed to send verification email. Please try logging in.",
                                    Toast.LENGTH_LONG).show();

                            NavHostFragment.findNavController(RegisterFragment.this)
                                    .navigate(R.id.action_register_to_login);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}