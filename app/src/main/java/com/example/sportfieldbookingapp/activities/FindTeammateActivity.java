package com.example.sportfieldbookingapp.activities;

// Thay "yourname" bằng package name của bạn

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.TeammatePostAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.JoinPostRequest;
import com.example.sportfieldbookingapp.models.TeammatePost;
import com.example.sportfieldbookingapp.models.TeammatePostResponse;
import com.example.sportfieldbookingapp.models.DeletePostRequest;
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
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_teammate);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("USER_ID", -1);

        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        fabCreatePost = findViewById(R.id.fabCreatePost);

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeammatePostAdapter(postList, currentUserId);
        recyclerViewPosts.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(FindTeammateActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        // Set listener cho cả 3 nút
        adapter.setOnJoinButtonClickListener(this::joinPost);
        adapter.setOnDeleteButtonClickListener(this::showDeleteConfirmationDialog);
        adapter.setOnEditButtonClickListener(post -> {
            Intent intent = new Intent(FindTeammateActivity.this, EditPostActivity.class);
            intent.putExtra("POST_TO_EDIT", post);
            startActivity(intent);
        });

        fetchPosts();
    }

    // Ghi chú: Cần thêm hàm onResume() để tự động cập nhật lại danh sách
    // sau khi người dùng Sửa hoặc Thêm mới một tin đăng.
    @Override
    protected void onResume() {
        super.onResume();
        // Gọi lại fetchPosts() mỗi khi màn hình này quay trở lại foreground
        fetchPosts();
    }

    private void showDeleteConfirmationDialog(TeammatePost post, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tin này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deletePost(post, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deletePost(TeammatePost post, int position) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) { return; }

        String authToken = "Bearer " + token;
        DeletePostRequest request = new DeletePostRequest(post.getId());

        Call<GenericResponse> call = apiService.deleteTeammatePost(authToken, request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FindTeammateActivity.this, "Xóa tin thành công", Toast.LENGTH_SHORT).show();
                    postList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, postList.size());
                } else {
                    Toast.makeText(FindTeammateActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(FindTeammateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinPost(TeammatePost post) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tham gia", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + token;
        JoinPostRequest request = new JoinPostRequest(post.getId());

        Call<GenericResponse> call = apiService.joinTeammatePost(authToken, request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FindTeammateActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý các lỗi do server trả về (đã tham gia, tự tham gia,...)
                    Toast.makeText(FindTeammateActivity.this, "Không thể tham gia. Có thể bạn đã tham gia rồi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(FindTeammateActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



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