package com.example.sportfieldbookingapp.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://dquangminh2003.id.vn/sport-booking-api/api/";
    private static final String TAG = "ApiClient";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging interceptor cho debug (chỉ log headers, không log body để tránh lỗi parse)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                Log.d(TAG, message);
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC); // BASIC thay vì BODY

            // Tạo OkHttpClient với timeout và logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(logging)  // Thêm logging
                    .build();

            // Gson với lenient parsing để chấp nhận JSON không strict
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
