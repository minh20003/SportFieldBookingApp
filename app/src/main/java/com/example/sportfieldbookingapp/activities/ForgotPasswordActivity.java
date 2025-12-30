package com.example.sportfieldbookingapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private TextInputEditText etForgotEmail, etOtp, etNewPassword;
    private Button btnSendOtp, btnVerifyOtp, btnResetPassword;
    private LinearLayout layoutEnterEmail, layoutEnterOtp, layoutResetPassword;
    private ImageButton btnBack;
    private TextView tvBackToLogin;
    private ApiService apiService;

    private String userEmail;
    private String enteredOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupListeners();

        apiService = ApiClient.getClient().create(ApiService.class);
        showLayout(layoutEnterEmail);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        layoutEnterEmail = findViewById(R.id.layoutEnterEmail);

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        layoutEnterOtp = findViewById(R.id.layoutEnterOtp);

        etNewPassword = findViewById(R.id.etNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        layoutResetPassword = findViewById(R.id.layoutResetPassword);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        tvBackToLogin.setOnClickListener(v -> finish());

        btnSendOtp.setOnClickListener(v -> sendOtpRequest());
        btnVerifyOtp.setOnClickListener(v -> verifyOtpRequest());
        btnResetPassword.setOnClickListener(v -> resetPasswordRequest());
    }

    private void showLayout(LinearLayout layoutToShow) {
        layoutEnterEmail.setVisibility(View.GONE);
        layoutEnterOtp.setVisibility(View.GONE);
        layoutResetPassword.setVisibility(View.GONE);
        layoutToShow.setVisibility(View.VISIBLE);
    }

    private void sendOtpRequest() {
        userEmail = etForgotEmail.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail) || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSendOtp.setEnabled(false);
        btnSendOtp.setText("Đang gửi...");

        Call<GenericResponse> call = apiService.requestPasswordReset(userEmail);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi Mã OTP");
                
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        showLayout(layoutEnterOtp);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Gửi OTP thất bại. Email có thể không tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi Mã OTP");
                if (!isFinishing()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyOtpRequest() {
        enteredOtp = etOtp.getText().toString().trim();
        if (TextUtils.isEmpty(enteredOtp) || enteredOtp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập mã OTP gồm 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Đang xác thực...");

        Call<GenericResponse> call = apiService.verifyOtp(userEmail, enteredOtp);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác Nhận");

                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        showLayout(layoutResetPassword);
                    } else {
                        String errorMessage = "Mã OTP không hợp lệ hoặc đã hết hạn.";
                        if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson();
                                GenericResponse errorResponse = gson.fromJson(response.errorBody().string(), GenericResponse.class);
                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMessage = errorResponse.getMessage();
                                }
                            } catch (IOException e) { e.printStackTrace(); }
                        }
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác Nhận");
                if (!isFinishing()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPasswordRequest() {
        String newPassword = etNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới (ít nhất 6 ký tự)", Toast.LENGTH_LONG).show();
            return;
        }

        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Đang xử lý...");

        Call<GenericResponse> call = apiService.resetPassword(userEmail, enteredOtp, newPassword);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt Lại Mật Khẩu");

                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ForgotPasswordActivity.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String errorMessage = "Đặt lại mật khẩu thất bại.";
                        if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson();
                                GenericResponse errorResponse = gson.fromJson(response.errorBody().string(), GenericResponse.class);
                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMessage = errorResponse.getMessage();
                                }
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt Lại Mật Khẩu");
                if (!isFinishing()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}