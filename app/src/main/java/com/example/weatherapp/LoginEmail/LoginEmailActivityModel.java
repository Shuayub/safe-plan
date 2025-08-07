package com.example.weatherapp.LoginEmail;

import com.example.weatherapp.GoogleSignInFragment;
import com.example.weatherapp.SharedPreferenceHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LoginEmailActivityModel handles the business logic related to user authentication
 * using email/password and Google Sign-In. It communicates with Firebase Authentication services
 * and relays the outcome to the LoginEmailActivityPresenter and LoginEmailActivityView.
 * This class also implements GoogleSignInFragment.GoogleSignInCallback
 * to handle the outcome of the Google sign-in process.
 */
public class LoginEmailActivityModel implements GoogleSignInFragment.GoogleSignInCallback {

    private FirebaseAuth mAuth;
    LoginEmailActivityView view;


    /**
     * Constructs a new LoginEmailActivityModel with the given View.
     *
     * @param view the View interface for interacting with the UI.
     */
    public LoginEmailActivityModel(LoginEmailActivityView view) {
        mAuth = FirebaseAuth.getInstance();
        this.view = view;
    }

    /**
     * Initializes and performs the Google sign-in flow.
     * Adds the GoogleSignInFragment to the view's FragmentManager and starts the sign-in process.
     *
     * @param view the current View, used to access FragmentManager and store the sign-in fragment.
     */
    void setupGoogleSignIn(LoginEmailActivityView view) {
        view.googleSignInFragment = new GoogleSignInFragment();
        view.getSupportFragmentManager()
                .beginTransaction()
                .add(view.googleSignInFragment, "GoogleSignInFragment")
                .commitNow();
        view.googleSignInFragment.setCallback(this);
        view.googleSignInFragment.performGoogleSignIn();
    }


    /**
     * Attempts to sign in the user using email and password via Firebase Authentication.
     * Upon completion, the result is passed to the presenter.
     * @param presenter the Presenter instance to handle the sign-in result.
     * @param email     the user’s email address.
     * @param password  the user’s password.
     */
    void handleEmailLogin(LoginEmailActivityPresenter presenter, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    presenter.onEmailSignIn(task.isSuccessful());
                });
    }

    /**
     * Called when Google Sign-In is successful.
     * Creates a new presenter instance and notifies the view and presenter of the success.
     *
     * @param user the signed-in Firebase user.
     */
    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        LoginEmailActivityPresenter presenter = new LoginEmailActivityPresenter(this, view);
        presenter.GoogleSignInSuccess();
        view.finish();
    }

    /**
     * Called when Google Sign-In fails.
     * Notifies the presenter about the failure reason.
     *
     * @param reason the reason for the failure.
     */
    @Override
    public void onGoogleSignInFailure(String reason) {
        LoginEmailActivityPresenter presenter = new LoginEmailActivityPresenter(this, view);
        presenter.GoogleSignInFailure();
    }

    /**
     * Checks if the user has previously created a PIN.
     *
     * @return true if a PIN exists in shared preferences, false otherwise.
     */
    public boolean checkPinCreated() {
        return(SharedPreferenceHelper.verifyPinCreated());
    }
}
