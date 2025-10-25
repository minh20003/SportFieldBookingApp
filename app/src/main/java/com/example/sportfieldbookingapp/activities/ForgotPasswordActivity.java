package com.example.sportfieldbookingapp.activities;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // <<< Ensure this is imported
import android.view.MenuItem; // Import MenuItem
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;

// Import Gson if you haven't already (needed for error parsing)
import com.google.gson.Gson;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity"; // Tag for filtering logs

    private TextInputEditText etForgotEmail, etOtp, etNewPassword;
    private Button btnSendOtp, btnVerifyOtp, btnResetPassword;
    private LinearLayout layoutEnterEmail, layoutEnterOtp, layoutResetPassword;
    private ApiService apiService;

    private String userEmail; // Store email from step 1
    private String enteredOtp; // Store OTP from step 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Log.d(TAG, "onCreate: Activity created");

        // Add Toolbar/ActionBar with Back Button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quên mật khẩu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Map Views
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        layoutEnterEmail = findViewById(R.id.layoutEnterEmail);

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        layoutEnterOtp = findViewById(R.id.layoutEnterOtp);

        etNewPassword = findViewById(R.id.etNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        layoutResetPassword = findViewById(R.id.layoutResetPassword);
        Log.d(TAG, "onCreate: Views mapped");

        // Initialize ApiService
        apiService = ApiClient.getClient().create(ApiService.class);
        Log.d(TAG, "onCreate: ApiService initialized");

        // Set Click Listeners
        btnSendOtp.setOnClickListener(v -> {
            Log.d(TAG, "btnSendOtp clicked");
            sendOtpRequest();
        });
        btnVerifyOtp.setOnClickListener(v -> {
            Log.d(TAG, "btnVerifyOtp clicked");
            verifyOtpRequest(); // Changed to server verification
        });
        btnResetPassword.setOnClickListener(v -> {
            Log.d(TAG, "btnResetPassword clicked");
            resetPasswordRequest();
        });
        Log.d(TAG, "onCreate: Click listeners set");

        // Show only the first layout initially
        showLayout(layoutEnterEmail);
    }

    // Handle Back button press in ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Function to show only one layout and hide others
    private void showLayout(LinearLayout layoutToShow) {
        layoutEnterEmail.setVisibility(View.GONE);
        layoutEnterOtp.setVisibility(View.GONE);
        layoutResetPassword.setVisibility(View.GONE);
        layoutToShow.setVisibility(View.VISIBLE);
    }

    // --- Stage 1: Send OTP Request ---
    private void sendOtpRequest() {
        Log.d(TAG, "sendOtpRequest: Function started");
        userEmail = etForgotEmail.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail) || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Log.w(TAG, "sendOtpRequest: Invalid email entered: " + userEmail);
            Toast.makeText(this, "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "sendOtpRequest: Email validated: " + userEmail);

        btnSendOtp.setEnabled(false);
        btnSendOtp.setText("Đang gửi...");
        Log.d(TAG, "sendOtpRequest: Calling API...");

        Call<GenericResponse> call = apiService.requestPasswordReset(userEmail);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                Log.d(TAG, "sendOtpRequest onResponse: Code=" + response.code());
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi mã OTP");
                // Check if activity is still running before showing UI
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "sendOtpRequest onResponse: Success - " + response.body().getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        // Switch to OTP entry layout
                        showLayout(layoutEnterOtp);
                    } else {
                        Log.e(TAG, "sendOtpRequest onResponse: Error - Code=" + response.code());
                        Toast.makeText(ForgotPasswordActivity.this, "Gửi OTP thất bại. Email có thể không tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "sendOtpRequest onFailure: " + t.getMessage(), t);
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi mã OTP");
                if (!isFinishing()) { // Check if activity is still running
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --- Stage 2: Verify OTP Request ---
    private void verifyOtpRequest() {
        Log.d(TAG, "verifyOtpRequest: Function started");
        enteredOtp = etOtp.getText().toString().trim();
        if (TextUtils.isEmpty(enteredOtp) || enteredOtp.length() != 6) {
            Log.w(TAG, "verifyOtpRequest: Invalid OTP entered: " + enteredOtp);
            Toast.makeText(this, "Vui lòng nhập mã OTP gồm 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "verifyOtpRequest: OTP format validated: " + enteredOtp);

        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Đang xác thực...");
        Log.d(TAG, "verifyOtpRequest: Calling API...");

        Call<GenericResponse> call = apiService.verifyOtp(userEmail, enteredOtp);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                Log.d(TAG, "verifyOtpRequest onResponse: Code=" + response.code());
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác nhận OTP");

                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "verifyOtpRequest onResponse: Success - " + response.body().getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        // OTP is valid, show the password reset layout
                        showLayout(layoutResetPassword);
                    } else {
                        Log.e(TAG, "verifyOtpRequest onResponse: Error - Code=" + response.code());
                        // OTP invalid or expired, show error from server
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
                Log.e(TAG, "verifyOtpRequest onFailure: " + t.getMessage(), t);
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác nhận OTP");
                if (!isFinishing()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --- Stage 3: Reset Password Request ---
    private void resetPasswordRequest() {
        Log.d(TAG, "resetPasswordRequest: Function started");
        String newPassword = etNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            Log.w(TAG, "resetPasswordRequest: Invalid new password entered");
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới (ít nhất 6 ký tự)", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "resetPasswordRequest: New password validated");

        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Đang xử lý...");
        Log.d(TAG, "resetPasswordRequest: Calling API...");

        // Call the API using stored email, stored OTP, and new password
        Call<GenericResponse> call = apiService.resetPassword(userEmail, enteredOtp, newPassword);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                Log.d(TAG, "resetPasswordRequest onResponse: Code=" + response.code());
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");

                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "resetPasswordRequest onResponse: Success - " + response.body().getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        finish(); // Close activity after successful reset
                    } else {
                        Log.e(TAG, "resetPasswordRequest onResponse: Error - Code=" + response.code());
                        // Get specific error message from server if possible
                        String errorMessage = "Đặt lại mật khẩu thất bại."; // Default
                        if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson();
                                GenericResponse errorResponse = gson.fromJson(response.errorBody().string(), GenericResponse.class);
                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMessage = errorResponse.getMessage(); // Use server's specific message
                                }
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "resetPasswordRequest onFailure: " + t.getMessage(), t);
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");
                if (!isFinishing()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}