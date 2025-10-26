package com.example.sportfieldbookingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.activities.HomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "SportBookingChannel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String title = "Thông báo mới"; // Mặc định
        String body = "Bạn có thông báo mới."; // Mặc định

        // Lấy tiêu đề và nội dung từ notification payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle() != null ? remoteMessage.getNotification().getTitle() : title;
            body = remoteMessage.getNotification().getBody() != null ? remoteMessage.getNotification().getBody() : body;
            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);
        }

        // Lấy dữ liệu từ data payload (nếu có)
        String notificationType = remoteMessage.getData().get("notification_type");
        String postId = remoteMessage.getData().get("post_id");
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        // Gửi thông báo đi kèm dữ liệu (nếu có) để xử lý click
        sendNotification(title, body, notificationType, postId);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: " + token);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("USER_TOKEN", null);

        if (authToken != null) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("PENDING_FCM_TOKEN", token);
            editor.apply();
        }
    }

    private void sendNotification(String messageTitle, String messageBody, String notificationType, String postId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo Kênh thông báo (NotificationChannel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo Intent để mở HomeActivity
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Thêm dữ liệu vào Intent dựa trên loại thông báo
        if ("teammate_join".equals(notificationType)) {
            intent.putExtra("NAVIGATE_TO", "find_teammate"); // Bảo HomeActivity mở tab Tìm người
            if (postId != null) {
                intent.putExtra("POST_ID", postId); // Gửi kèm ID (nếu cần mở chi tiết)
            }
        }
        // TODO: Thêm các else if cho loại thông báo khác (ví dụ: booking_confirmed, booking_cancelled)
        // else if ("booking_confirmed".equals(notificationType)) {
        //     intent.putExtra("NAVIGATE_TO", "my_bookings");
        //     intent.putExtra("BOOKING_ID", bookingId); // Cần gửi bookingId từ backend
        // }

        // Tạo PendingIntent - FLAG_UPDATE_CURRENT để cập nhật dữ liệu extra nếu intent giống nhau
        // FLAG_IMMUTABLE là bắt buộc
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Xây dựng thông báo
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round) // Icon nhỏ
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL) // Âm thanh, rung mặc định
                        .setContentIntent(pendingIntent); // <<< GẮN PENDINGINTENT

        // Hiển thị thông báo với ID duy nhất (dựa trên thời gian)
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}