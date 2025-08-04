package com.example.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoogleSignInFragment extends Fragment {

    public interface GoogleSignInCallback {
        void onGoogleSignInSuccess(FirebaseUser user);
        void onGoogleSignInFailure(String reason);
    }

    private GoogleSignInCallback callback;

    private FirebaseAuth mAuth;
    private Executor executor;
    private CredentialManager credentialManager;
    private GetCredentialRequest request;

    public void setCallback(GoogleSignInCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mAuth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createRequest();
    }

    public void performGoogleSignIn() {
        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                cancellationSignal,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // Switch to main thread for UI operations
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleSignIn(result.getCredential());
                            }
                        });
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        // Switch to main thread for UI operations
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Check for specific error types
                                String errorMessage = "Google Sign-In failed: " + e.getMessage();

                                // Handle specific error cases
                                if (e.getMessage() != null) {
                                    if (e.getMessage().contains("No credentials available")) {
                                        errorMessage = "No Google accounts found. Please add a Google account to your device first.";
                                    } else if (e.getMessage().contains("user canceled")) {
                                        errorMessage = "Sign-in was canceled.";
                                    } else if (e.getMessage().contains("network")) {
                                        errorMessage = "Network error. Please check your connection.";
                                    }
                                }

                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
    }

    private void createRequest() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.client_id))
                .setAutoSelectEnabled(false)
                .build();

        request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager = CredentialManager.create(requireContext());
    }

    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential
                && credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            CustomCredential customCredential = (CustomCredential) credential;
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);
            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            Toast.makeText(requireContext(), "Unexpected credential type", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onGoogleSignInFailure("Unexpected credential type");
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (callback != null && user != null) {
                                callback.onGoogleSignInSuccess(user);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Firebase authentication failed", Toast.LENGTH_SHORT).show();
                            if (callback != null) callback.onGoogleSignInFailure("Firebase authentication failed");
                        }
                    }
                });
    }
}
