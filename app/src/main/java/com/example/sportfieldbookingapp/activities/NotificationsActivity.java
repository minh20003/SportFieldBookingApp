// ================================================
// NOTIFICATIONS ACTIVITY
// File: app/src/main/java/com/example/sportfieldbookingapp/activities/NotificationsActivity.java
// ================================================

package com.example.sportfieldbookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.NotificationAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.Notification;
import com.example.sportfieldbookingapp.models.NotificationResponse;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.MarkNotificationRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmpty;
    private Toolbar toolbar;

    private ApiService apiService;
    private String token;
    private int currentPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadNotifications();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvEmpty = findViewById(R.id.tvEmpty);
        toolbar = findViewById(R.id.toolbar);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("USER_TOKEN", null);
        token = "Bearer " + authToken;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            notificationList.clear();
            loadNotifications();
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Thông báo");
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
        adapter = new NotificationAdapter(this, notificationList, notification -> handleNotificationClick(notification));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        loadNotifications();
                    }
                }
            }
        });
    }

    private void loadNotifications() {
        if (isLoading) return;
        isLoading = true;

        if (currentPage == 1) {
            progressBar.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        apiService.getNotifications(token, currentPage, 20).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    NotificationResponse notifResponse = response.body();
                    if (notifResponse.isSuccess()) {
                        List<Notification> newList = notifResponse.getData();
                        if (newList != null && !newList.isEmpty()) {
                            notificationList.addAll(newList);
                            adapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(View.GONE);
                        } else if (notificationList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNotificationClick(Notification notification) {
        // Đánh dấu đã đọc
        if (!notification.isRead()) {
            markAsRead(notification.getId());
            notification.setRead(true);
            adapter.notifyDataSetChanged();
        }

        // Navigate dựa vào type
        try {
            String dataString = notification.getDataAsString();
            if (dataString != null && !dataString.isEmpty()) {
                JSONObject data = new JSONObject(dataString);

                if ("teammate_join".equals(notification.getType())) {
                    int postId = data.optInt("post_id", 0);
                    if (postId > 0) {
                        Intent intent = new Intent(this, TeammatePostDetailActivity.class);
                        intent.putExtra("post_id", postId);
                        startActivity(intent);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    private void markAsRead(int notificationId) {
        MarkNotificationRequest request = new MarkNotificationRequest(notificationId);
        apiService.markNotificationRead(token, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                // Silent success
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                // Silent fail
            }
        });
    }
}