package com.example.sportfieldbookingapp.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // === CHUYỂN ĐỔI GIỮA LOCAL VÀ PRODUCTION ===
    // Local (XAMPP): Dùng IP máy tính của bạn
    private static final String BASE_URL = "http://192.168.1.55/sport-booking-api/api/";
    
    // Production (khi deploy): Bỏ comment dòng dưới, comment dòng trên
    // private static final String BASE_URL = "http://dquangminh2003.id.vn/sport-booking-api/api/";
    
    // Emulator: Dùng 10.0.2.2
    // private static final String BASE_URL = "http://10.0.2.2/sport-booking-api/api/";
    //http://192.168.1.55/sport-booking-api/api/
    private static final String TAG = "ApiClient";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging interceptor cho debug (chỉ log headers, không log body để tránh lỗi parse)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                Log.d(TAG, message);
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC); // BASIC thay vì BODY

            // UTF-8 Interceptor - đảm bảo tất cả request đều dùng UTF-8
            Interceptor utf8Interceptor = chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                // Thêm header Accept-Charset
                requestBuilder.header("Accept-Charset", "UTF-8");

                // Nếu có body, đảm bảo Content-Type có charset=UTF-8
                if (original.body() != null) {
                    MediaType contentType = original.body().contentType();
                    if (contentType != null && contentType.type().equals("application")) {
                        // Đọc body gốc
                        Buffer buffer = new Buffer();
                        original.body().writeTo(buffer);
                        String bodyString = buffer.readString(StandardCharsets.UTF_8);

                        // Tạo lại body với charset UTF-8 explicit
                        MediaType utf8MediaType = MediaType.parse("application/json; charset=UTF-8");
                        RequestBody newBody = RequestBody.create(bodyString, utf8MediaType);
                        requestBuilder.method(original.method(), newBody);
                    }
                }

                return chain.proceed(requestBuilder.build());
            };

            // Tạo OkHttpClient với timeout và logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(utf8Interceptor)  // Thêm UTF-8 interceptor
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
