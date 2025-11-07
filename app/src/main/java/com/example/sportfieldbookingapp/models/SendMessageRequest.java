package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class SendMessageRequest {
    @SerializedName("room_id")
    private int roomId;

    @SerializedName("message")
    private String message;

    public SendMessageRequest(int roomId, String message) {
        this.roomId = roomId;
        this.message = message;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}