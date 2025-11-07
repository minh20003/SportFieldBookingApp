package com.example.sportfieldbookingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.activities.ChatActivity;
import com.example.sportfieldbookingapp.adapters.ChatRoomAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.ChatRoom;
import com.example.sportfieldbookingapp.models.ChatRoomListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment {

    private static final String TAG = "MessagesFragment";
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private ChatRoomAdapter adapter;
    private final List<ChatRoom> chatRooms = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewChatRooms);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatRoomAdapter(getContext(), chatRooms, chatRoom -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("room_id", chatRoom.getRoomId());
            intent.putExtra("other_user_name", chatRoom.getOtherUserName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> loadChatRooms(false));

        apiService = ApiClient.getClient().create(ApiService.class);

        loadChatRooms(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Chỉ reload nếu đã có data (không phải lần đầu)
        if (!chatRooms.isEmpty()) {
            loadChatRooms(false);
        }
    }

    private void loadChatRooms(boolean showLoading) {
        if (!isAdded()) {
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String authToken = prefs.getString("USER_TOKEN", null);

        if (authToken == null || authToken.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Vui lòng đăng nhập để xem tin nhắn");
            chatRooms.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        if (showLoading) {
            progressBar.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        String token = "Bearer " + authToken;

        Log.d(TAG, "Loading chat rooms with token: Bearer ***");
        
        apiService.getChatRooms(token).enqueue(new Callback<ChatRoomListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatRoomListResponse> call, @NonNull Response<ChatRoomListResponse> response) {
                if (!isAdded()) {
                    return;
                }

                Log.d(TAG, "Response code: " + response.code());
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response successful: " + response.body().isSuccess());
                    if (response.body().isSuccess()) {
                        List<ChatRoom> rooms = response.body().getData();
                        chatRooms.clear();
                        if (rooms != null) {
                            chatRooms.addAll(rooms);
                            Log.d(TAG, "Loaded " + rooms.size() + " chat rooms");
                        }
                        adapter.notifyDataSetChanged();

                        tvEmpty.setVisibility(chatRooms.isEmpty() ? View.VISIBLE : View.GONE);
                        if (chatRooms.isEmpty()) {
                            tvEmpty.setText("Chưa có cuộc trò chuyện nào");
                        }
                    } else {
                        Log.w(TAG, "Response success=false");
                        tvEmpty.setVisibility(chatRooms.isEmpty() ? View.VISIBLE : View.GONE);
                        if (chatRooms.isEmpty()) {
                            tvEmpty.setText("Không thể tải danh sách cuộc trò chuyện");
                        }
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code() + " - " + response.message());
                    tvEmpty.setVisibility(chatRooms.isEmpty() ? View.VISIBLE : View.GONE);
                    if (chatRooms.isEmpty()) {
                        tvEmpty.setText("Không thể tải danh sách cuộc trò chuyện\nMã lỗi: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatRoomListResponse> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    return;
                }
                Log.e(TAG, "Request failed: " + t.getClass().getName() + " - " + t.getMessage(), t);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                
                if (chatRooms.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Lỗi kết nối: " + t.getMessage() + "\n\nKiểm tra:\n- XAMPP đang chạy\n- Apache đã start\n- API endpoint tồn tại");
                }
            }
        });
    }
}

