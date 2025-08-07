package com.example.weatherapp.LoginEmail;

import android.util.Patterns;

import com.example.weatherapp.EmergencyExitButton;
import com.example.weatherapp.RegistrationPartTwoActivity;
import com.example.weatherapp.homeScreenActivity;

public class LoginEmailActivityPresenter {

    LoginEmailActivityModel model;
    LoginEmailActivityView view;

    public LoginEmailActivityPresenter(LoginEmailActivityModel model, LoginEmailActivityView view) {
        this.model = model;
        this.view = view;
    }
    void GoogleSignInSuccess() {
        if (model.checkPinCreated()) {
            view.sendToNextScreen(homeScreenActivity.class);
        }
        else {
            view.sendToNextScreen(RegistrationPartTwoActivity.class);
        }
    }

    void GoogleSignInFailure() {
        view.setOutputText("Failed Google sign in. Try again.");
    }

    public void initiateEmailLogin(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            view.setOutputText("Please enter a valid email address");
        }
        else if (password.isEmpty()) {
            view.setOutputText("Please check your entries");
        }
        else {
            model.handleEmailLogin(this, email, password);
        }
    }

    public void initiateGoogleSignIn() {
        model.setupGoogleSignIn(view);
    }

    public void onEmailSignIn(boolean successful) {
        if (successful) {
            if (model.checkPinCreated()) {
                view.sendToNextScreen(homeScreenActivity.class);
            }
            else {
                view.sendToNextScreen(RegistrationPartTwoActivity.class);
            }
        }
        else {
            view.setOutputText("Please check your entries");
        }
    }

    public void initiateEmergencyExit() {
        if (view.exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(view, view.exit_button);
        }
    }
}
