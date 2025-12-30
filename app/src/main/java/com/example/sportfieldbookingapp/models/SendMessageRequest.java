package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class SendMessageRequest {
    @SerializedName("room_id")
    private int roomId;

    @SerializedName("message")
    private String message;

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("type")
    private String type = "text";

    public SendMessageRequest(int roomId, String message) {
        this.roomId = roomId;
        this.message = message;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
