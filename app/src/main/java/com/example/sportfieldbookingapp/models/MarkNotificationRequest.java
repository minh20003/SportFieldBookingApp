package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class MarkNotificationRequest {
    @SerializedName("notification_id")
    private Integer notificationId; // Nullable nếu mark all

    @SerializedName("mark_all")
    private Boolean markAll;

    // Constructor cho mark 1 notification
    public MarkNotificationRequest(int notificationId) {
        this.notificationId = notificationId;
        this.markAll = null;
    }

    // Constructor cho mark all
    public MarkNotificationRequest(boolean markAll) {
        this.notificationId = null;
        this.markAll = markAll;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Boolean getMarkAll() {
        return markAll;
    }

    public void setMarkAll(Boolean markAll) {
        this.markAll = markAll;
    }
}








