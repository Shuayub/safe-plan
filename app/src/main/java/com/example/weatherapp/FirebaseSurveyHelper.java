package com.example.weatherapp;

public class FirebaseSurveyHelper {
    private final FirebaseFirestore db;
    private static final String SURVEY_COLLECTION = "surveys";

    public FirebaseSurveyHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveSurvey(Map<String, Object> surveyData, OnCompleteListener<Void> listener) {
        db.collection(SURVEY_COLLECTION)
                .add(surveyData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(task);
                    } else {
                        Log.e("Firebase", "Error saving survey", task.getException());
                    }
                });
    }

    public interface OnCompleteListener<T> {
        void onComplete(Task<T> task);
    }
}
