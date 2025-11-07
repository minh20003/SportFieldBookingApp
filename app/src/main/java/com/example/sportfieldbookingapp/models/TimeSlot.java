package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class TimeSlot {

    @SerializedName("time_slot")
    private String timeSlot;

    @SerializedName("price")
    private double price;

    @SerializedName("is_peak_hour")
    private boolean isPeakHour;

    @SerializedName("is_available")
    private boolean isAvailable;

    // Constructor
    public TimeSlot(String timeSlot, double price, boolean isPeakHour, boolean isAvailable) {
        this.timeSlot = timeSlot;
        this.price = price;
        this.isPeakHour = isPeakHour;
        this.isAvailable = isAvailable;
    }

    // Getters
    public String getTimeSlot() {
        return timeSlot;
    }

    public double getPrice() {
        return price;
    }

    public boolean isPeakHour() {
        return isPeakHour;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    // Setters
    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPeakHour(boolean peakHour) {
        isPeakHour = peakHour;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}