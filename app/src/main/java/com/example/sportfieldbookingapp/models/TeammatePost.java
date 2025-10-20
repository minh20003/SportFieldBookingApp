package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class TeammatePost {
    @SerializedName("id")
    private int id;
    @SerializedName("sport_type")
    private String sportType;
    @SerializedName("play_date")
    private String playDate;
    @SerializedName("time_slot")
    private String timeSlot;
    @SerializedName("players_needed")
    private int playersNeeded;
    @SerializedName("description")
    private String description;
    @SerializedName("poster_name")
    private String posterName;
    public TeammatePost(String sportType, String playDate, String timeSlot, int playersNeeded, String description) {
        this.sportType = sportType;
        this.playDate = playDate;
        this.timeSlot = timeSlot;
        this.playersNeeded = playersNeeded;
        this.description = description;
    }
    // Thêm các hàm Getters cho các thuộc tính trên
    public int getId() { return id; }
    public String getSportType() { return sportType; }
    public String getPlayDate() { return playDate; }
    public String getTimeSlot() { return timeSlot; }
    public int getPlayersNeeded() { return playersNeeded; }
    public String getDescription() { return description; }
    public String getPosterName() { return posterName; }
}