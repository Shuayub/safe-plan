package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Displays support services information for the selected city.
 * Fetches the selected city from Firebase and displays links and phone numbers loaded from a local JSON file.
 */
public class SupportActivity extends AppCompatActivity {

    private String selectedCity = "Toronto";  // Default fallback city

    private TextView title, subtitle;
    private TextView victimServicesText, hotlineText, sheltersText, legalAidText, policeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // Set up bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_support);

        // Bind views
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        victimServicesText = findViewById(R.id.victimServicesText);
        hotlineText = findViewById(R.id.hotlineText);
        sheltersText = findViewById(R.id.sheltersText);
        legalAidText = findViewById(R.id.legalAidText);
        policeText = findViewById(R.id.policeText);

        // Load selected city from Firebase and populate UI
        fetchSelectedCityFromFirebase();

        // Set up emergency exit button
        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }
    }

    /**
     * Fetches the selected city from Firebase using the logged-in user's UID.
     */
    private void fetchSelectedCityFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference cityRef = FirebaseDatabase.getInstance()
                    .getReference("surveyAnswers")
                    .child(uid)
                    .child("pageAnswers")
                    .child("1")
                    .child("P1Q2"); // Adjust path as needed

            cityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String city = snapshot.getValue(String.class);
                    if (city != null && !city.isEmpty()) {
                        selectedCity = city;
                    }
                    updateUIWithCityData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Use default city on error
                    updateUIWithCityData();
                }
            });
        }
    }

    /**
     * Updates the UI with support information based on the selected city.
     * Loads data from a JSON asset and sets clickable links and dialable numbers.
     */
    private void updateUIWithCityData() {
        runOnUiThread(() -> {
            try {
                JSONObject allCities = new JSONObject(loadJSONFromAsset("support.json"));

                if (!allCities.has(selectedCity)) {
                    throw new Exception("City not found: " + selectedCity);
                }

                JSONObject data = allCities.getJSONObject(selectedCity);

                HashMap<String, String> supportMap = new HashMap<>();
                supportMap.put("Local Services", data.optString("Local Services", "N/A"));
                supportMap.put("Hotline", data.optString("Hotline", "N/A"));
                supportMap.put("Shelters", data.optString("Shelters", "N/A"));
                supportMap.put("Legal Aid", data.optString("Legal Aid", "N/A"));
                supportMap.put("Police", data.optString("Police", "N/A"));

                title.setText(selectedCity + " Support Services");
                subtitle.setText("Below are local resources:");

                makeLink(victimServicesText, "Local Services", supportMap.get("Local Services"));
                makePhoneLink(hotlineText, "Hotline", supportMap.get("Hotline"));
                makeLink(sheltersText, "Shelters", supportMap.get("Shelters"));
                makePhoneLink(legalAidText, "Legal Aid", supportMap.get("Legal Aid"));
                makePhoneLink(policeText, "Police", supportMap.get("Police"));

            } catch (Exception e) {
                title.setText("Error");
                subtitle.setText("Couldn't load support data.");
                victimServicesText.setText(e.getMessage());
            }
        });
    }

    /**
     * Sets a clickable URL link on a TextView.
     * @param view TextView to set the link on
     * @param label The label (e.g., "Local Services")
     * @param url The URL string to open
     */
    @SuppressLint("SetTextI18n")
    private void makeLink(TextView view, String label, String url) {
        if (url != null && url.startsWith("http")) {
            SpannableString spannable = new SpannableString(label + ": " + url);
            spannable.setSpan(new URLSpan(url),
                    label.length() + 2, spannable.length(), 0);
            view.setText(spannable);
            view.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            view.setText(label + ": " + (url != null ? url : "N/A"));
        }
    }

    /**
     * Sets a clickable phone number on a TextView that opens the dialer.
     * @param view TextView to set the number on
     * @param label The label (e.g., "Hotline")
     * @param phoneNumber The phone number string (e.g., "tel:416-123-4567")
     */
    @SuppressLint("SetTextI18n")
    private void makePhoneLink(TextView view, String label, String phoneNumber) {
        if (phoneNumber != null && phoneNumber.startsWith("tel:")) {
            String display = phoneNumber.replace("tel:", "");
            SpannableString spannable = new SpannableString(label + ": " + display);
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(phoneNumber));
                    startActivity(intent);
                }
            }, label.length() + 2, spannable.length(), 0);
            view.setText(spannable);
            view.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            view.setText(label + ": " + (phoneNumber != null ? phoneNumber : "N/A"));
        }
    }

    /**
     * Loads a JSON string from the assets folder.
     * @param filename Name of the file to load (e.g., "support.json")
     * @return JSON string or null on failure
     */
    private String loadJSONFromAsset(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
