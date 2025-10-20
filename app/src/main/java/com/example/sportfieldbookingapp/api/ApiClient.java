package com.example.sportfieldbookingapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // --- LƯU Ý CỰC KỲ QUAN TRỌNG VỀ ĐỊA CHỈ IP ---
    // Không dùng "localhost" hay "127.0.0.1" ở đây.
    // 10.0.2.2 là địa chỉ IP đặc biệt mà máy ảo Android dùng để truy cập localhost của máy tính thật.
    private static final String BASE_URL = "http://10.0.2.2/sport-booking-api/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}