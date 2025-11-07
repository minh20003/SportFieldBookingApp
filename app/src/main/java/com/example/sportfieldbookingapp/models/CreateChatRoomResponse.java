package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class CreateChatRoomResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("room_id")
    private Integer roomId;

    @SerializedName("data")
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getRoomId() {
        if (roomId != null) {
            return roomId;
        }
        if (data != null) {
            return data.roomId != null ? data.roomId : 0;
        }
        return 0;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("room_id")
        private Integer roomId;

        @SerializedName("other_user_name")
        private String otherUserName;

        public Integer getRoomId() {
            return roomId;
        }

        public String getOtherUserName() {
            return otherUserName;
        }
    }
}
