package com.example.weatherapp.RegistrationPartOne;

import androidx.annotation.NonNull;

import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * RegistrationPartOneActivityModel} handles the business logic for the
 * first part of the registration process.
 * It supports both email/password account creation and Google Sign-In using
 * Firebase Authentication. It also sends email verification upon successful
 * registration.
 * This class also implements GoogleSignInFragment.GoogleSignInCallback
 * to handle results of the Google sign-in flow.
 */
public class RegistrationPartOneActivityModel implements GoogleSignInFragment.GoogleSignInCallback {

    private FirebaseAuth mAuth;
    private RegistrationPartOneActivityView view;

    /**
     * Constructor that initializes FirebaseAuth and sets up the Google sign-in fragment.
     *
     * @param view The registration view used to manage UI and fragment transactions.
     */
    public RegistrationPartOneActivityModel(RegistrationPartOneActivityView view) {
        mAuth = FirebaseAuth.getInstance();
        this.view = view;

        view.googleSignInFragment = new GoogleSignInFragment();
        this.view.getSupportFragmentManager().beginTransaction()
                .add(view.googleSignInFragment, "GoogleSignInFragment")
                .commit();

        view.googleSignInFragment.setCallback(this);
    }

    /**
     * Starts the Google sign-in process via the embedded GoogleSignInFragment.
     */
    public void startGoogleSignIn(){
        view.googleSignInFragment.performGoogleSignIn();
    }

    /**
     * Starts the email/password account creation flow.
     * On success, a verification email is sent to the user. On failure,
     * appropriate error messages are sent to the presenter.
     *
     * @param email    The user's email.
     * @param password The user's chosen password.
     */
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

    /**
     * Callback invoked when Google Sign-In succeeds.
     * Saves a flag in shared preferences and navigates the user forward.
     *
     * @param user The authenticated Firebase user.
     */
    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(this, view);
        presenter.GoogleSignInSuccess();
        SharedPreferenceHelper.editor.putBoolean("createdAccount", true);
        SharedPreferenceHelper.editor.apply();
        view.finish();
    }

    /**
     * Callback invoked when Google Sign-In fails.
     *
     * @param reason A description of why the sign-in failed.
     */
    @Override
    public void onGoogleSignInFailure(String reason) {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(this, view);
        presenter.GoogleSignInFailure();
    }

    /**
     * Sends a verification email to the newly registered Firebase user.
     * On success, the user is signed out. On failure, an error message is shown.
     *
     * @param user The newly registered user who requires email verification.
     */
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
