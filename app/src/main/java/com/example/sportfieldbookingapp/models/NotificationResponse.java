package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Notification> data;

    @SerializedName("pagination")
    private Pagination pagination;

    public boolean isSuccess() {
        return success;
    }

    public List<Notification> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public static class Pagination {
        @SerializedName("total")
        private int total;

        @SerializedName("unread_count")
        private int unreadCount;

        @SerializedName("page")
        private int page;

        @SerializedName("limit")
        private int limit;

        @SerializedName("total_pages")
        private int totalPages;

        public int getTotal() {
            return total;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public int getPage() {
            return page;
        }

        public int getLimit() {
            return limit;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}