package com.example.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TipsFragment extends Fragment {

    private static final String TIPS_FILE = "tips.json";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tipsContent = view.findViewById(R.id.tipsContentTextView);
        Button backButton = view.findViewById(R.id.actionButton);

        // Load and display tips
        List<String> tips = loadSavedTips();
        if (tips.isEmpty()) {
            tipsContent.setText("No safety tips available. Please complete the survey first.");
        } else {
            StringBuilder formattedTips = new StringBuilder();
            for (int i = 0; i < tips.size(); i++) {
                formattedTips.append(i + 1).append(". ").append(tips.get(i)).append("\n\n");
            }
            tipsContent.setText(formattedTips.toString());
        }

        // Set up navigation
        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_TipsFragment_to_FirstFragment));
    }

    private List<String> loadSavedTips() {
        List<String> tips = new ArrayList<>();
        try (FileInputStream fis = requireContext().openFileInput(TIPS_FILE)) {
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