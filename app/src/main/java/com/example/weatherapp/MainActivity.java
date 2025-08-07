package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
    Main launcher activity for the app.
    Displays a button that allows the user to start the survey.
    Handles click events to navigate to the SurveyActivity.
**/
public class MainActivity extends AppCompatActivity {


    /**
    Initializes the activity.
    Sets the layout content and sets up a click listener on the “Start Survey” button.
    When clicked, the button launches the SurveyActivity.

    Parameters:
    savedInstanceState - Bundle containing the activity’s previously saved state (if any)
**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startSurveyButton = findViewById(R.id.startSurveyButton);
        startSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the SurveyActivity
                Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
                startActivity(intent);
            }
        });
    }
}