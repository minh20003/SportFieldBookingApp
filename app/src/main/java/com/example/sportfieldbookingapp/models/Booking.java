package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class Booking {
    // Dùng để gửi đi
    @SerializedName("field_id")
    private int fieldId;

    @SerializedName("booking_date")
    private String bookingDate;

    @SerializedName("time_slot_start")
    private String timeSlotStart;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("id")
    private int id;

    @SerializedName("status")
    private String status;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("field_address")
    private String fieldAddress;
    // Constructor để tạo request
    public Booking(int fieldId, String bookingDate, String timeSlotStart, double totalPrice) {
        this.fieldId = fieldId;
        this.bookingDate = bookingDate;
        this.timeSlotStart = timeSlotStart;
        this.totalPrice = totalPrice;
    }

    public int getId() { return id; }
    public String getStatus() { return status; }
    public String getFieldName() { return fieldName; }
    public String getFieldAddress() { return fieldAddress; }
    public String getBookingDate() { return bookingDate; }
    public String getTimeSlotStart() { return timeSlotStart; }
}