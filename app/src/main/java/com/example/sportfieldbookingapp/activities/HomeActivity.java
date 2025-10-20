package com.example.sportfieldbookingapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.SportFieldAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.SportField;
import com.example.sportfieldbookingapp.models.SportFieldResponse; // Chúng ta sẽ cần tạo model này

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFields;
    private SportFieldAdapter sportFieldAdapter;
    private List<SportField> fieldList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo RecyclerView
        recyclerViewFields = findViewById(R.id.recyclerViewFields);
        recyclerViewFields.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter
        sportFieldAdapter = new SportFieldAdapter(this, fieldList);
        recyclerViewFields.setAdapter(sportFieldAdapter);

        // Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Gọi hàm để lấy dữ liệu
        fetchSportFields();
    }

    private void fetchSportFields() {
        Call<SportFieldResponse> call = apiService.getAllFields();
        call.enqueue(new Callback<SportFieldResponse>() {
            @Override
            public void onResponse(Call<SportFieldResponse> call, Response<SportFieldResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Xóa dữ liệu cũ và thêm dữ liệu mới từ API
                    fieldList.clear();
                    fieldList.addAll(response.body().getRecords());
                    sportFieldAdapter.notifyDataSetChanged(); // Báo cho adapter biết dữ liệu đã thay đổi
                } else {
                    Toast.makeText(HomeActivity.this, "Không thể tải dữ liệu sân.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SportFieldResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HOME_ACTIVITY_ERROR", "onFailure: " + t.getMessage());
            }
        });
    }
}