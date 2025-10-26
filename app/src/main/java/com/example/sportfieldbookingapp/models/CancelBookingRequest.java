package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class CancelBookingRequest {
    @SerializedName("booking_id")
    private int bookingId;

    public CancelBookingRequest(int bookingId) {
        this.bookingId = bookingId;
    }
}