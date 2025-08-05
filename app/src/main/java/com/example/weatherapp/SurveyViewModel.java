// SurveyViewModel.java
package com.example.weatherapp;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class SurveyViewModel extends ViewModel {
    private String page1Choice;
    private final Map<Integer, Map<String, String>> pageAnswers = new HashMap<>();

    public void setPage1Choice(String choice) {
        this.page1Choice = choice;
    }

    public String getPage1Choice() {
        return page1Choice;
    }

    public void setPageAnswer(int page, String questionId, String answer) {
        if (!pageAnswers.containsKey(page)) {
            pageAnswers.put(page, new HashMap<>());
        }
        pageAnswers.get(page).put(questionId, answer);
    }

    public String getAnswerByQuestionId(String questionId) {
        for (Map<String, String> pageMap : pageAnswers.values()) {
            if (pageMap.containsKey(questionId)) {
                return pageMap.get(questionId);
            }
        }
        return null;
    }
}