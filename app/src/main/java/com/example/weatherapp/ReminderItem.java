package com.example.weatherapp;

/**
 * Represents a reminder with a specific frequency and time.
 * This model is used to store and retrieve reminder details from shared preferences
 * and to display them in the UI.
 */
public class ReminderItem {
    /** The frequency of the reminder (e.g., Daily, Weekly, Monthly). */
    private String frequency;

    /** The time the reminder is set for (e.g., 04:30 PM). */
    private String time;

    /** The unique identifier for each alarm */
    private int requestCode;

    /**
     * Constructs a new ReminderItem with the given frequency and time.
     *
     * @param frequency   The frequency of the reminder.
     * @param time        The time of the reminder.
     * @param requestCode (Unused) Identifier for AlarmManager
     */
    public ReminderItem(String frequency, String time, int requestCode) {
        this.frequency = frequency;
        this.time = time;
        this.requestCode = requestCode;
    }

    /**
     * Gets the reminder frequency.
     *
     * @return The frequency string.
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the reminder frequency.
     *
     * @param frequency The frequency to set.
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets the reminder time.
     *
     * @return The time string.
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the reminder time.
     *
     * @param time The time to set.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets the reminder unique identifier.
     *
     * @return The unique identifier.
     */
    public int getRequestCode() {
        return requestCode;
    }

    /**
     * Sets the unique identifier.
     *
     * @param requestCode The unique identifier to set.
     * */
    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

}