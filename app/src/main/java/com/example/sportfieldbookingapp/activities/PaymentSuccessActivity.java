package com.example.sportfieldbookingapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.BookingDetail;
import java.text.NumberFormat;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView tvBookingId, tvFieldName, tvBookingDate, tvTimeSlot;
    private TextView tvPaymentMethod, tvTransactionId, tvTotalAmount;
    private Button btnViewBookings, btnBackToHome;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize views
        initializeViews();

        // Get data from intent
        String returnUrl = getIntent().getStringExtra("RETURN_URL");

        // Parse URL parameters
        if (returnUrl != null) {
            parseUrlParameters(returnUrl);
        }

        // Set click listeners
        btnViewBookings.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentSuccessActivity.this, HomeActivity.class);
            intent.putExtra("NAVIGATE_TO_BOOKINGS", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initializeViews() {
        tvBookingId = findViewById(R.id.tvBookingId);
        tvFieldName = findViewById(R.id.tvFieldName);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvTimeSlot = findViewById(R.id.tvTimeSlot);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnBackToHome = findViewById(R.id.btnBackToHome);
    }

    private void parseUrlParameters(String url) {
        try {
            Uri uri = Uri.parse(url);

            // Get VNPay transaction parameters
            String vnpTxnRef = uri.getQueryParameter("vnp_TxnRef");  // This is booking_id
            String amountStr = uri.getQueryParameter("vnp_Amount");
            String transactionNo = uri.getQueryParameter("vnp_TransactionNo");
            String paymentMethod = uri.getQueryParameter("vnp_BankCode");

            // Set booking ID from vnp_TxnRef
            if (vnpTxnRef != null && !vnpTxnRef.isEmpty()) {
                tvBookingId.setText("#" + vnpTxnRef);
                // ⭐ Fetch booking details from API
                fetchBookingDetails(vnpTxnRef);
            }

            // Set payment method
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                tvPaymentMethod.setText("VNPay (" + paymentMethod + ")");
            } else {
                tvPaymentMethod.setText("VNPay");
            }

            // Set transaction ID
            if (transactionNo != null && !transactionNo.isEmpty()) {
                tvTransactionId.setText(transactionNo);
            }

            // Set total amount
            if (amountStr != null && !amountStr.isEmpty()) {
                try {
                    // VNPay returns amount in smallest unit (multiply by 100)
                    long amount = Long.parseLong(amountStr) / 100;
                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    tvTotalAmount.setText(formatter.format(amount) + "đ");
                } catch (NumberFormatException e) {
                    tvTotalAmount.setText(amountStr + "đ");
                }
            }

        } catch (Exception e) {
            Log.e("PaymentSuccess", "Error parsing URL", e);
        }
    }

    // ⭐⭐⭐ IMPLEMENT API CALL ĐỂ LẤY THÔNG TIN ĐẦY ĐỦ
    private void fetchBookingDetails(String bookingIdStr) {
        try {
            int bookingId = Integer.parseInt(bookingIdStr);

            // Hiển thị loading
            tvFieldName.setText("Đang tải...");
            tvBookingDate.setText("--");
            tvTimeSlot.setText("--");

            Log.d("PaymentSuccess", "Fetching booking details for ID: " + bookingId);

            // Gọi API
            Call<BookingDetail> call = apiService.getBookingDetail(bookingId);
            call.enqueue(new Callback<BookingDetail>() {
                @Override
                public void onResponse(Call<BookingDetail> call, Response<BookingDetail> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BookingDetail booking = response.body();

                        // ⭐ Hiển thị thông tin chi tiết
                        tvFieldName.setText(booking.getFieldName());
                        tvBookingDate.setText(booking.getBookingDate());

                        // Format giờ: 08:00 - 09:00
                        String timeSlot = booking.getTimeSlotStart() + " - " + booking.getTimeSlotEnd();
                        tvTimeSlot.setText(timeSlot);

                        // Format giá (nếu chưa có từ VNPay response)
                        if (tvTotalAmount.getText().toString().equals("200,000đ") ||
                                tvTotalAmount.getText().toString().isEmpty()) {
                            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                            tvTotalAmount.setText(formatter.format(booking.getTotalPrice()) + "đ");
                        }

                        Log.d("PaymentSuccess", "Booking details loaded: " + booking.getFieldName());

                    } else {
                        Log.e("PaymentSuccess", "Response not successful: " + response.code());
                        // Hiển thị thông tin cơ bản từ VNPay
                        tvFieldName.setText("Đặt sân thành công");
                        tvBookingDate.setText("--");
                        tvTimeSlot.setText("--");
                        Toast.makeText(PaymentSuccessActivity.this,
                                "Không thể tải chi tiết đơn đặt",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BookingDetail> call, Throwable t) {
                    Log.e("PaymentSuccess", "API call failed", t);
                    // Hiển thị thông tin cơ bản
                    tvFieldName.setText("Đặt sân thành công");
                    tvBookingDate.setText("--");
                    tvTimeSlot.setText("--");
                    Toast.makeText(PaymentSuccessActivity.this,
                            "Lỗi kết nối. Vui lòng xem chi tiết trong 'Đơn đặt của tôi'",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Log.e("PaymentSuccess", "Invalid booking ID format", e);
            tvFieldName.setText("Đặt sân thành công");
            tvBookingDate.setText("--");
            tvTimeSlot.setText("--");
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent back button, redirect to home
        Intent intent = new Intent(PaymentSuccessActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}