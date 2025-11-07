package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatRoomListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<ChatRoom> data;

    public boolean isSuccess() {
        return success;
    }

    public List<ChatRoom> getData() {
        return data;
    }
}