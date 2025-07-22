// SurveyViewModel.java
package com.example.weatherapp;

import androidx.lifecycle.ViewModel;

public class SurveyViewModel extends ViewModel {
    private String page1Choice;
    private String[] page1Answers = new String[10];
    private String[] page2Answers = new String[10];

    public void setPage1Choice(String choice) {
        this.page1Choice = choice;
    }

    public String getPage1Choice() {
        return page1Choice;
    }

    public void setPage1Answer(int index, String answer) {
        if (index >= 0 && index < page1Answers.length) {
            page1Answers[index] = answer;
        }
    }

    public void setPage2Answer(int index, String answer) {
        if (index >= 0 && index < page2Answers.length) {
            page2Answers[index] = answer;
        }
    }
}