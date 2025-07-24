package com.example.weatherapp;

import android.content.res.ColorStateList;
import androidx.credentials.CredentialManager;

import androidx.credentials.Credential;
import androidx.credentials.GetCredentialRequest;

import androidx.credentials.GetCredentialResponse;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weatherapp.databinding.RegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.firebase.auth.GoogleAuthProvider;


public class RegisterFragment extends Fragment {

    private RegistrationBinding binding;
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = RegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        binding.googleSignInButton.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            credentialManager = CredentialManager.create(requireContext());

            ImageButton googleButton = binding.googleSignInButton;
            googleButton.setOnClickListener(view1 -> launchGoogleSignIn());
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

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterFragment.this.getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                    }
                });
                NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_register_to_login);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void launchGoogleSignIn() {
        String clientId = getString(R.string.default_web_client_id);

        GetGoogleIdOption googleOptions = new GetGoogleIdOption.Builder()
                .setServerClientId(clientId)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleOptions)
                .build();
    }

    private void handleCredential(GetCredentialResponse response) {
        Credential credential = response.getCredential();

        if (credential instanceof GoogleIdTokenCredential) {
            String idToken = ((GoogleIdTokenCredential) credential).getIdToken();
            firebaseAuthWithGoogle(idToken);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // EDITTTT FOR MAIN ACTIVITY
                    }
                });
    }
}
