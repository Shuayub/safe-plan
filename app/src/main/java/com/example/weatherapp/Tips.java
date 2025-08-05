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

public class Tips {
    private static final String TIPS_FILE = "tips.json";
    private static final int MAX_TIPS = 20;

    public static void generateAndSaveTips(Context context, SurveyViewModel viewModel, JSONObject unifiedJson) {
        resetTipsFile(context);
        List<String> tipsList = selectPersonalizedTips(context, viewModel, unifiedJson);
        saveTipsToJson(context, tipsList);
    }

    private static List<String> selectPersonalizedTips(Context context, SurveyViewModel viewModel, JSONObject unifiedJson) {
        List<String> selectedTips = new ArrayList<>();
        if (unifiedJson == null) return selectedTips;

        try {
            JSONArray allTips = unifiedJson.getJSONArray("tips");
            String page1Choice = viewModel.getPage1Choice();

            // Determine which section to include based on P1Q1 answer
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
    private static void resetTipsFile(Context context) {
        try {
            context.deleteFile(TIPS_FILE);
            Log.d("Tips", "Reset tips file: " + TIPS_FILE);
        } catch (Exception e) {
            Log.e("Tips", "Failed to reset tips file", e);
        }
    }
}