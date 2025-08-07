package com.example.weatherapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 * Utility class for generating, selecting, and saving personalized tips
 * based on survey responses using data from a unified JSON structure.
 */
public class Tips {
    private static final String TIPS_FILE = "tips.json";
    private static final int MAX_TIPS = 20;
    /**
     * Generates and saves a list of personalized tips to local storage,
     * based on the user's survey responses and a unified JSON file.
     *
     * @param context     The application context used for file operations.
     * @param viewModel   The ViewModel containing survey answers.
     * @param unifiedJson The parsed JSON containing all survey tips and logic.
     */
    public static void generateAndSaveTips(Context context, SurveyViewModel viewModel, JSONObject unifiedJson) {
        resetTipsFile(context);
        List<String> tipsList = selectPersonalizedTips(context, viewModel, unifiedJson);
        saveTipsToJson(context, tipsList);
    }
    /**
     * Selects a personalized list of tips from the unified JSON file
     * by applying filtering logic and condition checks against survey responses.
     *
     * @param context     The application context.
     * @param viewModel   The ViewModel containing user answers.
     * @param unifiedJson The JSON object with all tips and metadata.
     * @return A list of selected tip strings.
     */
    private static List<String> selectPersonalizedTips(Context context, SurveyViewModel viewModel, JSONObject unifiedJson) {
        List<String> selectedTips = new ArrayList<>();
        if (unifiedJson == null) return selectedTips;

        try {
            JSONArray allTips = unifiedJson.getJSONArray("tips");
            String page1Choice = viewModel.getPage1Choice();

            // Determine relevant section (P2a, P2b, or P2c)
            String includedSection = null;
            if (page1Choice != null) {
                if (page1Choice.equals("Still in a relationship")) {
                    includedSection = "P2a";
                } else if (page1Choice.equals("Planning to leave")) {
                    includedSection = "P2b";
                } else if (page1Choice.equals("Post-separation")) {
                    includedSection = "P2c";
                }
            }

            for (int i = 0; i < allTips.length(); i++) {
                JSONObject tip = allTips.getJSONObject(i);
                String tipId = tip.getString("id");

                // Skip tips from other sections (2a/2b/2c)
                if (includedSection != null && tipId.startsWith("P2") && !tipId.startsWith(includedSection)) {
                    continue;
                }

                if (evaluateTipConditions(tip, viewModel)) {
                    String tipText = tip.getString("text");

                    // Apply replacements
                    if (tip.has("placeholder") && tip.has("placeholder_source")) {
                        String placeholder = tip.getString("placeholder");
                        String sourceId = tip.getString("placeholder_source");
                        String replacement = viewModel.getAnswerByQuestionId(sourceId);

                        if (replacement != null) {
                            tipText = tipText.replace(placeholder, replacement);
                        }
                    }

                    selectedTips.add(tipText);
                    if (selectedTips.size() >= MAX_TIPS) break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return selectedTips;
    }
    /**
     * Evaluates whether a given tip meets the condition criteria
     * based on the user's survey answer for a specific question.
     *
     * @param tip       The tip JSON object with condition logic.
     * @param viewModel The ViewModel containing survey responses.
     * @return true if the tip should be shown, false otherwise.
     */
    private static boolean evaluateTipConditions(JSONObject tip, SurveyViewModel viewModel) {
        try {
            String questionId = tip.getString("question_id");
            String userAnswer = viewModel.getAnswerByQuestionId(questionId);

            if (userAnswer == null || userAnswer.isEmpty()) {
                return false;
            }

            if (tip.has("condition")) {
                String requiredAnswer = tip.getString("condition");

                // Special handling for radio-with-other questions
                if (questionId.equals("P1Q5") || questionId.equals("P2bQ4") ||
                        questionId.equals("P2cQ2") || questionId.equals("P2cQ3")) {
                    if (requiredAnswer.equals("Yes")) {
                        return !userAnswer.equals("No") && !userAnswer.equals("Yes");
                    } else if (requiredAnswer.equals("No")) {
                        return userAnswer.equals("No");
                    }
                }

                // Default handling for other questions
                if (userAnswer.contains(",")) {
                    for (String part : userAnswer.split(",")) {
                        if (part.trim().equals(requiredAnswer)) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    return userAnswer.equals(requiredAnswer);
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Saves a list of selected tips to a local JSON file for offline access.
     *
     * @param context   The application context used to write the file.
     * @param tipsList  A list of tip strings to save.
     */
    private static void saveTipsToJson(Context context, List<String> tipsList) {
        try {
            JSONObject json = new JSONObject();
            json.put("tips", new JSONArray(tipsList));
            json.put("timestamp", System.currentTimeMillis());

            try (FileOutputStream fos = context.openFileOutput(TIPS_FILE, Context.MODE_PRIVATE)) {
                fos.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            Log.e("Tips", "Save failed", e);
        }
    }
    /**
     * Deletes the existing tips file to ensure fresh data is saved.
     *
     * @param context The application context used to delete the file.
     */
    private static void resetTipsFile(Context context) {
        try {
            context.deleteFile(TIPS_FILE);
            Log.d("Tips", "Reset tips file: " + TIPS_FILE);
        } catch (Exception e) {
            Log.e("Tips", "Failed to reset tips file", e);
        }
    }
}