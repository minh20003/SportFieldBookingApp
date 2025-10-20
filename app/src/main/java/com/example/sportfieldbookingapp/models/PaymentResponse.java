package com.example.sportfieldbookingapp.models;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("payment_url")
    private String paymentUrl;

    public String getPaymentUrl() {
        return paymentUrl;
    }
}
