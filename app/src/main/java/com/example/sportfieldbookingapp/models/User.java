package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;

public class User {
    // Các thuộc tính cũ để nhận dữ liệu
    @SerializedName("id")
    private int id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    // Thêm thuộc tính password để gửi dữ liệu đi
    @SerializedName("password")
    private String password;
    @SerializedName("phone")
    private String phone;

    // --- Thêm các hàm khởi tạo (Constructors) ---

    // Constructor để gửi thông tin đăng nhập
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor để gửi thông tin đăng ký
    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}