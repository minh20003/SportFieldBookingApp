package com.example.sportfieldbookingapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.User;
// API đăng ký chỉ trả về message, chúng ta có thể dùng lại LoginResponse hoặc tạo model mới
// Để đơn giản, ta tạm dùng lại LoginResponse vì nó cũng có trường "message"
import com.example.sportfieldbookingapp.models.LoginResponse;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private Button btnRegister;
    private ApiService apiService;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setup Toolbar with back button
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User userToRegister = new User(fullName, email, password);

        Call<LoginResponse> call = apiService.registerUser(userToRegister);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    // Đăng ký thành công thì quay về màn hình đăng nhập
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Email có thể đã tồn tại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("REGISTER_ERROR", "onFailure: " + t.getMessage());
            }
        });
    }
}