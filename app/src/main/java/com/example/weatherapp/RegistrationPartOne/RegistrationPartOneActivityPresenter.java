package com.example.weatherapp.RegistrationPartOne;

import android.util.Patterns;

import com.example.weatherapp.RegistrationPartTwoActivity;
import com.example.weatherapp.VerifyEmailActivity;

public class RegistrationPartOneActivityPresenter {

    RegistrationPartOneActivityModel model;
    RegistrationPartOneActivityView view;

    public RegistrationPartOneActivityPresenter(RegistrationPartOneActivityModel model, RegistrationPartOneActivityView view) {
        this.model = model;
        this.view = view;
    }

    public void initiateGoogleSignIn() {
        model.startGoogleSignIn();
    }

    void GoogleSignInSuccess() {
        view.sendToNextScreen(RegistrationPartTwoActivity.class);
    }

    public void GoogleSignInFailure() {
        view.setConfirmPswdOutputText("Failed Google sign in. Try again.");
    }


    public void initiateEmailLogin(String email, String password, String confirmPswd) {
        if (email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email).matches()))
        {
            view.setEmailOutputText("Please enter a valid email address");
        }
        else if (password.isEmpty()) {
            view.setPswdOutputText("Password should be at least 6 characters long");
        }
        else if (!password.equals(confirmPswd)) {
            view.setConfirmPswdOutputText("Password do not match");
        }
        else {
            model.startEmailRegistration(email, password);
        }
    }

    public void setViewText(boolean successful, String message) {
        if (successful) {
            view.sendToNextScreen(VerifyEmailActivity.class);
        }
        else {
            view.setConfirmPswdOutputText(message);
        }
    }
}
