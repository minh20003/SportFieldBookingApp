package com.example.sportfieldbookingapp.activities; // <<< THAY BẰNG PACKAGE CỦA BẠN

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri; // <<< Import
import android.os.Build;
import android.os.Bundle;
import android.util.Log; // <<< Import
import android.view.MenuItem; // <<< Import
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull; // <<< Import
import androidx.appcompat.app.AppCompatActivity;

// <<< THAY PACKAGE CỦA BẠN >>>
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.JoinPostRequest;
import com.example.sportfieldbookingapp.models.TeammatePost;
// <<< HẾT THAY PACKAGE >>>

// <<< THÊM IMPORT GSON VÀ IOEXCEPTION >>>
import com.google.gson.Gson;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeammatePostDetailActivity extends AppCompatActivity {

    private static final String TAG = "TeammatePostDetail";

    // Khai báo đầy đủ các View
    private TextView tvDetailSportType, tvDetailPosterName, tvDetailPlayDateTime,
            tvDetailPlayersNeeded, tvDetailDescription, tvDetailContactInfo;
    private Button btnDetailAction; // <<< Khai báo nút
    private ApiService apiService;
    private TeammatePost currentPost;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammate_post_detail);
        Log.d(TAG, "onCreate: Activity created");

        // Bật nút Back trên Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết Tin đăng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ánh xạ Views (bao gồm cả nút)
        tvDetailSportType = findViewById(R.id.tvDetailSportType);
        tvDetailPosterName = findViewById(R.id.tvDetailPosterName);
        tvDetailPlayDateTime = findViewById(R.id.tvDetailPlayDateTime);
        tvDetailPlayersNeeded = findViewById(R.id.tvDetailPlayersNeeded);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailContactInfo = findViewById(R.id.tvDetailContactInfo);
        btnDetailAction = findViewById(R.id.btnDetailAction); // <<< Ánh xạ nút

        apiService = ApiClient.getClient().create(ApiService.class);

        // Lấy User ID hiện tại
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("USER_ID", -1);

        // Nhận đối tượng Post từ Intent (cách làm mới)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentPost = getIntent().getSerializableExtra("POST_DETAIL", TeammatePost.class);
        } else {
            currentPost = (TeammatePost) getIntent().getSerializableExtra("POST_DETAIL");
        }

        // Hiển thị dữ liệu nếu post hợp lệ
        if (currentPost != null) {
            Log.d(TAG, "onCreate: Received Post ID: " + currentPost.getId());
            populateUI(currentPost);
            setupActionButton(currentPost);
        } else {
            Log.e(TAG, "onCreate: No Post data received!");
            Toast.makeText(this, "Lỗi: Không thể tải chi tiết tin đăng.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có dữ liệu
        }
    }

    // Xử lý nút Back trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity này
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Hiển thị dữ liệu lên giao diện
    private void populateUI(TeammatePost post) {
        tvDetailSportType.setText("Môn: " + post.getSportType());
        tvDetailPosterName.setText("Người đăng: " + post.getPosterName());
        tvDetailPlayDateTime.setText("Thời gian: " + post.getTimeSlot() + " - " + post.getPlayDate());
        tvDetailPlayersNeeded.setText("Cần tìm: " + post.getPlayersNeeded() + " người");
        tvDetailDescription.setText(post.getDescription());

        // Hiển thị SĐT và cho phép gọi
        String phone = post.getPosterPhone(); // <<< Đảm bảo hàm này tồn tại trong TeammatePost.java
        if (phone != null && !phone.isEmpty() && !phone.equalsIgnoreCase("Chưa cung cấp")) {
            tvDetailContactInfo.setText("SĐT: " + phone);
            tvDetailContactInfo.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Could not launch dialer", e);
                    Toast.makeText(this, "Không thể mở ứng dụng gọi điện.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvDetailContactInfo.setText("SĐT: Chưa cung cấp");
            tvDetailContactInfo.setOnClickListener(null);
        }
    }

    // Cài đặt chức năng cho nút bấm chính (Tham gia, Sửa)
    private void setupActionButton(TeammatePost post) {
        if (currentUserId == post.getUserId()) {
            btnDetailAction.setText("Sửa tin");
            btnDetailAction.setOnClickListener(v -> {
                Log.d(TAG, "Edit button clicked for post ID: " + post.getId());
                Intent intent = new Intent(TeammatePostDetailActivity.this, EditPostActivity.class);
                intent.putExtra("POST_TO_EDIT", post);
                startActivity(intent);
                finish();
            });
        } else {
            btnDetailAction.setText("Tham gia");
            btnDetailAction.setOnClickListener(v -> {
                Log.d(TAG, "Join button clicked for post ID: " + post.getId());
                joinPost(post);
            });
        }
    }

    // Hàm gọi API tham gia
    private void joinPost(TeammatePost post) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tham gia", Toast.LENGTH_SHORT).show();
            return;
        }

        btnDetailAction.setEnabled(false);
        btnDetailAction.setText("Đang xử lý...");

        String authToken = "Bearer " + token;
        JoinPostRequest request = new JoinPostRequest(post.getId());

        Call<GenericResponse> call = apiService.joinTeammatePost(authToken, request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                // Kích hoạt lại nút TRƯỚC khi kiểm tra isFinishing
                btnDetailAction.setEnabled(true);
                btnDetailAction.setText("Tham gia");

                if (!isFinishing()) { // Kiểm tra Activity còn tồn tại
                    if (response.isSuccessful() && response.body() != null) {
                        Log.i(TAG, "Join successful for post ID: " + post.getId());
                        Toast.makeText(TeammatePostDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish(); // Đóng màn hình sau khi tham gia thành công
                    } else {
                        // Xử lý lỗi từ server
                        String errorMessage = "Tham gia thất bại.";
                        if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson(); // <<< Import đã thêm
                                GenericResponse errorResponse = gson.fromJson(response.errorBody().string(), GenericResponse.class);
                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMessage = errorResponse.getMessage();
                                }
                            } catch (IOException e) { Log.e(TAG, "Error parsing error body", e); } // <<< Import đã thêm
                        }
                        Log.e(TAG, "Join failed for post ID: " + post.getId() + ", Error: " + errorMessage);
                        Toast.makeText(TeammatePostDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnDetailAction.setEnabled(true);
                btnDetailAction.setText("Tham gia");
                if(!isFinishing()){
                    Log.e(TAG, "Join network failure for post ID: " + post.getId(), t);
                    Toast.makeText(TeammatePostDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}