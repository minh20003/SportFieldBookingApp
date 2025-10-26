package com.example.sportfieldbookingapp.models;
import com.google.gson.annotations.SerializedName;

public class GoogleSignInRequest {
    @SerializedName("idToken")
    private String idToken;

    public GoogleSignInRequest(String idToken) {
        this.idToken = idToken;
    }
}