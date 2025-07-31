// SurveyViewModel.java
package com.example.weatherapp;

import androidx.lifecycle.ViewModel;

public class SurveyViewModel extends ViewModel {
    private String page1Choice;
    private String[] page1Answers = new String[10];
    private String[] page2aAnswers = new String[10];
    private String[] page2bAnswers = new String[10];
    private String[] page2cAnswers = new String[10];
    private String[] page3Answers = new String[10];

    public void setPage1Choice(String choice) {
        this.page1Choice = choice;
    }

    public String getPage1Choice() {
        return page1Choice;
    }

    public void setPageAnswer(int page, int index, String answer) {
        if (page == 1 && index < page1Answers.length) {
            page1Answers[index] = answer;
        } else if (page == 2 && index < page2aAnswers.length) {
            page2aAnswers[index] = answer;
        } else if (page == 3 && index < page2bAnswers.length) {
            page2bAnswers[index] = answer;
        } else if (page == 4 && index < page2cAnswers.length) {
            page2cAnswers[index] = answer;
        } else if (page == 5 && index < page3Answers.length) {
            page3Answers[index] = answer;
        }
    }

    public String getPageAnswer(int page, int index) {
        if (page == 1 && index < page1Answers.length) {
            return page1Answers[index];
        } else if (page == 2 && index < page2aAnswers.length) {
            return page2aAnswers[index];
        } else if (page == 3 && index < page2bAnswers.length) {
            return page2bAnswers[index];
        } else if (page == 4 && index < page2cAnswers.length) {
            return page2cAnswers[index];
        } else if (page == 5 && index < page3Answers.length) {
            return page3Answers[index];
        }
        return null;
    }
}