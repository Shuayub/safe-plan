package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class homeScreenActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

}