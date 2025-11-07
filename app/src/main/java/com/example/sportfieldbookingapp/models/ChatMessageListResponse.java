package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatMessageListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ChatMessage> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<ChatMessage> getData() {
        return data;
    }
}
