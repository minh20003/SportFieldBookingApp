package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}