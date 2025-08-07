package com.example.weatherapp;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
/**
 * ViewModel that stores and manages survey answers.
 * Supports saving answers locally within the app's lifecycle
 * and remotely to Firebase for persistence.
 */
public class SurveyViewModel extends ViewModel {

    private String page1Choice;
    private final Map<Integer, Map<String, String>> pageAnswers = new HashMap<>();
    /**
     * Sets the user's choice for the first page of the survey
     * and saves the value to Firebase under the current user's UID.
     *
     * @param choice The selected choice for page 1.
     */
    public void setPage1Choice(String choice) {
        this.page1Choice = choice;

        // Save to Firebase as well (optional)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("surveyAnswers").child(uid);
            ref.child("page1Choice").setValue(choice);
        }
    }
    /**
     * Gets the user's choice for the first page of the survey.
     *
     * @return The selected choice as a string.
     */
    public String getPage1Choice() {
        return page1Choice;
    }
    /**
     * Stores an answer for a specific question on a given survey page.
     * Also saves the answer to Firebase under the current user's UID.
     *
     * @param page       The page number of the survey.
     * @param questionId The identifier of the question.
     * @param answer     The user's answer to the question.
     */
    public void setPageAnswer(int page, String questionId, String answer) {
        if (!pageAnswers.containsKey(page)) {
            pageAnswers.put(page, new HashMap<>());
        }
        pageAnswers.get(page).put(questionId, answer);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("surveyAnswers").child(uid);

            // Use string key for page (Firebase doesn't allow Integer keys)
            ref.child("pageAnswers")
                    .child(String.valueOf(page))
                    .child(questionId)
                    .setValue(answer);
        }
    }
    /**
     * Retrieves an answer by searching for a question ID across all pages.
     *
     * @param questionId The ID of the question to search for.
     * @return The corresponding answer if found, otherwise null.
     */
    public String getAnswerByQuestionId(String questionId) {
        for (Map<String, String> pageMap : pageAnswers.values()) {
            if (pageMap.containsKey(questionId)) {
                return pageMap.get(questionId);
            }
        }
        return null;
    }
    /**
     * Returns all the stored answers organized by page number and question ID.
     *
     * @return A map containing all survey answers.
     */
    public Map<Integer, Map<String, String>> getPageAnswers() {
        return pageAnswers;
    }
}