package com.example.sportfieldbookingapp.fragments;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.activities.TeammatePostDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.activities.CreatePostActivity;
import com.example.sportfieldbookingapp.activities.EditPostActivity;
import com.example.sportfieldbookingapp.adapters.TeammatePostAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.DeletePostRequest;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.JoinPostRequest;
import com.example.sportfieldbookingapp.models.TeammatePost;
import com.example.sportfieldbookingapp.models.TeammatePostResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;
import java.io.IOException;
public class FindTeammateFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private TeammatePostAdapter adapter;
    private List<TeammatePost> postList = new ArrayList<>();
    private ApiService apiService;
    private FloatingActionButton fabCreatePost;
    private int currentUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_find_teammate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy ID người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("USER_ID", -1);

        // Ánh xạ Views
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        fabCreatePost = view.findViewById(R.id.fabCreatePost);

        // Cài đặt RecyclerView
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeammatePostAdapter(postList, currentUserId);
        recyclerViewPosts.setAdapter(adapter);

        // Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cài đặt các sự kiện click
        setClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi fragment này được hiển thị
        // để cập nhật danh sách sau khi thêm/sửa/xóa
        fetchPosts();
    }

    private void setClickListeners() {
        // Nút (+) để tạo tin mới
        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });

        // Nút "Tham gia" trong adapter
        adapter.setOnJoinButtonClickListener(this::joinPost);

        // Nút "Xóa" trong adapter
        adapter.setOnDeleteButtonClickListener(this::showDeleteConfirmationDialog);

        // Nút "Sửa" trong adapter
        adapter.setOnEditButtonClickListener(post -> {
            Intent intent = new Intent(getActivity(), EditPostActivity.class);
            intent.putExtra("POST_TO_EDIT", post);
            startActivity(intent);
        });
        adapter.setOnItemClickListener(post -> {
            Log.d(TAG, "Item clicked: " + post.getId());
            Intent intent = new Intent(getActivity(), TeammatePostDetailActivity.class);
            // Gửi toàn bộ đối tượng Post sang màn hình chi tiết
            intent.putExtra("POST_DETAIL", post);
            startActivity(intent);
        });
    }

    private void fetchPosts() {
        Call<TeammatePostResponse> call = apiService.getTeammatePosts();
        call.enqueue(new Callback<TeammatePostResponse>() {
            @Override
            public void onResponse(@NonNull Call<TeammatePostResponse> call, @NonNull Response<TeammatePostResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body().getRecords());
                    adapter.notifyDataSetChanged();
                } else if (isAdded()) {
                    postList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(@NonNull Call<TeammatePostResponse> call, @NonNull Throwable t) {
                if(isAdded()){
                    Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void joinPost(TeammatePost post) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để tham gia", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + token;
        JoinPostRequest request = new JoinPostRequest(post.getId());

        Call<GenericResponse> call = apiService.joinTeammatePost(authToken, request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (isAdded()) { // Kiểm tra xem fragment còn được gắn vào activity không
                    if (response.isSuccessful() && response.body() != null) {
                        // Trường hợp thành công (server trả về mã 2xx)
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Trường hợp thất bại (server trả về mã 4xx, 5xx)
                        // Đọc và hiển thị lỗi cụ thể từ server
                        String errorMessage = "Tham gia thất bại."; // Mặc định
                        if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson();
                                GenericResponse errorResponse = gson.fromJson(response.errorBody().string(), GenericResponse.class);
                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMessage = errorResponse.getMessage();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showDeleteConfirmationDialog(TeammatePost post, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tin này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deletePost(post, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deletePost(TeammatePost post, int position) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) { return; }

        String authToken = "Bearer " + token;
        DeletePostRequest request = new DeletePostRequest(post.getId());

        Call<GenericResponse> call = apiService.deleteTeammatePost(authToken, request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (isAdded() && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Xóa tin thành công", Toast.LENGTH_SHORT).show();
                    postList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, postList.size());
                } else if(isAdded()) {
                    Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                if(isAdded()){
                    Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}