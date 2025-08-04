package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InfoActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    private Button addContactBtn, addDocumentBtn, addMedicationBtn, addLocationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_info);

        addContactBtn = findViewById(R.id.add_contact_button);
        addDocumentBtn = findViewById(R.id.add_document_button);
        addMedicationBtn = findViewById(R.id.add_medication_button);
        addLocationBtn = findViewById(R.id.add_location_button);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String uid = prefs.getString("userUID", "xw5kWxTsebgdWJHHVOKfBIbxAwR2");
        // TODO: Replace these with real logic
        addContactBtn.setOnClickListener(v -> {
            // Example: startActivity(new Intent(this, AddContactActivity.class));
        });

        addDocumentBtn.setOnClickListener(v -> {
            // Example: open file picker
        });

        addMedicationBtn.setOnClickListener(v -> {
            // Example: open medication form
        });

        addLocationBtn.setOnClickListener(v -> {
            // Example: open location add screen
        });


    }




}