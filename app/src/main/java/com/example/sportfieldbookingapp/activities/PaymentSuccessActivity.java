package com.example.sportfieldbookingapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sportfieldbookingapp.R;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView tvBookingId, tvFieldName, tvBookingDate, tvTimeSlot;
    private TextView tvPaymentMethod, tvTransactionId, tvTotalAmount;
    private Button btnViewBookings, btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

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
                // Fetch booking details from API
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
            e.printStackTrace();
        }
    }
    
    private void fetchBookingDetails(String bookingId) {
        // For now, use static data. You can implement API call later if needed
        // The booking details are already updated in the database by vnpay_return.php
        // You could make an API call here to get the full booking details
        
        // Placeholder - you can enhance this with actual API call
        tvFieldName.setText("Đang tải...");
        tvBookingDate.setText("--");
        tvTimeSlot.setText("--");
        
        // TODO: Implement API call to fetch booking details
        // For now, the user can view details in "My Bookings" section
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

