package com.example.weatherapp.RegistrationPartOne;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.weatherapp.GoogleSignInFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationPartOneActivityModel implements GoogleSignInFragment.GoogleSignInCallback {

    private FirebaseAuth mAuth;
    private RegistrationPartOneActivityView view;

    public RegistrationPartOneActivityModel(RegistrationPartOneActivityView view) {
        mAuth = FirebaseAuth.getInstance();
        view.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view);
        view.prefEditor = view.sharedPreferences.edit();
        this.view = view;

        view.googleSignInFragment = new GoogleSignInFragment();
        this.view.getSupportFragmentManager().beginTransaction()
                .add(view.googleSignInFragment, "GoogleSignInFragment")
                .commit();

        view.googleSignInFragment.setCallback(this);
    }

    public void startGoogleSignIn(){
        view.googleSignInFragment.performGoogleSignIn();
    }

    public void startEmailRegistration(String email, String password) {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(this, view);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) sendEmailVerification(user);
                        } else {
                            if (task.getException() != null) {
                                String ex = task.getException().getMessage();
                                if (ex != null) {
                                    if (ex.contains("email address is already in use")) {
                                        presenter.setViewText(false, "This email is already registered");
                                    } else if (ex.contains("Password should be at least 6 characters")) {
                                        presenter.setViewText(false, "Password should be at least 6 characters long");
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(this, view);
        presenter.GoogleSignInSuccess();
        view.prefEditor.putBoolean("createdAccount", true);
        view.prefEditor.apply();
        view.finish();
    }

    @Override
    public void onGoogleSignInFailure(String reason) {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(this, view);
        presenter.GoogleSignInFailure();
    }

    private void sendEmailVerification(FirebaseUser user) {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(this, view);
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mAuth.signOut();
                presenter.setViewText(true, "");
            } else {
                presenter.setViewText(false, "Failed to Send Email Verification");
            }
        });
    }
}
