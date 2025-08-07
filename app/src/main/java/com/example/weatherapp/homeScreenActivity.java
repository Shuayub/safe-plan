package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



/**
 * homeScreenActivity represents the main home screen of the app once the user is logged in.
 * This activity:
 *     - Initializes the layout using ViewBinding via ActivityHomeBinding.
 *     - Sets up a BottomNavigationView using NavigationButton
 *     - Ensures the home navigation item is selected by default.
 *     - Initializes and configures the emergency exit button if it is present in the layout.
 * This class assumes the layout contains a bottom navigation view with the ID bottom_navigation
 * and a floating action button with the ID emergency_exit_button.
 */
public class homeScreenActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    /**
     * Called when the activity is first created.
     * This method:
     *     - Inflates the layout using ActivityHomeBinding.
     *     - Sets the content view to the root of the binding.
     *     - Initializes and configures the bottom navigation view for in-app navigation.
     *     - Initializes and configures the emergency exit button, if available.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }
    }

}