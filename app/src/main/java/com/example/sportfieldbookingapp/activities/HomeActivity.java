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
import android.content.Intent;
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

        // 1. Ánh xạ và cài đặt RecyclerView
        recyclerViewFields = findViewById(R.id.recyclerViewFields);
        recyclerViewFields.setLayoutManager(new LinearLayoutManager(this));

        // 2. Khởi tạo Adapter và gán cho RecyclerView
        sportFieldAdapter = new SportFieldAdapter(this, fieldList);
        recyclerViewFields.setAdapter(sportFieldAdapter);

        // 3. Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // 4. Set sự kiện click cho Adapter
        sportFieldAdapter.setOnItemClickListener(new SportFieldAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Lấy sân được click
                SportField clickedField = fieldList.get(position);

                // Tạo Intent để chuyển sang màn hình chi tiết
                Intent intent = new Intent(HomeActivity.this, FieldDetailActivity.class);

                // Gửi ID của sân được chọn sang màn hình chi tiết
                intent.putExtra("FIELD_ID", clickedField.getId());
                startActivity(intent);
            }
        });

        // 5. Gọi API để lấy dữ liệu sân
        fetchSportFields();
    }

    /**
     * Hàm này thực hiện việc gọi API để lấy danh sách các sân thể thao.
     */
    private void fetchSportFields() {
        Call<SportFieldResponse> call = apiService.getAllFields();
        call.enqueue(new Callback<SportFieldResponse>() {
            @Override
            public void onResponse(Call<SportFieldResponse> call, Response<SportFieldResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Xóa dữ liệu cũ trong danh sách
                    fieldList.clear();
                    // Thêm toàn bộ dữ liệu mới từ API vào danh sách
                    fieldList.addAll(response.body().getRecords());
                    // Báo cho Adapter biết rằng dữ liệu đã thay đổi để nó cập nhật lại giao diện
                    sportFieldAdapter.notifyDataSetChanged();
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