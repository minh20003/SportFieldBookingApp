package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class ChatRoom {
    @SerializedName("room_id")
    private int roomId;

    @SerializedName("conversation_id")
    private String conversationId;

    @SerializedName("post_id")
    private Integer postId;

    @SerializedName("other_user_id")
    private int otherUserId;

    @SerializedName("other_user_name")
    private String otherUserName;

    @SerializedName("other_user_phone")
    private String otherUserPhone;

    @SerializedName("other_user_avatar")
    private String otherUserAvatar;

    @SerializedName("sport_name")
    private String sportName;

    @SerializedName("play_date")
    private String playDate;

    @SerializedName("last_message")
    private String lastMessage;

    @SerializedName("last_message_time")
    private String lastMessageTime;

    @SerializedName("last_message_sender_id")
    private int lastMessageSenderId;

    @SerializedName("unread_count")
    private int unreadCount;

    @SerializedName("is_online")
    private boolean isOnline;

    @SerializedName("last_seen")
    private String lastSeen;

    @SerializedName("is_typing")
    private boolean isTyping;

    // Constructor
    public ChatRoom() {}

    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public int getOtherUserId() { return otherUserId; }
    public void setOtherUserId(int otherUserId) { this.otherUserId = otherUserId; }

    public String getOtherUserName() { return otherUserName; }
    public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }

    public String getOtherUserPhone() { return otherUserPhone; }
    public void setOtherUserPhone(String otherUserPhone) { this.otherUserPhone = otherUserPhone; }

    public String getOtherUserAvatar() { return otherUserAvatar; }
    public void setOtherUserAvatar(String otherUserAvatar) { this.otherUserAvatar = otherUserAvatar; }

    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public String getPlayDate() { return playDate; }
    public void setPlayDate(String playDate) { this.playDate = playDate; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public int getLastMessageSenderId() { return lastMessageSenderId; }
    public void setLastMessageSenderId(int lastMessageSenderId) { this.lastMessageSenderId = lastMessageSenderId; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public String getLastSeen() { return lastSeen; }
    public void setLastSeen(String lastSeen) { this.lastSeen = lastSeen; }

    public boolean isTyping() { return isTyping; }
    public void setTyping(boolean typing) { isTyping = typing; }

    // Helper methods
    public void incrementUnreadCount() {
        this.unreadCount++;
    }

    public void resetUnreadCount() {
        this.unreadCount = 0;
    }

    public void updateLastMessage(String message, String time, int senderId) {
        this.lastMessage = message;
        this.lastMessageTime = time;
        this.lastMessageSenderId = senderId;
    }
}
