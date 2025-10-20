package com.example.sportfieldbookingapp.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.TeammatePostAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.TeammatePost;
import com.example.sportfieldbookingapp.models.TeammatePostResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTeammateActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosts;
    private TeammatePostAdapter adapter;
    private List<TeammatePost> postList = new ArrayList<>();
    private ApiService apiService;
    private FloatingActionButton fabCreatePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_teammate);

        // 1. Ánh xạ Views
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        fabCreatePost = findViewById(R.id.fabCreatePost);

        // 2. Cài đặt RecyclerView
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeammatePostAdapter(postList);
        recyclerViewPosts.setAdapter(adapter);

        // 3. Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // 4. Cài đặt sự kiện click cho nút FAB
        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(FindTeammateActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        // 5. Lấy dữ liệu từ API
        fetchPosts();
    }

    /**
     * Hàm gọi API để lấy danh sách các tin tìm người.
     */
    private void fetchPosts() {
        Call<TeammatePostResponse> call = apiService.getTeammatePosts();
        call.enqueue(new Callback<TeammatePostResponse>() {
            @Override
            public void onResponse(Call<TeammatePostResponse> call, Response<TeammatePostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body().getRecords());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FindTeammateActivity.this, "Chưa có tin tìm người nào.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeammatePostResponse> call, Throwable t) {
                Toast.makeText(FindTeammateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}