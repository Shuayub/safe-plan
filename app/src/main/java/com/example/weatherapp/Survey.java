package com.example.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import org.json.JSONException;
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
    private JSONObject unifiedJson;
    // Track UI elements for each question
    private final Map<Integer, View> questionViews = new HashMap<>();
    private final String[] currentPageQuestionTypes = new String[10];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSurveyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SurveyViewModel.class);
        loadUnifiedJson();
        loadPage(currentPage);
        binding.buttonSurvey.setText("Next");

        binding.buttonSurvey.setOnClickListener(v -> handleNavigation());
    }
    private void loadUnifiedJson() {
        try {
            String json = loadJSONFromAsset(requireContext(), "questions_and_tips.json");
            unifiedJson = new JSONObject(json);
        } catch (Exception e) {
            Log.e(TAG, "Error loading unified JSON", e);
            Toast.makeText(requireContext(), "Error loading survey data", Toast.LENGTH_SHORT).show();
        }
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
                    currentPage = 2; // Always go to page 2 next, mapToJsonPageId will determine content
                }
            }

            loadPage(currentPage);
            binding.buttonSurvey.setText("Next");

        } else if (currentPage == 2) {
            if (!validatePage()) return;
            saveCurrentPageAnswers();
            currentPage = 5; // Skip to last page after safety questions
            loadPage(currentPage);
            binding.buttonSurvey.setText("Submit");

        } else if (currentPage == 5) {
            if (!validatePage()) return;
            saveCurrentPageAnswers();
            submitSurvey();
        }
    }

    private void loadPage(int pageNumber) {
        binding.surveyContainer.removeAllViews();
        questionViews.clear();

        int jsonPageId = mapToJsonPageId(pageNumber);
        if(pageNumber == 5){
            binding.pageIndicator.setText("Page 3 of 3");
        } else {
            binding.pageIndicator.setText("Page " + pageNumber + " of 3");
        }


        try {
            if (unifiedJson == null) {
                loadUnifiedJson();
                if (unifiedJson == null) return;
            }

            JSONArray pages = unifiedJson.getJSONObject("survey").getJSONArray("pages");
            JSONObject page = null;

            // Find the page with matching ID
            for (int i = 0; i < pages.length(); i++) {
                JSONObject p = pages.getJSONObject(i);
                if (p.getInt("id") == jsonPageId) {
                    page = p;
                    break;
                }
            }

            if (page == null) return;

            JSONArray questions = page.getJSONArray("questions");
            for (int i = 0; i < questions.length(); i++) {
                JSONObject questionObj = questions.getJSONObject(i);
                String type = questionObj.optString("type", "radio");
                currentPageQuestionTypes[i] = type;
                String questionText = questionObj.getString("question");
                String questionId = questionObj.getString("id");  // Get question ID

                // Create question header
                TextView questionView = new TextView(requireContext());
                questionView.setText((i + 1) + ". " + questionText);
                questionView.setTextSize(18);
                questionView.setTextColor(Color.BLACK);
                questionView.setPadding(0, 24, 0, 16);
                binding.surveyContainer.addView(questionView);

                // Create input based on type
                View inputView = createInputView(type, questionObj, i);
                inputView.setTag(questionId);  // Set tag to question ID
                binding.surveyContainer.addView(inputView);
                questionViews.put(i, inputView);
            }

            binding.surveyContainer.addView(binding.buttonSurvey);

        } catch (Exception e) {
            Log.e(TAG, "Error loading page " + pageNumber, e);
            Toast.makeText(requireContext(), "Error loading questions", Toast.LENGTH_SHORT).show();
        }
    }

    private int mapToJsonPageId(int fragmentPage) {
        switch (fragmentPage) {
            case 1: return 1;  // Basic Information
            case 2:
                String choice = viewModel.getPage1Choice();
                if (choice != null) {
                    if (choice.equals("Still in a relationship")) return 2;  // Current Safety
                    if (choice.equals("Planning to leave")) return 3;        // Exit Planning
                    if (choice.equals("Post-separation")) return 4;          // Post-Separation
                }
                return 2; // Default to Current Safety
            case 3: return 3; // Exit Planning (if needed)
            case 4: return 4; // Post-Separation (if needed)
            case 5: return 5; // Support Needs
            default: return 1;
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

    private EditText createTextInput(JSONObject questionObj, int index) throws JSONException {
        EditText editText = new EditText(requireContext());
        editText.setHint(questionObj.optString("placeholder", "Enter your answer"));
        editText.setTag(questionObj.getString("id")); // Store question ID as tag

        // Set saved answer if exists
        String savedAnswer = viewModel.getAnswerByQuestionId(questionObj.getString("id"));
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
        radioGroup.setTag(questionObj.getString("id"));

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
        otherInput.setTag(questionObj.getString("id"));
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
        String savedAnswer = viewModel.getAnswerByQuestionId(questionObj.getString("id"));
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
                ((RadioButton) radioGroup.getChildAt(1)).setChecked(true); // "Yes" is at index 1
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
        spinner.setTag(questionObj.getString("id")); // Store question ID as tag

        // Set saved answer if exists
        String savedAnswer = viewModel.getAnswerByQuestionId(questionObj.getString("id"));
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
            checkBox.setTag(questionObj.getString("id")); // Store question ID as tag
            container.addView(checkBox);
        }

        // Set saved answers if exist
        String savedAnswer = viewModel.getAnswerByQuestionId(questionObj.getString("id"));
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
        radioGroup.setTag(questionObj.getString("id")); // Store question ID as tag

        JSONArray options = questionObj.getJSONArray("options");
        for (int j = 0; j < options.length(); j++) {
            String option = options.getString(j);
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }

        // Set saved answer if exists
        String savedAnswer = viewModel.getAnswerByQuestionId(questionObj.getString("id"));
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
            String questionId = (String) inputView.getTag(); // Get question ID from tag

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
                    LinearLayout container = (LinearLayout) inputView;
                    RadioGroup radioGroup = (RadioGroup) container.getChildAt(0);
                    EditText otherInput = (EditText) container.getChildAt(1);

                    int selectedRadioId = radioGroup.getCheckedRadioButtonId();
                    if (selectedRadioId != -1) {
                        RadioButton selected = radioGroup.findViewById(selectedRadioId);
                        if (selected.getText().toString().equals("Yes") &&
                                otherInput.getVisibility() == View.VISIBLE &&
                                !otherInput.getText().toString().isEmpty()) {
                            // Save the code word if "Yes" is selected and there's text
                            answer = otherInput.getText().toString();
                        } else {
                            // Save the radio button text otherwise
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
                viewModel.setPageAnswer(currentPage, questionId, answer);
            }
        }
    }

    private boolean validatePage() {
        for (int i = 0; i < currentPageQuestionTypes.length; i++) {
            if (currentPageQuestionTypes[i] == null) continue;

            View inputView = questionViews.get(i);
            if (inputView == null) continue;

            if (currentPageQuestionTypes[i].equals("text")) {
                EditText et = (EditText) inputView;
                if (et.getText().toString().trim().isEmpty()) {
                    // Get the question text for better error message
                    try {
                        JSONArray pages = unifiedJson.getJSONObject("survey").getJSONArray("pages");
                        JSONObject currentPageObj = pages.getJSONObject(mapToJsonPageId(currentPage) - 1);
                        JSONArray questions = currentPageObj.getJSONArray("questions");
                        JSONObject question = questions.getJSONObject(i);

                        Toast.makeText(requireContext(),
                                "Please answer: " + question.getString("question"),
                                Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(),
                                "Please answer question " + (i+1),
                                Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private void submitSurvey() {
        // Collect all answers
        StringBuilder surveyResults = new StringBuilder();

        // Get all pages from the unified JSON
        try {
            JSONArray pages = unifiedJson.getJSONObject("survey").getJSONArray("pages");
            for (int p = 0; p < pages.length(); p++) {
                JSONObject page = pages.getJSONObject(p);
                JSONArray questions = page.getJSONArray("questions");

                for (int q = 0; q < questions.length(); q++) {
                    JSONObject question = questions.getJSONObject(q);
                    String questionId = question.getString("id");
                    String answer = viewModel.getAnswerByQuestionId(questionId);

                    if (answer != null) {
                        surveyResults.append("Page ").append(page.getInt("id"))
                                .append(", ").append(questionId)
                                .append(": ").append(answer)
                                .append("\n");
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error generating survey results", e);
        }

        saveCurrentPageAnswers(); // Ensure final page answers are saved
        Tips.generateAndSaveTips(requireContext(), viewModel, unifiedJson);

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