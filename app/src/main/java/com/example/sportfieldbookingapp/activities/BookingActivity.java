package com.example.sportfieldbookingapp.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.sportfieldbookingapp.models.PaymentResponse;
import com.example.sportfieldbookingapp.models.TimeSlot;
import com.example.sportfieldbookingapp.models.AvailabilityResponse;

import android.content.Intent;

public class BookingActivity extends AppCompatActivity {

    private int fieldId;
    private String fieldName = ""; // ⭐ THÊM BIẾN TÊN SÂN

    private Button btnSelectDate, btnConfirmBooking;
    private TextView tvSelectedDate, tvFieldName;
    private RecyclerView recyclerViewTimeSlots;
    private TimeSlotAdapter timeSlotAdapter;
    private List<String> timeSlotList = new ArrayList<>();
    private ApiService apiService;
    private Toolbar toolbar;

    private String selectedDate = "";
    private String selectedTimeSlot = "";

    private TimeSlot selectedTimeSlotObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Setup Toolbar with back button
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 1. Ánh xạ tất cả các View
        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvFieldName = findViewById(R.id.tvFieldName);
        recyclerViewTimeSlots = findViewById(R.id.recyclerViewTimeSlots);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // 2. Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // 3. Nhận ID sân và TÊN SÂN từ Intent
        fieldId = getIntent().getIntExtra("FIELD_ID", -1);
        fieldName = getIntent().getStringExtra("FIELD_NAME"); // ⭐ NHẬN TÊN SÂN

        if (fieldId == -1) {
            Toast.makeText(this, "Lỗi: Không có thông tin sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ⭐ Hiển thị tên sân
        if (fieldName != null && !fieldName.isEmpty()) {
            tvFieldName.setText("Đặt sân cho: " + fieldName);
        } else {
            tvFieldName.setText("Đặt sân cho: [Tên sân]");
        }

        Log.d("BookingActivity", "Booking for Field ID: " + fieldId + ", Name: " + fieldName);

        // 4. Cài đặt RecyclerView
        recyclerViewTimeSlots.setLayoutManager(new GridLayoutManager(this, 4));
        timeSlotAdapter = new TimeSlotAdapter(new ArrayList<>());
        recyclerViewTimeSlots.setAdapter(timeSlotAdapter);

        // 5. Cài đặt các sự kiện OnClick
        setClickListeners();
    }

    private void setClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        timeSlotAdapter.setOnItemClickListener(slot -> {
            selectedTimeSlotObject = slot;
            selectedTimeSlot = slot.getTimeSlot();
            Log.d("BookingActivity", "Selected Time: " + selectedTimeSlot + ", Price: " + slot.getPrice());
        });

        btnConfirmBooking.setOnClickListener(v -> {
            confirmBooking();
        });
    }

    private void confirmBooking() {
        // Log debug
        Log.d("BookingActivity", "=== CONFIRM BOOKING START ===");
        Log.d("BookingActivity", "Field Name: " + fieldName);
        Log.d("BookingActivity", "selectedDate: " + selectedDate);
        Log.d("BookingActivity", "selectedTimeSlot: " + selectedTimeSlot);
        Log.d("BookingActivity", "selectedTimeSlotObject: " + (selectedTimeSlotObject != null ? "NOT NULL" : "NULL"));

        if (selectedTimeSlotObject != null) {
            Log.d("BookingActivity", "isAvailable: " + selectedTimeSlotObject.isAvailable());
            Log.d("BookingActivity", "price: " + selectedTimeSlotObject.getPrice());
        }

        // 1. Kiểm tra dữ liệu
        if (selectedDate.isEmpty() || selectedTimeSlot.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày và giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTimeSlotObject == null) {
            Toast.makeText(this, "Vui lòng chọn khung giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!selectedTimeSlotObject.isAvailable()) {
            Toast.makeText(this, "Khung giờ này đã được đặt. Vui lòng chọn khung giờ khác.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Lấy token
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            Toast.makeText(this, "Lỗi xác thực. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        String authToken = "Bearer " + token;

        // 3. TẠO ĐƠN ĐẶT SÂN TRƯỚC
        long amount = (long) selectedTimeSlotObject.getPrice();
        Booking bookingRequest = new Booking(fieldId, selectedDate, selectedTimeSlot, amount);

        Call<Booking> createBookingCall = apiService.createBooking(authToken, bookingRequest);
        createBookingCall.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 4. LẤY URL THANH TOÁN SAU KHI TẠO ĐƠN THÀNH CÔNG
                    int bookingId = response.body().getId();

                    // ⭐ Tạo mô tả đơn hàng có tên sân
                    String orderDesc = "Dat san: " + fieldName + " - Ngay " + selectedDate + " - " + selectedTimeSlot;

                    Call<PaymentResponse> paymentCall = apiService.createVnPayPayment(
                            String.valueOf(bookingId),
                            amount,
                            orderDesc, // ⭐ Mô tả có tên sân
                            "billpayment",
                            "vn",
                            ""
                    );

                    paymentCall.enqueue(new Callback<PaymentResponse>() {
                        @Override
                        public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String paymentUrl = response.body().getPaymentUrl();
                                Intent intent = new Intent(BookingActivity.this, PaymentWebViewActivity.class);
                                intent.putExtra("PAYMENT_URL", paymentUrl);
                                intent.putExtra("FIELD_NAME", fieldName); // ⭐ Truyền tên sân
                                intent.putExtra("BOOKING_ID", bookingId);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(BookingActivity.this, "Không thể tạo yêu cầu thanh toán.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PaymentResponse> call, Throwable t) {
                            Toast.makeText(BookingActivity.this, "Lỗi khi tạo yêu cầu thanh toán: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    String errorMsg = "Không thể tạo đơn đặt sân. Vui lòng thử lại.";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("BookingActivity", "Error body: " + errorBody);

                            if (errorBody.contains("SLOT_ALREADY_BOOKED") ||
                                    errorBody.contains("đã được đặt")) {
                                errorMsg = "Khung giờ này đã được đặt. Vui lòng chọn khung giờ khác.";
                                loadAvailableTimeSlots();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("BookingActivity", "Error parsing error body", e);
                    }

                    Toast.makeText(BookingActivity.this, errorMsg, Toast.LENGTH_LONG).show();
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
                    loadAvailableTimeSlots();
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void loadAvailableTimeSlots() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày trước", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("BookingActivity", "Loading time slots for date: " + selectedDate + ", field: " + fieldId);

        Call<AvailabilityResponse> call = apiService.checkAvailability(fieldId, selectedDate);
        call.enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TimeSlot> slots = response.body().getSlots();

                    if (slots != null && !slots.isEmpty()) {
                        timeSlotAdapter = new TimeSlotAdapter(slots);
                        timeSlotAdapter.setOnItemClickListener(slot -> {
                            selectedTimeSlotObject = slot;
                            selectedTimeSlot = slot.getTimeSlot();
                            Log.d("BookingActivity", "Selected Time: " + selectedTimeSlot + ", Price: " + slot.getPrice());
                        });
                        recyclerViewTimeSlots.setAdapter(timeSlotAdapter);

                        int availableCount = 0;
                        for (TimeSlot slot : slots) {
                            if (slot.isAvailable()) {
                                availableCount++;
                            }
                        }

                        Toast.makeText(BookingActivity.this,
                                "Có " + availableCount + "/" + slots.size() + " khung giờ còn trống",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookingActivity.this,
                                "Không có khung giờ nào cho ngày này",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("BookingActivity", "Error loading slots: " + response.code());
                    Toast.makeText(BookingActivity.this,
                            "Không thể tải khung giờ. Mã lỗi: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {
                Log.e("BookingActivity", "Error loading slots", t);
                Toast.makeText(BookingActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

                // Fallback
                List<String> defaultSlotStrings = generateTimeSlots();
                List<TimeSlot> defaultSlots = new ArrayList<>();

                for (String timeStr : defaultSlotStrings) {
                    boolean isPeak = false;
                    double price = 150000;

                    try {
                        int hour = Integer.parseInt(timeStr.substring(0, 2));
                        if (hour >= 17 && hour <= 21) {
                            isPeak = true;
                            price = 200000;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    TimeSlot slot = new TimeSlot(timeStr, price, isPeak, true);
                    defaultSlots.add(slot);
                }

                timeSlotAdapter = new TimeSlotAdapter(defaultSlots);
                timeSlotAdapter.setOnItemClickListener(slot -> {
                    selectedTimeSlotObject = slot;
                    selectedTimeSlot = slot.getTimeSlot();
                    Log.d("BookingActivity", "Selected Time (fallback): " + selectedTimeSlot + ", Price: " + slot.getPrice());
                });
                recyclerViewTimeSlots.setAdapter(timeSlotAdapter);
            }
        });
    }

    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int i = 6; i <= 21; i++) {
            slots.add(String.format(Locale.getDefault(), "%02d:00", i));
        }
        return slots;
    }
}