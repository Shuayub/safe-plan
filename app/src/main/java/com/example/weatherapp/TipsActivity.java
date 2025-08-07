package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends AppCompatActivity {
    private static final String TIPS_FILE = "tips.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_plan);

        RecyclerView recyclerView = findViewById(R.id.tipsRecyclerView);
        Button backButton = findViewById(R.id.actionButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TipsActivity.this, SurveyActivity.class);
            startActivity(intent);
        });
        TextView emptyView = findViewById(R.id.emptyTextView);

        List<String> tips = loadSavedTips();

        if (tips.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new TipsAdapter(tips));
        }


    }

    private List<String> loadSavedTips() {
        List<String> tips = new ArrayList<>();
        try (FileInputStream fis = this.openFileInput(TIPS_FILE)) {
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonStr);
            JSONArray tipsArray = json.getJSONArray("tips");

            for (int i = 0; i < tipsArray.length(); i++) {
                tips.add(tipsArray.getString(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return tips;
    }
}
