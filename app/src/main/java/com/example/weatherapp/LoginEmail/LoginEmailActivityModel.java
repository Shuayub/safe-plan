package com.example.weatherapp.LoginEmail;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.weatherapp.GoogleSignInFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Context;

public class LoginEmailActivityModel implements GoogleSignInFragment.GoogleSignInCallback{

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    LoginEmailActivityView view;


    public LoginEmailActivityModel(LoginEmailActivityView view) {
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view);
        this.view = view;
    }

    void setupGoogleSignIn(LoginEmailActivityView view) {
        view.googleSignInFragment = new GoogleSignInFragment();
        view.getSupportFragmentManager()
                .beginTransaction()
                .add(view.googleSignInFragment, "GoogleSignInFragment")
                .commitNow();
        view.googleSignInFragment.setCallback(this);
        view.googleSignInFragment.performGoogleSignIn();
    }

    void handleEmailLogin(LoginEmailActivityPresenter presenter, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    presenter.onEmailSignIn(task.isSuccessful());
                });
    }
    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        LoginEmailActivityPresenter presenter = new LoginEmailActivityPresenter(this, view);
        presenter.GoogleSignInSuccess();
        view.finish();
    }

    @Override
    public void onGoogleSignInFailure(String reason) {
        LoginEmailActivityPresenter presenter = new LoginEmailActivityPresenter(this, view);
        presenter.GoogleSignInFailure();
    }

    public boolean checkPinCreated() {
        return(sharedPreferences.getBoolean("CreatedPin", false));
    }
}
