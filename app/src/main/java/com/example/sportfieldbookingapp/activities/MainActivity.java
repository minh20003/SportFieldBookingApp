

package com.example.sportfieldbookingapp.activities;

// Thay "yourname" bằng package name của bạn

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.LoginResponse;
import com.example.sportfieldbookingapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister, tvForgotPassword;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ View từ layout XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister); // Ánh xạ TextView mới
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        // 2. Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // 3. Set sự kiện click cho nút Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // 4. Set sự kiện click cho TextView để chuyển sang màn hình Đăng ký
        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // Lấy text từ EditText
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra đầu vào cơ bản
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng User để gửi đi
        User userToLogin = new User(email, password);

        // Gọi API
        Call<LoginResponse> call = apiService.loginUser(userToLogin);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // 4. Xử lý kết quả trả về
                // Bên trong hàm onResponse của MainActivity.java

                if (response.isSuccessful() && response.body() != null) {
                    // Đăng nhập thành công
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(MainActivity.this, "Đăng nhập thành công! Chào " + loginResponse.getUser().getFullName(), Toast.LENGTH_LONG).show();

                    // --- BƯỚC 1: LƯU TOKEN ---
                    // Tạo hoặc mở file SharedPreferences có tên "AppPrefs"
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Lưu token và tên người dùng
                    editor.putString("USER_TOKEN", loginResponse.getToken());
                    editor.putString("USER_NAME", loginResponse.getUser().getFullName());
                    editor.putInt("USER_ID", loginResponse.getUser().getId());
                    // Áp dụng các thay đổi
                    editor.apply();

                    Log.d("LOGIN_SUCCESS", "Token saved: " + loginResponse.getToken());

                    // TODO: Chuyển sang màn hình chính
                    // --- BƯỚC 2: CHUYỂN MÀN HÌNH ---
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Đóng MainActivity lại để người dùng không thể quay lại

                } else {
                    // Đăng nhập thất bại (sai email, password,...)
                    Toast.makeText(MainActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Lỗi kết nối mạng hoặc server
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LOGIN_ERROR", "onFailure: " + t.getMessage());
            }
        });
    }
}
