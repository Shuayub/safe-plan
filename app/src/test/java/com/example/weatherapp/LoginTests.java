package com.example.weatherapp;

import static org.mockito.Mockito.verify;

import com.example.weatherapp.LoginEmail.LoginEmailActivityModel;
import com.example.weatherapp.LoginEmail.LoginEmailActivityPresenter;
import com.example.weatherapp.LoginEmail.LoginEmailActivityView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LoginTests {

    @Mock
    LoginEmailActivityModel model;
    @Mock
    LoginEmailActivityView view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onEmptyUser() {
        LoginEmailActivityPresenter presenter = new LoginEmailActivityPresenter(model, view);
        presenter.initiateEmailLogin("","");
        verify(view).setOutputText("Please enter a valid email address");
    }

    @Test
    public void onEmptyPassword() {
        LoginEmailActivityPresenter presenter = new LoginEmailActivityPresenter(model, view);
        presenter.initiateEmailLogin("unknown@gmail.com","");
        verify(view).setOutputText("Please check your entries");
    }
}
