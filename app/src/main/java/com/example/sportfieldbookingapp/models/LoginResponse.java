package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    // Getters
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }
}