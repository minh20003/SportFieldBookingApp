package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class ChatRoom {
    @SerializedName("room_id")
    private int roomId;

    @SerializedName("post_id")
    private Integer postId; // Nullable

    @SerializedName("other_user_id")
    private int otherUserId;

    @SerializedName("other_user_name")
    private String otherUserName;

    @SerializedName("other_user_phone")
    private String otherUserPhone;

    @SerializedName("sport_name")
    private String sportName;

    @SerializedName("play_date")
    private String playDate;

    @SerializedName("last_message")
    private String lastMessage;

    @SerializedName("last_message_time")
    private String lastMessageTime;

    @SerializedName("unread_count")
    private int unreadCount;

    // Constructor
    public ChatRoom() {}

    // Getters và Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public int getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(int otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getOtherUserPhone() {
        return otherUserPhone;
    }

    public void setOtherUserPhone(String otherUserPhone) {
        this.otherUserPhone = otherUserPhone;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
