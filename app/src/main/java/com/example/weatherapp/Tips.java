package com.example.weatherapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tips {
    private static final String TIPS_FILE = "tips.json";
    private static final String ALL_TIPS_FILE = "user_tips.json";
    private static final int MAX_TIPS = 20;

    public static void generateAndSaveTips(Context context, SurveyViewModel viewModel) {
        resetTipsFile(context);

        List<String> tipsList = selectPersonalizedTips(context, viewModel);
        saveTipsToJson(context, tipsList);
    }

    private static List<String> selectPersonalizedTips(Context context, SurveyViewModel viewModel) {
        List<String> selectedTips = new ArrayList<>();
        List<JSONObject> allTips = loadAllTips(context);
        Map<String, String> answersMap = new HashMap<>();

        Log.d("TIP_DEBUG", "Loaded " + allTips.size() + " tips");
        Log.d("TIP_DEBUG", "User answers: " + answersMap.toString());
        // Create answers map with page/question keys
        int page = 1;
        for (int q = 0; q < 10; q++) {
            String answer = viewModel.getPageAnswer(page, q);
            if (answer != null) {
                answersMap.put("page" + page + "_q" + q, answer);
            }
        }
        if(viewModel.getPageAnswer(page, 0).equals("Still in a relationship")){
            page = 2;
        } else if (viewModel.getPageAnswer(page, 0).equals("Planning to leave")){
            page = 3;
        } else if (viewModel.getPageAnswer(page, 0).equals("Post-separation")){
            page = 4;
        }
        for (int q = 0; q < 10; q++) {
            String answer = viewModel.getPageAnswer(page, q);
            if (answer != null) {
                answersMap.put("page" + page + "_q" + q, answer);
            }
        }

        String answer = viewModel.getPageAnswer(5, 0);
        if (answer != null) {
            answersMap.put("page" + 5 + "_q" + 0, answer);
        }

        // Evaluate each tip
        for (JSONObject tip : allTips) {
            try {
                Log.d("TIP_EVAL", "Checking tip: " + tip.getString("id"));
                if (evaluateTipConditions(tip, answersMap)) {
                    String tipText = tip.getString("text");
                    Log.d("TIP_MATCH", "MATCHED: " + tip.getString("id"));
                    // Apply replacements if any
                    if (tip.has("conditions") && tip.getJSONObject("conditions").has("replacements")) {
                        JSONArray replacements = tip.getJSONObject("conditions")
                                .getJSONArray("replacements");
                        tipText = applyReplacements(tipText, replacements, answersMap);
                    }

                    selectedTips.add(tipText);
                    if (selectedTips.size() >= MAX_TIPS) break;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return selectedTips;
    }

    private static boolean evaluateTipConditions(JSONObject tip, Map<String, String> answers)
            throws JSONException {

        if (!tip.has("conditions")) return false;
        JSONObject conditions = tip.getJSONObject("conditions");
        boolean conditionMet = true;

        // Check trigger question
        if (conditions.has("triggerQuestion")) {
            JSONObject trigger = conditions.getJSONObject("triggerQuestion");
            int page = trigger.getInt("page");
            int question = trigger.getInt("question");
            String answerKey = "page" + page + "_q" + question;
            String actualAnswer = answers.get(answerKey);

            if (actualAnswer == null || actualAnswer.isEmpty()) {
                return false;
            }

            // Check for specific answers if defined
            if (trigger.has("answers")) {
                JSONArray requiredAnswers = trigger.getJSONArray("answers");
                boolean matchFound = true;

                // Handle checkbox answers (comma-separated)
                String[] actualParts = actualAnswer.split(",");

                for (int i = 0; i < requiredAnswers.length(); i++) {
                    String required = requiredAnswers.getString(i).trim();
                    boolean foundThisAnswer = false;

                    for (String part : actualParts) {
                        if (part.trim().equals(required)) {
                            foundThisAnswer = true;
                            break;
                        }
                    }

                    if (!foundThisAnswer) {
                        matchFound = false;
                        break;
                    }
                }
                conditionMet = conditionMet && matchFound;
            }
        }

        return conditionMet;
    }

    private static String applyReplacements(String tipText, JSONArray replacements,
                                            Map<String, String> answers) throws JSONException {

        for (int i = 0; i < replacements.length(); i++) {
            JSONObject replacement = replacements.getJSONObject(i);
            String placeholder = replacement.getString("placeholder");

            JSONObject question = replacement.getJSONObject("question");
            int page = question.getInt("page");
            int qIndex = question.getInt("question");

            String answerKey = "page" + page + "_q" + qIndex;
            String answerValue = answers.get(answerKey);

            if (answerValue != null) {
                tipText = tipText.replace(placeholder, answerValue);
            } else {
                tipText = tipText.replace(placeholder, "[unknown]"); // Avoid broken text
            }
        }
        return tipText;
    }

    private static List<JSONObject> loadAllTips(Context context) {
        List<JSONObject> tipsList = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(ALL_TIPS_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray tipsArray = new JSONArray(json);
            for (int i = 0; i < tipsArray.length(); i++) {
                tipsList.add(tipsArray.getJSONObject(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return tipsList;
    }

    private static void saveTipsToJson(Context context, List<String> tipsList) {
        try {
            JSONObject json = new JSONObject();
            json.put("tips", new JSONArray(tipsList));
            json.put("timestamp", System.currentTimeMillis());

            try (FileOutputStream fos = context.openFileOutput(TIPS_FILE, Context.MODE_PRIVATE)) {
                fos.write(json.toString().getBytes(StandardCharsets.UTF_8));
            } // Auto-close here
        } catch (Exception e) {
            Log.e("Tips", "Save failed", e);
        }
    }

    public static List<String> getSavedTips(Context context) {
        List<String> tips = new ArrayList<>();
        try {
            // Read from internal storage
            InputStream is = context.openFileInput(TIPS_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
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
    private static void resetTipsFile(Context context) {
        try {
            context.deleteFile(TIPS_FILE);
            Log.d("Tips", "Reset tips file: " + TIPS_FILE);
        } catch (Exception e) {
            Log.e("Tips", "Failed to reset tips file", e);
        }
    }
}