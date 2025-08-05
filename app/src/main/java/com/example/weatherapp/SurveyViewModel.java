package com.example.weatherapp;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SurveyViewModel extends ViewModel {

    private String page1Choice;
    private final Map<Integer, Map<String, String>> pageAnswers = new HashMap<>();

    public void setPage1Choice(String choice) {
        this.page1Choice = choice;

        // Save to Firebase as well (optional)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null || true) { //TODO REMOVE TRUE AFTER
            //String uid = user.getUid();
            String uid = "xw5kWxTsebgdWJHHVOKfBIbxAwR2"; //TODO REMOVE THIS LINE AFTER
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("surveyAnswers").child(uid);
            ref.child("page1Choice").setValue(choice);
        }
    }

    public String getPage1Choice() {
        return page1Choice;
    }

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

    public String getAnswerByQuestionId(String questionId) {
        for (Map<String, String> pageMap : pageAnswers.values()) {
            if (pageMap.containsKey(questionId)) {
                return pageMap.get(questionId);
            }
        }
        return null;
    }

    public Map<Integer, Map<String, String>> getPageAnswers() {
        return pageAnswers;
    }
}