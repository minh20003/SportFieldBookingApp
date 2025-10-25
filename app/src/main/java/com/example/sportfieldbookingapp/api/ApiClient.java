package com.example.sportfieldbookingapp.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2/sport-booking-api/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Tạo OkHttpClient với timeout DÀI HƠN - KHÔNG CÓ LOGGING
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS) // Tăng lên 60s
                    .readTimeout(60, TimeUnit.SECONDS)    // Tăng lên 60s
                    .writeTimeout(60, TimeUnit.SECONDS)   // Tăng lên 60s
                    .retryOnConnectionFailure(true)       // Tự động retry
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
