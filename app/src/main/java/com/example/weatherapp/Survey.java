package com.example.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weatherapp.databinding.FragmentSurveyBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Survey extends Fragment {
    // Fixed TAG declaration
    private static final String TAG = "SurveyFragment";

    private FragmentSurveyBinding binding;
    private SurveyViewModel viewModel;
    private int currentPage = 1;
    private RadioGroup[] radioGroups = new RadioGroup[10];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSurveyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SurveyViewModel.class);

        // Setup initial page
        loadPage(currentPage);

        // Update button text
        binding.buttonSurvey.setText("Next");

        // Button click listener
        binding.buttonSurvey.setOnClickListener(v -> handleNavigation());
    }

    private void handleNavigation() {
        if (currentPage == 1) {
            // Validate at least Q1 is answered
            if (radioGroups[0] == null || radioGroups[0].getCheckedRadioButtonId() == -1) {
                Toast.makeText(requireContext(), "Please answer question 1", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save answers from page 1
            saveCurrentPageAnswers();

            // Get Q1 selection for next page
            int selectedId = radioGroups[0].getCheckedRadioButtonId();
            RadioButton selected = binding.getRoot().findViewById(selectedId);
            String choice = selected.getText().toString();
            viewModel.setPage1Choice(choice);

            currentPage = 2;
            loadPage(currentPage);
            binding.buttonSurvey.setText("Next");

        } else if (currentPage == 2) {
            saveCurrentPageAnswers();
            currentPage = 3;
            loadPage(currentPage);
            binding.buttonSurvey.setText("Submit");

        } else if (currentPage == 3) {
            saveCurrentPageAnswers();
            submitSurvey();
        }
    }

    private void loadPage(int pageNumber) {
        // Clear previous content except page indicator
        binding.surveyContainer.removeAllViews();

        // Reset radio groups
        radioGroups = new RadioGroup[10];

        // Update page indicator
        binding.pageIndicator.setText("Page " + pageNumber + " of 3");

        String filename;
        if (pageNumber == 1) {
            filename = "survey_page1.json";
        } else if (pageNumber == 2) {
            String choice = viewModel.getPage1Choice();
            if (choice != null) {
                if (choice.contains("Still in a relationship")) filename = "survey_page2a.json";
                else if (choice.contains("Planning to leave")) filename = "survey_page2b.json";
                else filename = "survey_page2c.json";
            } else {
                // Default if no choice saved
                filename = "survey_page2a.json";
            }
        } else {
            filename = "survey_page3.json";
        }

        try {
            String json = loadJSONFromAsset(requireContext(), filename);
            JSONArray questionsArray = new JSONArray(json);

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);
                String questionText = questionObj.getString("question");
                JSONArray optionsArray = questionObj.getJSONArray("options");

                // Create question TextView
                TextView questionView = new TextView(requireContext());
                questionView.setText((i + 1) + ". " + questionText);
                questionView.setTextSize(18);
                questionView.setTextColor(Color.BLACK);
                questionView.setPadding(0, 24, 0, 16);
                binding.surveyContainer.addView(questionView);

                // Create radio group
                RadioGroup radioGroup = new RadioGroup(requireContext());
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                radioGroup.setId(View.generateViewId());
                radioGroups[i] = radioGroup;

                for (int j = 0; j < optionsArray.length(); j++) {
                    RadioButton radioButton = new RadioButton(requireContext());
                    radioButton.setText(optionsArray.getString(j));
                    radioButton.setTextColor(Color.DKGRAY);
                    radioButton.setPadding(0, 8, 0, 8);
                    radioGroup.addView(radioButton);
                }

                binding.surveyContainer.addView(radioGroup);
            }

            // Add button at bottom
            binding.surveyContainer.addView(binding.buttonSurvey);

        } catch (Exception e) {
            Log.e(TAG, "Error loading page " + pageNumber, e);
            Toast.makeText(requireContext(), "Error loading questions", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCurrentPageAnswers() {
        for (int i = 0; i < radioGroups.length; i++) {
            if (radioGroups[i] != null) {
                int selectedId = radioGroups[i].getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selected = binding.getRoot().findViewById(selectedId);
                    String answer = selected.getText().toString();

                    if (currentPage == 1) {
                        viewModel.setPage1Answer(i, answer);
                    } else if (currentPage == 2) {
                        viewModel.setPage2Answer(i, answer);
                    } else if (currentPage == 3) {
                        // Save page 3 answers if needed
                    }
                }
            }
        }
    }

    private void submitSurvey() {
        // In a real app, you would:
        // 1. Collect all answers from ViewModel
        // 2. Send to your backend server
        // 3. Navigate to results screen

        Toast.makeText(requireContext(), "Survey submitted!", Toast.LENGTH_SHORT).show();

        // Navigate back to first fragment
        NavHostFragment.findNavController(Survey.this)
                .navigate(R.id.action_ThirdFragment_to_FirstFragment);
    }

    private String loadJSONFromAsset(Context context, String filename) {
        try (InputStream is = context.getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Error loading JSON: " + filename, e);
            Toast.makeText(context, "Error loading: " + filename, Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}