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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        RecyclerView recyclerView = view.findViewById(R.id.tipsRecyclerView);
        Button backButton = view.findViewById(R.id.actionButton);
        TextView emptyView = view.findViewById(R.id.emptyTextView); // Optional: Add for empty state

        List<String> tips = loadSavedTips();

        // Handle empty state
        if (tips.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE); // Show "No tips" message
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new TipsAdapter(tips));
        }

        // Navigation
        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_TipsFragment_to_FirstFragment)
        );
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