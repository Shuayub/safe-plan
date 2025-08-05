package com.example.weatherapp;

public class ReminderItem {
    private String frequency;
    private String time;

    public ReminderItem(String frequency, String time) {
        this.frequency = frequency;
        this.time = time;
    }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}

