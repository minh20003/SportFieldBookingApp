package com.example.sportfieldbookingapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sportfieldbookingapp.R;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity xử lý cuộc gọi video/voice sử dụng Zegocloud
 * 
 * Để sử dụng:
 * 1. Đăng ký tài khoản tại https://console.zegocloud.com/
 * 2. Tạo project và lấy AppID, AppSign
 * 3. Thay thế YOUR_APP_ID và YOUR_APP_SIGN bên dưới
 */
public class CallActivity extends AppCompatActivity {

    private static final String TAG = "CallActivity";
    
    // ============================================
    // QUAN TRỌNG: Thay bằng credentials của bạn từ Zegocloud Console
    // ============================================
    private static final long APP_ID = 2087551041; // Thay bằng AppID của bạn (số)
    private static final String APP_SIGN = "9151e5534d58585ee0bb11c62092e13a21f750d9bfa177fa28c3069f71fb8255"; // Thay bằng AppSign của bạn (chuỗi)
    
    // Intent extras
    public static final String EXTRA_CALL_ID = "call_id";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USER_NAME = "user_name";
    public static final String EXTRA_IS_VIDEO_CALL = "is_video_call";
    public static final String EXTRA_TARGET_USER_ID = "target_user_id";
    public static final String EXTRA_TARGET_USER_NAME = "target_user_name";
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private String callId;
    private String userId;
    private String userName;
    private boolean isVideoCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        // Kiểm tra credentials
        if (APP_ID == 0L || APP_SIGN.isEmpty()) {
            Toast.makeText(this, "Vui lòng cấu hình Zegocloud credentials trong CallActivity.java", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Zegocloud credentials not configured! Please set APP_ID and APP_SIGN");
            finish();
            return;
        }

        // Lấy thông tin từ Intent
        Intent intent = getIntent();
        callId = intent.getStringExtra(EXTRA_CALL_ID);
        userId = intent.getStringExtra(EXTRA_USER_ID);
        userName = intent.getStringExtra(EXTRA_USER_NAME);
        isVideoCall = intent.getBooleanExtra(EXTRA_IS_VIDEO_CALL, true);

        // Nếu không có userId, lấy từ SharedPreferences
        if (userId == null || userId.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            userId = String.valueOf(prefs.getInt("USER_ID", 0));
            userName = prefs.getString("USER_NAME", "User");
        }

        // Tạo callId nếu chưa có (khi gọi đi)
        if (callId == null || callId.isEmpty()) {
            String targetUserId = intent.getStringExtra(EXTRA_TARGET_USER_ID);
            if (targetUserId != null) {
                // Tạo unique call ID từ 2 user IDs
                callId = generateCallId(userId, targetUserId);
            } else {
                callId = "call_" + System.currentTimeMillis();
            }
        }

        Log.d(TAG, "Starting call - CallID: " + callId + ", UserID: " + userId + ", UserName: " + userName + ", IsVideo: " + isVideoCall);

        // Kiểm tra quyền
        if (checkPermissions()) {
            startCall();
        } else {
            requestPermissions();
        }
    }

    private String generateCallId(String myUserId, String targetUserId) {
        // Sắp xếp để đảm bảo cùng callId cho cả 2 người
        try {
            int id1 = Integer.parseInt(myUserId);
            int id2 = Integer.parseInt(targetUserId);
            if (id1 < id2) {
                return "room_" + id1 + "_" + id2;
            } else {
                return "room_" + id2 + "_" + id1;
            }
        } catch (NumberFormatException e) {
            return "room_" + myUserId + "_" + targetUserId;
        }
    }

    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                    permissionsToRequest.toArray(new String[0]), 
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                startCall();
            } else {
                Toast.makeText(this, "Cần cấp quyền camera và microphone để gọi điện", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCall() {
        // Cấu hình cuộc gọi
        ZegoUIKitPrebuiltCallConfig config;
        if (isVideoCall) {
            config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
        } else {
            config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
        }

        // Tạo fragment cuộc gọi
        ZegoUIKitPrebuiltCallFragment fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
                APP_ID,
                APP_SIGN,
                userId,
                userName,
                callId,
                config
        );

        // Thêm fragment vào container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.call_container, fragment)
                .commitNow();
    }

    /**
     * Helper method để bắt đầu cuộc gọi video từ Activity khác
     */
    public static void startVideoCall(Context context, String targetUserId, String targetUserName, 
                                       String currentUserId, String currentUserName) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.putExtra(EXTRA_TARGET_USER_ID, targetUserId);
        intent.putExtra(EXTRA_TARGET_USER_NAME, targetUserName);
        intent.putExtra(EXTRA_USER_ID, currentUserId);
        intent.putExtra(EXTRA_USER_NAME, currentUserName);
        intent.putExtra(EXTRA_IS_VIDEO_CALL, true);
        context.startActivity(intent);
    }

    /**
     * Helper method để bắt đầu cuộc gọi thoại từ Activity khác
     */
    public static void startVoiceCall(Context context, String targetUserId, String targetUserName, 
                                       String currentUserId, String currentUserName) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.putExtra(EXTRA_TARGET_USER_ID, targetUserId);
        intent.putExtra(EXTRA_TARGET_USER_NAME, targetUserName);
        intent.putExtra(EXTRA_USER_ID, currentUserId);
        intent.putExtra(EXTRA_USER_NAME, currentUserName);
        intent.putExtra(EXTRA_IS_VIDEO_CALL, false);
        context.startActivity(intent);
    }
}
