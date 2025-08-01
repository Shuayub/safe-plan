package com.example.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.weatherapp.databinding.LoginEmailBinding;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginEmailFragment extends GoogleSignIn {

    private LoginEmailBinding binding;
    private FirebaseAuth mAuth;

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
        NavHostFragment.findNavController(LoginEmailFragment.this)
                .navigate(R.id.action_login_email_to_tips);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = LoginEmailBinding.inflate(inflater, container, false);
        createRequest();
        mAuth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.LoginButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailError.setVisibility(View.VISIBLE);
                binding.emailEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                return;
            }

            mAuth = FirebaseAuth.getInstance();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(requireContext(), "Authentication Success.",
                                        Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(LoginEmailFragment.this)
                                        .navigate(R.id.action_first_login_email_to_create_pin);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(requireContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
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

        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGoogleSignIn();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}