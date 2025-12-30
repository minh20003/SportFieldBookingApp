package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class ChatMessage {
    
    // Message Status Constants
    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_DELIVERED = 2;
    public static final int STATUS_SEEN = 3;
    public static final int STATUS_FAILED = -1;

    @SerializedName("id")
    private int id;

    @SerializedName("message_id")
    private String messageId; // UUID for local tracking

    @SerializedName("room_id")
    private int roomId;

    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("sender_name")
    private String senderName;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type = "text"; // text, image, file

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("is_mine")
    private boolean isMine;

    @SerializedName("is_recalled")
    private boolean isRecalled;

    @SerializedName("status")
    private int status = STATUS_SENT;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("delivered_at")
    private String deliveredAt;

    @SerializedName("seen_at")
    private String seenAt;

    // Local fields (not from server)
    private transient boolean isPending = false;
    private transient int retryCount = 0;

    // Constructors
    public ChatMessage() {
        this.messageId = UUID.randomUUID().toString();
    }

    public ChatMessage(int roomId, String message, boolean isMine) {
        this.messageId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.message = message;
        this.isMine = isMine;
        this.status = STATUS_SENDING;
        this.isPending = true;
        this.type = "text";
    }

    // Static factory for optimistic message
    public static ChatMessage createOptimistic(int roomId, String message, int senderId, String senderName) {
        ChatMessage msg = new ChatMessage();
        msg.messageId = UUID.randomUUID().toString();
        msg.roomId = roomId;
        msg.message = message;
        msg.senderId = senderId;
        msg.senderName = senderName;
        msg.isMine = true;
        msg.status = STATUS_SENDING;
        msg.isPending = true;
        msg.type = "text";
        msg.createdAt = getCurrentTimestamp();
        return msg;
    }

    private static String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isMine() { return isMine; }
    public void setMine(boolean mine) { isMine = mine; }

    public boolean isRecalled() { return isRecalled; }
    public void setRecalled(boolean recalled) { isRecalled = recalled; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(String deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getSeenAt() { return seenAt; }
    public void setSeenAt(String seenAt) { this.seenAt = seenAt; }

    public boolean isPending() { return isPending; }
    public void setPending(boolean pending) { isPending = pending; }

    public int getRetryCount() { return retryCount; }
    public void incrementRetryCount() { this.retryCount++; }

    // Helper methods
    public boolean isSending() { return status == STATUS_SENDING; }
    public boolean isSent() { return status == STATUS_SENT; }
    public boolean isDelivered() { return status == STATUS_DELIVERED; }
    public boolean isSeen() { return status == STATUS_SEEN; }
    public boolean isFailed() { return status == STATUS_FAILED; }

    public void markAsSent() {
        this.status = STATUS_SENT;
        this.isPending = false;
    }

    public void markAsDelivered() {
        this.status = STATUS_DELIVERED;
    }

    public void markAsSeen() {
        this.status = STATUS_SEEN;
        this.isRead = true;
    }

    public void markAsFailed() {
        this.status = STATUS_FAILED;
        this.isPending = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        if (messageId != null && that.messageId != null) {
            return messageId.equals(that.messageId);
        }
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return messageId != null ? messageId.hashCode() : id;
    }
}
