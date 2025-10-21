package com.example.sportfieldbookingapp.models;
import com.google.gson.annotations.SerializedName;

public class Review {
    // Dùng để gửi đi
    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    // Dùng để nhận về
    @SerializedName("reviewer_name")
    private String reviewerName;

    @SerializedName("created_at")
    private String createdAt;


    public Review(int bookingId, int rating, String comment) {
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getReviewerName() { return reviewerName; }
    public String getCreatedAt() { return createdAt; }
}