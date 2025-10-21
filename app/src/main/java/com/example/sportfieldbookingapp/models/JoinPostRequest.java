package com.example.sportfieldbookingapp.models;


import com.google.gson.annotations.SerializedName;

public class JoinPostRequest {
    @SerializedName("post_id")
    private int postId;

    public JoinPostRequest(int postId) {
        this.postId = postId;
    }
}