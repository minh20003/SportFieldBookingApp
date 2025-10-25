package com.example.sportfieldbookingapp.activities; // <<< THAY BẰNG PACKAGE CỦA BẠN

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull; // Đảm bảo import này tồn tại
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText; // Sửa import
import com.example.sportfieldbookingapp.R; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.api.ApiClient; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.api.ApiService; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.models.GenericResponse; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.models.LoginResponse; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.models.User; // <<< THAY BẰNG PACKAGE CỦA BẠN

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Tag cho Logcat

    private TextInputEditText etEmail, etPassword; // Sửa thành TextInputEditText
    private Button btnLogin;
    private TextView tvGoToRegister, tvForgotPassword;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Activity created");

        // 1. Ánh xạ View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        Log.d(TAG, "onCreate: Views mapped");

        // 2. Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);
        Log.d(TAG, "onCreate: ApiService initialized");

        // 3. Set sự kiện click
        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "btnLogin clicked");
            loginUser();
        });
        tvGoToRegister.setOnClickListener(v -> {
            Log.d(TAG, "tvGoToRegister clicked");
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        tvForgotPassword.setOnClickListener(v -> {
            Log.d(TAG, "tvForgotPassword clicked");
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        Log.d(TAG, "onCreate: Click listeners set");
    }

    private void loginUser() {
        Log.d(TAG, "loginUser: Function started");
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Log.w(TAG, "loginUser: Email or password empty");
            Toast.makeText(MainActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "loginUser: Input validated");

        btnLogin.setEnabled(false); // Vô hiệu hóa nút khi đang gọi API
        btnLogin.setText("Đang đăng nhập...");

        User userToLogin = new User(email, password);

        Call<LoginResponse> call = apiService.loginUser(userToLogin);
        Log.d(TAG, "loginUser: Calling API...");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                Log.d(TAG, "loginUser onResponse: Code=" + response.code());
                btnLogin.setEnabled(true); // Kích hoạt lại nút
                btnLogin.setText("Đăng Nhập");

                if (!isFinishing()) { // Kiểm tra Activity còn hoạt động
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        Log.i(TAG, "loginUser onResponse: Success - User: " + loginResponse.getUser().getFullName());
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công! Chào " + loginResponse.getUser().getFullName(), Toast.LENGTH_LONG).show();

                        // Lưu thông tin vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_TOKEN", loginResponse.getToken());
                        editor.putString("USER_NAME", loginResponse.getUser().getFullName());
                        editor.putInt("USER_ID", loginResponse.getUser().getId());
                        editor.apply();
                        Log.d(TAG, "loginUser onResponse: User info saved to SharedPreferences.");

                        // Kiểm tra và gửi FCM token còn thiếu
                        String pendingFcmToken = sharedPreferences.getString("PENDING_FCM_TOKEN", null);
                        if (pendingFcmToken != null) {
                            sendFcmTokenToServer(pendingFcmToken);
                            SharedPreferences.Editor editorFcm = sharedPreferences.edit();
                            editorFcm.remove("PENDING_FCM_TOKEN");
                            editorFcm.apply();
                            Log.d(TAG, "loginUser onResponse: Pending FCM token found and send attempt initiated.");
                        }

                        // Chuyển sang màn hình chính
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa các màn hình cũ
                        startActivity(intent);
                        finish(); // Đóng màn hình đăng nhập

                    } else {
                        Log.e(TAG, "loginUser onResponse: Login failed - Code=" + response.code());
                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại! Sai email hoặc mật khẩu.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "loginUser onFailure: " + t.getMessage(), t);
                btnLogin.setEnabled(true); // Kích hoạt lại nút
                btnLogin.setText("Đăng Nhập");
                if (!isFinishing()) {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm gửi FCM Token còn thiếu lên server
    private void sendFcmTokenToServer(String token) {
        if (token == null) return;
        Log.d(TAG, "Attempting to send pending FCM token: " + token);
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("USER_TOKEN", null);

        if (authToken != null) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<GenericResponse> call = apiService.updateFcmToken("Bearer " + authToken, token);
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Pending FCM Token sent successfully.");
                    } else {
                        Log.w(TAG, "Failed to send pending FCM Token. Code: " + response.code());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Error sending pending FCM Token: " + t.getMessage());
                }
            });
        }
    }
}