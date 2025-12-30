package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatMessageListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ChatMessage> data;

    @SerializedName("has_more")
    private boolean hasMore;

    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("limit")
    private int limit;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<ChatMessage> getData() {
        return data;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }
}
