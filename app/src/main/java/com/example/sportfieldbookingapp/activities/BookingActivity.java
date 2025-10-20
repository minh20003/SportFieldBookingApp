package com.example.sportfieldbookingapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportfieldbookingapp.R;

public class BookingActivity extends AppCompatActivity {

    private int fieldId; // <<-- Khai báo biến

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // <<-- THÊM ĐOẠN CODE NÀY -->>
        // Nhận ID của sân từ Intent
        fieldId = getIntent().getIntExtra("FIELD_ID", -1);

        if (fieldId == -1) {
            Toast.makeText(this, "Lỗi: Không có thông tin sân", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có ID
            return;
        }

        // Dùng Logcat để kiểm tra xem đã nhận ID đúng chưa
        Log.d("BookingActivity", "Booking for Field ID: " + fieldId);
    }
}