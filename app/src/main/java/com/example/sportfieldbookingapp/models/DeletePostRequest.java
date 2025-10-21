package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class DeletePostRequest {
    @SerializedName("post_id")
    private int postId;

    public DeletePostRequest(int postId) {
        this.postId = postId;
    }
}