package com.example.sportfieldbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.bumptech.glide.Glide;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.SportField;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.adapters.ReviewAdapter;
import com.example.sportfieldbookingapp.models.Review;
import com.example.sportfieldbookingapp.models.ReviewResponse;
import java.util.ArrayList;
import java.util.List;
public class FieldDetailActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvDetailName, tvDetailAddress, tvDetailDescription;
    private MaterialButton btnGoToBooking;
    private ExtendedFloatingActionButton fabBooking;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ApiService apiService;
    private int fieldId;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        
        // Ánh xạ views
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnGoToBooking = findViewById(R.id.btnGoToBooking);
        fabBooking = findViewById(R.id.fabBooking);

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

        View.OnClickListener bookingClickListener = v -> {
            Intent intent = new Intent(FieldDetailActivity.this, BookingActivity.class);
            intent.putExtra("FIELD_ID", fieldId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        };
        
        btnGoToBooking.setOnClickListener(bookingClickListener);
        fabBooking.setOnClickListener(bookingClickListener);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerViewReviews.setAdapter(reviewAdapter);

        if (fieldId != -1) {
            fetchFieldDetails(fieldId);
            fetchReviews(fieldId); // <<-- Gọi hàm lấy reviews
        } else {
            // ...
        }
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
        
        // Set collapsing toolbar title
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(field.getName());
        }

        // Dùng Glide để load ảnh
        if (field.getImages() != null && !field.getImages().isEmpty()) {
            Glide.with(this)
                    .load(field.getImages().get(0))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivDetailImage);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void fetchReviews(int fieldId) {
        Call<ReviewResponse> call = apiService.getReviewsForField(fieldId);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body().getRecords());
                    reviewAdapter.notifyDataSetChanged();
                } else {
                    // Không có review nào, không cần làm gì cả
                }
            }
            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Toast.makeText(FieldDetailActivity.this, "Lỗi tải đánh giá", Toast.LENGTH_SHORT).show();
            }
        });
    }
}