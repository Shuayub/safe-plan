package com.example.weatherapp.RegistrationPartOne;

import android.util.Patterns;

import com.example.weatherapp.EmergencyExitButton;
import com.example.weatherapp.LoginEmail.LoginEmailActivityView;
import com.example.weatherapp.R;
import com.example.weatherapp.RegistrationPartTwoActivity;
import com.example.weatherapp.VerifyEmailActivity;

/**
 * RegistrationPartOneActivityPresenter serves as the Presenter in the MVP architecture
 * for the first registration screen. It handles validation of user inputs, delegates actions
 * to the model, and instructs the view to update the UI or transition screens based on outcomes.
 */
public class RegistrationPartOneActivityPresenter {

    RegistrationPartOneActivityModel model;
    RegistrationPartOneActivityView view;

    /**
     * Constructs the presenter with the given model and view.
     *
     * @param model the model instance handling logic
     * @param view  the view instance to update UI
     */
    public RegistrationPartOneActivityPresenter(RegistrationPartOneActivityModel model, RegistrationPartOneActivityView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Initiates the Google Sign-In flow by delegating to the model.
     */
    public void initiateGoogleSignIn() {
        model.startGoogleSignIn();
    }

    /**
     * Called when Google Sign-In succeeds.
     * Transitions the user to the next screen in registration (PIN creation).
     */
    void GoogleSignInSuccess() {
        view.sendToNextScreen(RegistrationPartTwoActivity.class);
    }

    /**
     * Called when Google Sign-In fails.
     * Displays a failure message under the confirm password field.
     */
    public void GoogleSignInFailure() {
        view.setConfirmPswdOutputText("Failed Google sign in. Try again.");
    }


    /**
     * Validates email and password input fields and triggers account creation if valid.
     *
     * @param email       the email address entered by the user
     * @param password    the password entered by the user
     * @param confirmPswd the password confirmation entered by the user
     */
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

    /**
     * Displays error or proceeds to the next screen based on email registration outcome.
     *
     * @param successful whether the operation was successful
     * @param message    the message to display if unsuccessful
     */
    public void setViewText(boolean successful, String message) {
        if (successful) {
            view.sendToNextScreen(LoginEmailActivityView.class);
        }
        else {
            view.setConfirmPswdOutputText(message);
        }
    }

    /**
     * Initializes the emergency exit button if it exists in the current view.
     */
    public void initiateEmergencyExit() {
        if (view.exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(view, view.exit_button);
        }
    }
}
