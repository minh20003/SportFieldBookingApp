package com.example.sportfieldbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.SportField;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldDetailActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvDetailName, tvDetailAddress, tvDetailDescription;
    private Button btnGoToBooking;
    private ApiService apiService;
    private int fieldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);

        // Ánh xạ views
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnGoToBooking = findViewById(R.id.btnGoToBooking);

        // Nhận ID từ Intent
        fieldId = getIntent().getIntExtra("FIELD_ID", -1);

        // Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        if (fieldId != -1) {
            fetchFieldDetails(fieldId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sân", Toast.LENGTH_SHORT).show();
            finish();
        }
        btnGoToBooking.setOnClickListener(v -> {
            Intent intent = new Intent(FieldDetailActivity.this, BookingActivity.class);
            // Gửi ID của sân này sang màn hình đặt sân
            intent.putExtra("FIELD_ID", fieldId);
            startActivity(intent);
        });
    }

    private void fetchFieldDetails(int id) {
        Call<SportField> call = apiService.getFieldById(id);
        call.enqueue(new Callback<SportField>() {
            @Override
            public void onResponse(Call<SportField> call, Response<SportField> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUI(response.body());
                } else {
                    Toast.makeText(FieldDetailActivity.this, "Không thể tải chi tiết sân", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SportField> call, Throwable t) {
                Toast.makeText(FieldDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(SportField field) {
        tvDetailName.setText(field.getName());
        tvDetailAddress.setText(field.getAddress());
        tvDetailDescription.setText(field.getDescription());

        // Dùng Glide để load ảnh
        if (field.getImages() != null && !field.getImages().isEmpty()) {
            Glide.with(this)
                    .load(field.getImages().get(0))
                    .into(ivDetailImage);
        }
    }
}