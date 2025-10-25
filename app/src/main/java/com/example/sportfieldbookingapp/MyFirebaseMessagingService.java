package com.example.sportfieldbookingapp; // <<< THAY BẰNG PACKAGE CỦA BẠN

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// Thay 'com.example.sportfieldbookingapp' bằng package của bạn trong các dòng import dưới đây
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.R; // Đảm bảo import R

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "SportBookingChannel"; // ID của kênh thông báo

    /**
     * Được gọi khi nhận được tin nhắn mới.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Kiểm tra xem tin nhắn có chứa phần thông báo (notification) không.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);
            // Hiển thị thông báo lên điện thoại
            sendNotification(title, body);
        }

        // (Tùy chọn) Kiểm tra xem tin nhắn có chứa dữ liệu (data payload) không.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // Xử lý dữ liệu nếu cần (ví dụ: cập nhật gì đó trong app)
        }
    }

    /**
     * Được gọi khi FCM token được tạo mới hoặc cập nhật.
     * Token này là địa chỉ duy nhất của thiết bị để nhận thông báo.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Gửi token này lên server của bạn để lưu lại
        sendRegistrationToServer(token);
    }

    /**
     * Gửi FCM token lên server back-end.
     */
    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: " + token);
        // Dùng getApplicationContext() để an toàn hơn trong Service
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("USER_TOKEN", null);

        // Chỉ gửi nếu người dùng đã đăng nhập (có auth token)
        if (authToken != null) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            // Gọi API update_fcm_token.php (dạng Form Field)
            Call<GenericResponse> call = apiService.updateFcmToken("Bearer " + authToken, token);
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "FCM Token sent to server successfully.");
                    } else {
                        Log.w(TAG, "Failed to send FCM Token to server. Code: " + response.code());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Error sending FCM Token to server: " + t.getMessage());
                }
            });
        } else {
            Log.w(TAG, "User not logged in, cannot send FCM token yet.");
            // Lưu tạm token để gửi sau khi đăng nhập thành công
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("PENDING_FCM_TOKEN", token);
            editor.apply();
        }
    }

    /**
     * Tạo và hiển thị một thông báo đơn giản.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo Kênh thông báo (NotificationChannel) - Bắt buộc từ Android 8.0 (API 26) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Lấy tên và mô tả kênh từ strings.xml (cần tạo ở bước sau)
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH; // Mức độ ưu tiên cao để hiện pop-up
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Đăng ký kênh với hệ thống
            notificationManager.createNotificationChannel(channel);
        }

        // Xây dựng thông báo
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round) // Icon nhỏ hiển thị trên status bar
                        .setContentTitle(messageTitle) // Tiêu đề từ FCM
                        .setContentText(messageBody) // Nội dung từ FCM
                        .setAutoCancel(true) // Tự đóng khi người dùng nhấn vào
                        .setPriority(NotificationCompat.PRIORITY_HIGH) // Ưu tiên cao
                        .setDefaults(NotificationCompat.DEFAULT_ALL); // Bật rung, âm thanh, đèn LED mặc định
        // (Tùy chọn) Thêm Intent để mở Activity cụ thể khi nhấn vào
        // .setContentIntent(pendingIntent);

        // Hiển thị thông báo
        notificationManager.notify( (int) System.currentTimeMillis() /* ID duy nhất */, notificationBuilder.build());
    }
}