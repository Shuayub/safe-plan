package com.example.weatherapp.LoginEmail;

import android.util.Patterns;

import com.example.weatherapp.EmergencyExitButton;
import com.example.weatherapp.RegistrationPartTwoActivity;
import com.example.weatherapp.homeScreenActivity;

/**
 * LoginEmailActivityPresenter} acts as the Presenter in the Model-View-Presenter (MVP) architecture
 * for the login screen. It mediates the interaction between the LoginEmailActivityView
 * and the LoginEmailActivityModel.
 * Responsibilities include:
 *   - Validating user input (email and password).
 *   - Handling success/failure for email and Google sign-in.
 *   - Determining navigation flow based on whether a PIN has been created.
 *   - Initializing the emergency exit feature.
 */
public class LoginEmailActivityPresenter {

    LoginEmailActivityModel model;
    LoginEmailActivityView view;

    /**
     * Constructs a presenter with the given model and view.
     *
     * @param model the model handling authentication logic
     * @param view  the view interface for displaying results
     */
    public LoginEmailActivityPresenter(LoginEmailActivityModel model, LoginEmailActivityView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Called when Google Sign-In is successful.
     * Navigates to either the home screen or the PIN setup screen
     * based on whether the user has created a PIN.
     */
    void GoogleSignInSuccess() {
        if (model.checkPinCreated()) {
            view.sendToNextScreen(homeScreenActivity.class);
        }
        else {
            view.sendToNextScreen(RegistrationPartTwoActivity.class);
        }
    }

    /**
     * Called when Google Sign-In fails.
     * Updates the view with a failure message.
     */
    void GoogleSignInFailure() {
        view.setOutputText("Failed Google sign in. Try again.");
    }

    /**
     * Initiates email login validation and delegates authentication to the model.
     *
     * @param email    the user-entered email address
     * @param password the user-entered password
     */
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

    /**
     * Initiates the Google Sign-In flow by delegating to the model.
     */
    public void initiateGoogleSignIn() {
        model.setupGoogleSignIn(view);
    }

    /**
     * Called when email sign-in completes.
     * If successful, navigates the user to the appropriate next screen based on PIN creation status.
     * Otherwise, shows a generic error.
     *
     * @param successful true if authentication succeeded; false otherwise
     */
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

    /**
     * Sets up the emergency exit button functionality if the button is available in the view.
     */
    public void initiateEmergencyExit() {
        if (view.exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(view, view.exit_button);
        }
    }
}
