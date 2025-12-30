package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class DeleteChatRoomRequest {
    @SerializedName("room_id")
    private int roomId;

    public DeleteChatRoomRequest(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
}
