package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AvailabilityResponse {

    @SerializedName("field_id")
    private int fieldId;

    @SerializedName("date")
    private String date;

    @SerializedName("day_of_week")
    private String dayOfWeek;

    @SerializedName("slots")
    private List<TimeSlot> slots;

    // Constructor
    public AvailabilityResponse(int fieldId, String date, String dayOfWeek, List<TimeSlot> slots) {
        this.fieldId = fieldId;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.slots = slots;
    }

    // Getters
    public int getFieldId() {
        return fieldId;
    }

    public String getDate() {
        return date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public List<TimeSlot> getSlots() {
        return slots;
    }

    // Setters
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setSlots(List<TimeSlot> slots) {
        this.slots = slots;
    }
}