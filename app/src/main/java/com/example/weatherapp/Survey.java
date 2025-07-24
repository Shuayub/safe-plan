package com.example.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.example.weatherapp.databinding.FragmentSurveyBinding;

public class Survey extends Fragment {
    private static final String TAG = "SurveyFragment";

    private FragmentSurveyBinding binding;
    private SurveyViewModel viewModel;
    private int currentPage = 1;

    // Track UI elements for each question
    private Map<Integer, View> questionViews = new HashMap<>();
    private String[] currentPageQuestionTypes = new String[10];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSurveyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SurveyViewModel.class);
        loadPage(currentPage);
        binding.buttonSurvey.setText("Next");

        binding.buttonSurvey.setOnClickListener(v -> handleNavigation());
    }

    private void handleNavigation() {
        if (currentPage == 1) {
            // Validate required questions
            if (!validatePage()) {
                Toast.makeText(requireContext(), "Please complete all required questions", Toast.LENGTH_SHORT).show();
                return;
            }

            saveCurrentPageAnswers();

            // Determine next page based on Q1 answer
            View q1View = questionViews.get(0);
            if (q1View instanceof RadioGroup) {
                int selectedId = ((RadioGroup) q1View).getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selected = q1View.findViewById(selectedId);
                    viewModel.setPage1Choice(selected.getText().toString());
                }
            }

            currentPage = 2;
            loadPage(currentPage);
            binding.buttonSurvey.setText("Next");

        } else if (currentPage == 2) {
            if (!validatePage()) return;
            saveCurrentPageAnswers();
            currentPage = 3;
            loadPage(currentPage);
            binding.buttonSurvey.setText("Submit");

        } else if (currentPage == 3) {
            if (!validatePage()) return;
            saveCurrentPageAnswers();
            submitSurvey();
        }
    }

    private void loadPage(int pageNumber) {
        binding.surveyContainer.removeAllViews();
        questionViews.clear();
        binding.pageIndicator.setText("Page " + pageNumber + " of 3");

        String filename = getPageFilename(pageNumber);

        try {
            String json = loadJSONFromAsset(requireContext(), filename);
            JSONArray questionsArray = new JSONArray(json);

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);
                String type = questionObj.optString("type", "radio");
                currentPageQuestionTypes[i] = type;
                String questionText = questionObj.getString("question");

                // Create question header
                TextView questionView = new TextView(requireContext());
                questionView.setText((i + 1) + ". " + questionText);
                questionView.setTextSize(18);
                questionView.setTextColor(Color.BLACK);
                questionView.setPadding(0, 24, 0, 16);
                binding.surveyContainer.addView(questionView);

                // Create input based on type
                View inputView = createInputView(type, questionObj, i);
                binding.surveyContainer.addView(inputView);
                questionViews.put(i, inputView);
            }

            binding.surveyContainer.addView(binding.buttonSurvey);

        } catch (Exception e) {
            Log.e(TAG, "Error loading page " + pageNumber, e);
            Toast.makeText(requireContext(), "Error loading questions", Toast.LENGTH_SHORT).show();
        }
    }

    private View createInputView(String type, JSONObject questionObj, int index) throws Exception {
        Context context = requireContext();

        switch (type) {
            case "text":
                return createTextInput(questionObj, index);

            case "radio_with_other":
                return createRadioWithOtherInput(questionObj, index);

            case "dropdown":
                return createDropdownInput(questionObj, index);

            case "checkbox":
                return createCheckboxInput(questionObj, index);

            default: // radio
                return createRadioInput(questionObj, index);
        }
    }

    private EditText createTextInput(JSONObject questionObj, int index) {
        EditText editText = new EditText(requireContext());
        editText.setHint(questionObj.optString("placeholder", "Enter your answer"));
        editText.setTag(index);

        // Set saved answer if exists
        String savedAnswer = viewModel.getPageAnswer(currentPage, index);
        if (savedAnswer != null) {
            editText.setText(savedAnswer);
        }

        return editText;
    }

    private LinearLayout createRadioWithOtherInput(JSONObject questionObj, int index) throws Exception {
        Context context = requireContext();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setTag(index);

        JSONArray options = questionObj.getJSONArray("options");
        for (int j = 0; j < options.length(); j++) {
            String option = options.getString(j);
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }

        container.addView(radioGroup);

        // Other text input
        EditText otherInput = new EditText(context);
        otherInput.setHint(questionObj.optString("other_placeholder", "Please specify"));
        otherInput.setVisibility(View.GONE);
        otherInput.setTag(index);
        container.addView(otherInput);

        // Show/hide other input based on selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = group.findViewById(checkedId);
            if (selected != null && selected.getText().toString().equals("Yes")) {
                otherInput.setVisibility(View.VISIBLE);
            } else {
                otherInput.setVisibility(View.GONE);
            }
        });

        // Set saved answer if exists
        String savedAnswer = viewModel.getPageAnswer(currentPage, index);
        if (savedAnswer != null) {
            // Check if it's a radio selection or custom text
            boolean found = false;
            for (int j = 0; j < options.length(); j++) {
                if (options.getString(j).equals(savedAnswer)) {
                    ((RadioButton) radioGroup.getChildAt(j)).setChecked(true);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // It's a custom "Other" answer
                ((RadioButton) radioGroup.getChildAt(options.length() - 1)).setChecked(true);
                otherInput.setText(savedAnswer);
                otherInput.setVisibility(View.VISIBLE);
            }
        }

        return container;
    }

    private Spinner createDropdownInput(JSONObject questionObj, int index) throws Exception {
        Spinner spinner = new Spinner(requireContext());
        JSONArray options = questionObj.getJSONArray("options");

        // Convert JSON array to string array
        String[] items = new String[options.length()];
        for (int j = 0; j < options.length(); j++) {
            items[j] = options.getString(j);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setTag(index);

        // Set saved answer if exists
        String savedAnswer = viewModel.getPageAnswer(currentPage, index);
        if (savedAnswer != null) {
            for (int j = 0; j < items.length; j++) {
                if (items[j].equals(savedAnswer)) {
                    spinner.setSelection(j);
                    break;
                }
            }
        }

        return spinner;
    }

    private LinearLayout createCheckboxInput(JSONObject questionObj, int index) throws Exception {
        Context context = requireContext();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        JSONArray options = questionObj.getJSONArray("options");
        for (int j = 0; j < options.length(); j++) {
            String option = options.getString(j);
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(option);
            checkBox.setTag(index);
            container.addView(checkBox);
        }

        // Set saved answers if exist
        String savedAnswer = viewModel.getPageAnswer(currentPage, index);
        if (savedAnswer != null) {
            String[] selected = savedAnswer.split(",");
            for (String item : selected) {
                for (int j = 0; j < container.getChildCount(); j++) {
                    CheckBox cb = (CheckBox) container.getChildAt(j);
                    if (cb.getText().toString().equals(item.trim())) {
                        cb.setChecked(true);
                        break;
                    }
                }
            }
        }

        return container;
    }

    private RadioGroup createRadioInput(JSONObject questionObj, int index) throws Exception {
        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setTag(index);

        JSONArray options = questionObj.getJSONArray("options");
        for (int j = 0; j < options.length(); j++) {
            String option = options.getString(j);
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }

        // Set saved answer if exists
        String savedAnswer = viewModel.getPageAnswer(currentPage, index);
        if (savedAnswer != null) {
            for (int j = 0; j < options.length(); j++) {
                if (options.getString(j).equals(savedAnswer)) {
                    ((RadioButton) radioGroup.getChildAt(j)).setChecked(true);
                    break;
                }
            }
        }

        return radioGroup;
    }

    private void saveCurrentPageAnswers() {
        for (int i = 0; i < currentPageQuestionTypes.length; i++) {
            if (currentPageQuestionTypes[i] == null) continue;

            View inputView = questionViews.get(i);
            if (inputView == null) continue;

            String answer = null;

            switch (currentPageQuestionTypes[i]) {
                case "text":
                    answer = ((EditText) inputView).getText().toString();
                    break;

                case "radio":
                    RadioGroup rg = (RadioGroup) inputView;
                    int selectedId = rg.getCheckedRadioButtonId();
                    if (selectedId != -1) {
                        answer = ((RadioButton) rg.findViewById(selectedId)).getText().toString();
                    }
                    break;

                case "radio_with_other":
                    // Handle radio with other
                    LinearLayout container = (LinearLayout) inputView;
                    RadioGroup radioGroup = (RadioGroup) container.getChildAt(0);
                    EditText otherInput = (EditText) container.getChildAt(1);

                    int selectedRadioId = radioGroup.getCheckedRadioButtonId();
                    if (selectedRadioId != -1) {
                        RadioButton selected = radioGroup.findViewById(selectedRadioId);
                        if (selected.getText().toString().equals("Other") &&
                                !otherInput.getText().toString().isEmpty()) {
                            answer = otherInput.getText().toString();
                        } else {
                            answer = selected.getText().toString();
                        }
                    }
                    break;

                case "dropdown":
                    answer = ((Spinner) inputView).getSelectedItem().toString();
                    break;

                case "checkbox":
                    LinearLayout cbContainer = (LinearLayout) inputView;
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < cbContainer.getChildCount(); j++) {
                        CheckBox cb = (CheckBox) cbContainer.getChildAt(j);
                        if (cb.isChecked()) {
                            if (sb.length() > 0) sb.append(",");
                            sb.append(cb.getText().toString());
                        }
                    }
                    answer = sb.toString();
                    break;
            }

            if (answer != null && !answer.isEmpty()) {
                viewModel.setPageAnswer(currentPage, i, answer);
            }
        }
    }

    private boolean validatePage() {
        for (int i = 0; i < currentPageQuestionTypes.length; i++) {
            if (currentPageQuestionTypes[i] == null) continue;

            View inputView = questionViews.get(i);
            if (inputView == null) continue;

            // Add any required validation here
            if (currentPageQuestionTypes[i].equals("text")) {
                EditText et = (EditText) inputView;
                if (et.getText().toString().trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Please answer question " + (i+1), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private String getPageFilename(int pageNumber) {
        if (pageNumber == 1) return "survey_page1.json";
        if (pageNumber == 3) return "survey_page3.json";

        String choice = viewModel.getPage1Choice();
        if (choice != null) {
            if (choice.contains("Still in a relationship")) return "survey_page2a.json";
            if (choice.contains("Planning to leave")) return "survey_page2b.json";
        }
        return "survey_page2c.json";
    }

    private void submitSurvey() {
        // Collect all answers
        StringBuilder surveyResults = new StringBuilder();
        for (int page = 1; page <= 3; page++) {
            for (int i = 0; i < 10; i++) {
                String answer = viewModel.getPageAnswer(page, i);
                if (answer != null) {
                    surveyResults.append("Page ").append(page)
                            .append(", Q").append(i+1)
                            .append(": ").append(answer)
                            .append("\n");
                }
            }
        }

        Log.d(TAG, "Survey Results:\n" + surveyResults);
        Toast.makeText(requireContext(), "Survey submitted!", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigate(R.id.action_SurveyFragment_to_FirstFragment);
    }

    private String loadJSONFromAsset(Context context, String filename) {
        try (InputStream is = context.getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Error loading JSON: " + filename, e);
            return null;
        }
    }
}