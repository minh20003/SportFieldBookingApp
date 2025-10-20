package com.example.sportfieldbookingapp.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.TimeSlotAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.Booking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private int fieldId;
    private Button btnSelectDate, btnConfirmBooking;
    private TextView tvSelectedDate, tvFieldName;
    private RecyclerView recyclerViewTimeSlots;
    private TimeSlotAdapter timeSlotAdapter;
    private List<String> timeSlotList = new ArrayList<>();
    private ApiService apiService;

    private String selectedDate = "";
    private String selectedTimeSlot = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // 1. Ánh xạ tất cả các View
        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvFieldName = findViewById(R.id.tvFieldName);
        recyclerViewTimeSlots = findViewById(R.id.recyclerViewTimeSlots);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // 2. Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // 3. Nhận ID sân
        fieldId = getIntent().getIntExtra("FIELD_ID", -1);
        if (fieldId == -1) {
            Toast.makeText(this, "Lỗi: Không có thông tin sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("BookingActivity", "Booking for Field ID: " + fieldId);

        // 4. Cài đặt RecyclerView
        recyclerViewTimeSlots.setLayoutManager(new GridLayoutManager(this, 4));
        timeSlotList.addAll(generateTimeSlots());
        timeSlotAdapter = new TimeSlotAdapter(timeSlotList);
        recyclerViewTimeSlots.setAdapter(timeSlotAdapter);

        // 5. Cài đặt các sự kiện OnClick
        setClickListeners();
    }

    private void setClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        timeSlotAdapter.setOnItemClickListener(timeSlot -> {
            selectedTimeSlot = timeSlot;
            Log.d("BookingActivity", "Selected Time: " + selectedTimeSlot);
        });

        btnConfirmBooking.setOnClickListener(v -> {
            confirmBooking();
        });
    }

    private void confirmBooking() {
        // 1. Kiểm tra dữ liệu
        if (selectedDate.isEmpty() || selectedTimeSlot.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày và giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Lỗi xác thực. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Tạo đối tượng request
        Booking bookingRequest = new Booking(fieldId, selectedDate, selectedTimeSlot, 200000); // Tạm thời để giá 200000

        // 4. Gọi API
        String authToken = "Bearer " + token;
        Call<Booking> call = apiService.createBooking(authToken, bookingRequest); // Sửa từ ApiService -> apiService

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingActivity.this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();
                    finish(); // Đặt sân thành công thì đóng màn hình này lại
                } else {
                    Toast.makeText(BookingActivity.this, "Đặt sân thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    tvSelectedDate.setText("Ngày đã chọn: " + selectedDate);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int i = 6; i <= 21; i++) {
            slots.add(String.format(Locale.getDefault(), "%02d:00", i));
        }
        return slots;
    }
}