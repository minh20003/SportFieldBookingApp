package com.example.sportfieldbookingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sportfieldbookingapp.R;

public class PaymentWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Lắng nghe khi URL trả về chứa "vnpay_return"
                if (url.contains("vnpay_return.php")) {
                    // Dựa vào các tham số trên URL để biết thành công hay thất bại
                    if (url.contains("vnp_ResponseCode=00")) {
                        Toast.makeText(PaymentWebViewActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PaymentWebViewActivity.this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
                    }
                    // Đóng màn hình thanh toán và quay về
                    finish();
                }
            }
        });

        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        } else {
            Toast.makeText(this, "Lỗi: Không có URL thanh toán", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}