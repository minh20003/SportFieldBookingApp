package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class BookingDetail {

    @SerializedName("id")
    private int id;

    @SerializedName("booking_date")
    private String bookingDate;

    @SerializedName("time_slot_start")
    private String timeSlotStart;

    @SerializedName("time_slot_end")
    private String timeSlotEnd;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("field_address")
    private String fieldAddress;

    @SerializedName("sport_type")
    private String sportType;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("created_at")
    private String createdAt;

    // Constructor
    public BookingDetail() {
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getTimeSlotStart() {
        return timeSlotStart;
    }

    public String getTimeSlotEnd() {
        return timeSlotEnd;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldAddress() {
        return fieldAddress;
    }

    public String getSportType() {
        return sportType;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setTimeSlotStart(String timeSlotStart) {
        this.timeSlotStart = timeSlotStart;
    }

    public void setTimeSlotEnd(String timeSlotEnd) {
        this.timeSlotEnd = timeSlotEnd;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldAddress(String fieldAddress) {
        this.fieldAddress = fieldAddress;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}