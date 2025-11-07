package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class CreateChatRoomRequest {
    @SerializedName("other_user_id")
    private int otherUserId;

    @SerializedName("post_id")
    private Integer postId; // Nullable

    public CreateChatRoomRequest(int otherUserId, Integer postId) {
        this.otherUserId = otherUserId;
        this.postId = postId;
    }

    public int getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(int otherUserId) {
        this.otherUserId = otherUserId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }
}
