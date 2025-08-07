package com.example.weatherapp;

import static org.mockito.Mockito.verify;

import com.example.weatherapp.RegistrationPartOne.RegistrationPartOneActivityModel;
import com.example.weatherapp.RegistrationPartOne.RegistrationPartOneActivityPresenter;
import com.example.weatherapp.RegistrationPartOne.RegistrationPartOneActivityView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RegistrationTests {

    @Mock
    RegistrationPartOneActivityModel model;
    @Mock
    RegistrationPartOneActivityView view;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onEmptyUser() {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(model, view);
        presenter.initiateEmailLogin("","","");
        verify(view).setEmailOutputText("Please enter a valid email address");
    }

    @Test
    public void onEmptyPassword() {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(model, view);
        presenter.initiateEmailLogin("unknown@gmail.com","","");
        verify(view).setPswdOutputText("Password should be at least 6 characters long");
    }

    @Test
    public void onPswdNotMatcingConfirmPswd() {
        RegistrationPartOneActivityPresenter presenter = new RegistrationPartOneActivityPresenter(model, view);
        presenter.initiateEmailLogin("unknown@gmail.com","CSCB07","CSCB09");
        verify(view).setConfirmPswdOutputText("Password do not match");
    }
}
