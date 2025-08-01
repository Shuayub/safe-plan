package com.example.weatherapp;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

abstract public class GoogleSignIn extends Fragment {

    public Executor executor;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CredentialManager credentialManager;
    private GetCredentialRequest request;

    abstract public Context get_context();

    abstract public Activity get_activity();

    abstract public void onSuccess();

    void performGoogleSignIn() {
        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.getCredentialAsync(
                get_context(),
                request,
                cancellationSignal,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // Switch to main thread for UI operations
                        get_activity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleSignIn(result.getCredential());
                            }
                        });
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        // Switch to main thread for UI operations
                        get_activity().runOnUiThread(new Runnable() {
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

                                Toast.makeText(get_context(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
    }

    void createRequest() {
        // Instantiate a Google sign-in request
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.client_id))
                .setAutoSelectEnabled(false)
                .build();

        request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager = CredentialManager.create(get_context());
    }

    private void handleSignIn(Credential credential) {
        // Check if credential is of type Google ID
        if (credential instanceof CustomCredential
                && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            CustomCredential customCredential = (CustomCredential) credential;
            // Create Google ID Token
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            // Handle unexpected credential type
            Toast.makeText(get_context(), "Unexpected credential type", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(get_activity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onSuccess();
                        } else {
                            Toast.makeText(get_context(), "Firebase authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
