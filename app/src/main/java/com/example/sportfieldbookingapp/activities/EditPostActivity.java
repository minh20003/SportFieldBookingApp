package com.example.sportfieldbookingapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

public class EditPostActivity extends AppCompatActivity {

    private EditText etSportType, etPlayersNeeded, etDescription;
    private Button btnSelectDate, btnSelectTime, btnUpdatePost;
    private TextView tvSelectedDateTime;
    private ApiService apiService;
    private TeammatePost postToEdit;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        // Ánh xạ views
        etSportType = findViewById(R.id.etSportType);
        etPlayersNeeded = findViewById(R.id.etPlayersNeeded);
        etDescription = findViewById(R.id.etDescription);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnUpdatePost = findViewById(R.id.btnUpdatePost);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Nhận đối tượng Post từ Intent (cách làm mới, không bị deprecated)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            postToEdit = getIntent().getSerializableExtra("POST_TO_EDIT", TeammatePost.class);
        } else {
            postToEdit = (TeammatePost) getIntent().getSerializableExtra("POST_TO_EDIT");
        }


        if (postToEdit != null) {
            populateData(postToEdit);
        } else {
            Toast.makeText(this, "Lỗi: không có dữ liệu tin đăng", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Cài đặt sự kiện click
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnUpdatePost.setOnClickListener(v -> submitUpdate());
    }

    // Đổ dữ liệu cũ vào form
    private void populateData(TeammatePost post) {
        etSportType.setText(post.getSportType());
        etPlayersNeeded.setText(String.valueOf(post.getPlayersNeeded()));
        etDescription.setText(post.getDescription());
        selectedDate = post.getPlayDate();
        selectedTime = post.getTimeSlot();
        updateSelectedDateTimeText();
    }

    private void submitUpdate() {
        // Lấy dữ liệu mới từ form
        String sportType = etSportType.getText().toString().trim();
        String playersNeededStr = etPlayersNeeded.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Thêm kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(sportType) || TextUtils.isEmpty(playersNeededStr) || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }
        int playersNeeded = Integer.parseInt(playersNeededStr);

        // Cập nhật lại đối tượng postToEdit
        postToEdit.setSportType(sportType);
        postToEdit.setPlayersNeeded(playersNeeded);
        postToEdit.setDescription(description);
        postToEdit.setPlayDate(selectedDate);
        postToEdit.setTimeSlot(selectedTime);

        // Lấy token
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) { return; }
        String authToken = "Bearer " + token;

        // Gọi API
        Call<GenericResponse> call = apiService.updateTeammatePost(authToken, postToEdit);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditPostActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình
                } else {
                    Toast.makeText(EditPostActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        // ... code hàm này giữ nguyên ...
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            updateSelectedDateTimeText();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        // ... code hàm này giữ nguyên ...
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute);
            updateSelectedDateTimeText();
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void updateSelectedDateTimeText() {
        tvSelectedDateTime.setText("Thời gian đã chọn: " + selectedDate + " " + selectedTime);
    }
}