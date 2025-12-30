package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class DeleteMessageRequest {
    @SerializedName("message_id")
    private int messageId;

    @SerializedName("delete_type")
    private String deleteType; // "for_me" hoặc "for_all"

    public DeleteMessageRequest(int messageId, String deleteType) {
        this.messageId = messageId;
        this.deleteType = deleteType;
    }

    // Factory methods
    public static DeleteMessageRequest forMe(int messageId) {
        return new DeleteMessageRequest(messageId, "for_me");
    }

    public static DeleteMessageRequest forAll(int messageId) {
        return new DeleteMessageRequest(messageId, "for_all");
    }

    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public String getDeleteType() { return deleteType; }
    public void setDeleteType(String deleteType) { this.deleteType = deleteType; }
}
