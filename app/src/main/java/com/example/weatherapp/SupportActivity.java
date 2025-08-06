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

import com.example.weatherapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SupportActivity extends AppCompatActivity {

    private final String selectedCity = "Toronto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support); // renamed XML layout

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_support);

        TextView title = findViewById(R.id.title);
        TextView subtitle = findViewById(R.id.subtitle);

        TextView victimServicesText = findViewById(R.id.victimServicesText);
        TextView hotlineText = findViewById(R.id.hotlineText);
        TextView sheltersText = findViewById(R.id.sheltersText);
        TextView legalAidText = findViewById(R.id.legalAidText);
        TextView policeText = findViewById(R.id.policeText);

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

        FloatingActionButton exit_button = findViewById(R.id.emergency_exit_button);
        if (exit_button != null) {
            EmergencyExitButton.setupEmergencyExit(this, exit_button);
        }
    }

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