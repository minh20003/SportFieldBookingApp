package com.example.sportfieldbookingapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.TeammatePost;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etSportType, etPlayersNeeded, etDescription;
    private Button btnSelectDate, btnSelectTime, btnSubmitPost;
    private TextView tvSelectedDateTime;
    private ApiService apiService;
    private Toolbar toolbar;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Setup Toolbar with back button
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ views
        etSportType = findViewById(R.id.etSportType);
        etPlayersNeeded = findViewById(R.id.etPlayersNeeded);
        etDescription = findViewById(R.id.etDescription);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Cài đặt sự kiện click
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSubmitPost.setOnClickListener(v -> submitPost());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            updateSelectedDateTimeText();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute);
            updateSelectedDateTimeText();
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void updateSelectedDateTimeText() {
        tvSelectedDateTime.setText("Thời gian đã chọn: " + selectedDate + " " + selectedTime);
    }

    private void submitPost() {
        String sportType = etSportType.getText().toString().trim();
        String playersNeededStr = etPlayersNeeded.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(sportType) || TextUtils.isEmpty(playersNeededStr) || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        int playersNeeded = Integer.parseInt(playersNeededStr);

        // Lấy token
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            Toast.makeText(this, "Lỗi xác thực, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        String authToken = "Bearer " + token;

        // Tạo đối tượng và gọi API
        TeammatePost newPost = new TeammatePost(sportType, selectedDate, selectedTime, playersNeeded, description);
        Call<GenericResponse> call = apiService.createTeammatePost(authToken, newPost);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreatePostActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    finish(); // Đóng màn hình sau khi đăng thành công
                } else {
                    Toast.makeText(CreatePostActivity.this, "Đăng tin thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}