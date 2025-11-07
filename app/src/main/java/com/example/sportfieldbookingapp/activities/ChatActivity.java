// ================================================
// CHAT ACTIVITY
// File: app/src/main/java/com/example/sportfieldbookingapp/activities/ChatActivity.java
// ================================================

package com.example.sportfieldbookingapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.ChatMessageAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.ChatMessage;
import com.example.sportfieldbookingapp.models.ChatMessageListResponse;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.SendMessageRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final long REFRESH_INTERVAL = 3000; // 3 seconds

    private RecyclerView recyclerView;
    private ChatMessageAdapter adapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private Toolbar toolbar;

    private ApiService apiService;
    private String token;
    private int roomId;
    private String otherUserName;

    private Handler handler = new Handler();
    private boolean isAutoRefreshing = false;

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoRefreshing) {
                loadMessages(true); // Silent refresh
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Lấy thông tin từ Intent
        roomId = getIntent().getIntExtra("room_id", 0);
        otherUserName = getIntent().getStringExtra("other_user_name");

        if (roomId == 0) {
            Toast.makeText(this, "Lỗi: Không tìm thấy phòng chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadMessages(false);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        toolbar = findViewById(R.id.toolbar);

        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("USER_TOKEN", null);
        if (authToken == null || authToken.trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem tin nhắn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        token = "Bearer " + authToken;

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(otherUserName != null ? otherUserName : "Chat");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter(this, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Scroll to bottom
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void loadMessages(boolean silent) {
        if (!silent) {
            progressBar.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        apiService.getChatMessages(token, roomId, 1, 100).enqueue(new Callback<ChatMessageListResponse>() {
            @Override
            public void onResponse(Call<ChatMessageListResponse> call, Response<ChatMessageListResponse> response) {
                if (!silent) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    ChatMessageListResponse chatResponse = response.body();
                    if (chatResponse.isSuccess()) {
                        List<ChatMessage> newMessages = chatResponse.getData();
                        if (newMessages != null && !newMessages.isEmpty()) {
                            // So sánh và chỉ thêm tin nhắn mới
                            int oldSize = messageList.size();
                            messageList.clear();
                            messageList.addAll(newMessages);
                            adapter.notifyDataSetChanged();

                            // Scroll to bottom nếu có tin nhắn mới
                            if (newMessages.size() > oldSize) {
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            }

                            tvEmpty.setVisibility(View.GONE);
                        } else if (messageList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    } else if (!silent) {
                        Toast.makeText(ChatActivity.this, "Không thể tải tin nhắn: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "loadMessages: API success=false");
                    }
                } else {
                    if (!silent) {
                        String errorMsg = "Lỗi khi tải tin nhắn";
                        if (response.errorBody() != null) {
                            try {
                                errorMsg += ": " + response.errorBody().string();
                            } catch (Exception ignored) {}
                        }
                        Toast.makeText(ChatActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "loadMessages: response not successful - " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatMessageListResponse> call, Throwable t) {
                if (!silent) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error loading messages: " + t.getMessage());
                    Toast.makeText(ChatActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable send button
        btnSend.setEnabled(false);

        SendMessageRequest request = new SendMessageRequest(roomId, message);

        apiService.sendChatMessage(token, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                btnSend.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Clear input
                        etMessage.setText("");

                        // Reload messages
                        loadMessages(true);

                        Log.d(TAG, "Message sent successfully");
                    } else {
                        Toast.makeText(ChatActivity.this, "Không thể gửi tin nhắn", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                btnSend.setEnabled(true);
                Log.e(TAG, "Error sending message: " + t.getMessage());
                Toast.makeText(ChatActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu auto-refresh
        isAutoRefreshing = true;
        handler.post(refreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dừng auto-refresh
        isAutoRefreshing = false;
        handler.removeCallbacks(refreshRunnable);
    }
}