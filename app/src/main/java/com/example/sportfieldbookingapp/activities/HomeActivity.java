package com.example.sportfieldbookingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.fragments.FindTeammateFragment;
import com.example.sportfieldbookingapp.fragments.HomeFragment;
import com.example.sportfieldbookingapp.fragments.MyBookingsFragment;
import com.example.sportfieldbookingapp.fragments.MessagesFragment;
import com.example.sportfieldbookingapp.fragments.ProfileFragment;
import com.example.sportfieldbookingapp.models.NotificationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;

    // ============================================
    // THÊM CÁC BIẾN MỚI CHO NOTIFICATION BADGE
    // ============================================
    private TextView tvBadge;
    private int unreadCount = 0;
    private Handler handler = new Handler();
    private static final long CHECK_INTERVAL = 30000; // 30 seconds
    private ApiService apiService;

    private Runnable checkNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            loadUnreadCount();
            handler.postDelayed(this, CHECK_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: Activity created");

        // Setup toolbar to show menu (notifications)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load fragment mặc định hoặc xử lý intent từ notification
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: savedInstanceState is null, handling initial intent");
            handleIntent(getIntent());
        }

        // Xử lý sự kiện khi chọn một mục
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            Log.d(TAG, "BottomNav selected: " + item.getTitle());

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_my_bookings) {
                selectedFragment = new MyBookingsFragment();
            } else if (itemId == R.id.nav_messages) {
                selectedFragment = new MessagesFragment();
            } else if (itemId == R.id.nav_find_teammate) {
                selectedFragment = new FindTeammateFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    // ============================================
    // THÊM CODE MỚI: NOTIFICATION ICON VỚI BADGE
    // ============================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        // Lấy notification icon
        MenuItem notificationItem = menu.findItem(R.id.action_notifications);
        View actionView = notificationItem.getActionView();

        if (actionView != null) {
            // Setup badge
            tvBadge = actionView.findViewById(R.id.tvBadge);

            // Click listener
            actionView.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });

            // Load unread count ngay lập tức
            loadUnreadCount();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load số lượng thông báo chưa đọc từ API
     */
    private void loadUnreadCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("USER_TOKEN", null);

        if (authToken == null) {
            Log.w(TAG, "loadUnreadCount: No auth token found");
            return;
        }

        String token = "Bearer " + authToken;

        // Call API để lấy unread count
        apiService.getNotifications(token, 1, 1).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NotificationResponse notifResponse = response.body();
                    if (notifResponse.isSuccess() && notifResponse.getPagination() != null) {
                        unreadCount = notifResponse.getPagination().getUnreadCount();
                        Log.d(TAG, "loadUnreadCount: Unread count = " + unreadCount);
                        updateBadge();
                    }
                } else {
                    Log.w(TAG, "loadUnreadCount: Response not successful");
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.e(TAG, "loadUnreadCount error: " + t.getMessage());
            }
        });
    }

    /**
     * Cập nhật badge icon với số unread
     */
    private void updateBadge() {
        if (tvBadge != null) {
            if (unreadCount > 0) {
                tvBadge.setVisibility(View.VISIBLE);
                tvBadge.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
            } else {
                tvBadge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity resumed");
        // Load unread count khi activity resume
        loadUnreadCount();
        // Bắt đầu check định kỳ
        handler.post(checkNotificationRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity paused");
        // Dừng check định kỳ
        handler.removeCallbacks(checkNotificationRunnable);
    }

    /**
     * Được gọi khi Activity đã chạy và nhận một Intent mới (ví dụ: từ notification).
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: Received new intent");
        setIntent(intent); // Quan trọng: cập nhật intent hiện tại
        handleIntent(intent);
    }

    /**
     * Kiểm tra Intent đến và điều hướng đến Fragment phù hợp.
     */
    private void handleIntent(Intent intent) {
        // Check for NAVIGATE_TO_BOOKINGS flag from PaymentSuccessActivity
        if (intent != null && intent.getBooleanExtra("NAVIGATE_TO_BOOKINGS", false)) {
            Log.d(TAG, "handleIntent: NAVIGATE_TO_BOOKINGS flag found");
            loadFragment(new MyBookingsFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_my_bookings);
            intent.removeExtra("NAVIGATE_TO_BOOKINGS");
            return;
        }

        if (intent != null && intent.hasExtra("NAVIGATE_TO")) {
            String navigateTo = intent.getStringExtra("NAVIGATE_TO");
            Log.d(TAG, "handleIntent: NAVIGATE_TO extra found: " + navigateTo);

            if ("find_teammate".equals(navigateTo)) {
                String postId = intent.getStringExtra("POST_ID");
                Log.d(TAG, "handleIntent: Navigating to FindTeammateFragment, postId=" + postId);

                FindTeammateFragment fragment = new FindTeammateFragment();
                loadFragment(fragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_find_teammate);

            } else if ("my_bookings".equals(navigateTo)) {
                Log.d(TAG, "handleIntent: Navigating to MyBookingsFragment");
                loadFragment(new MyBookingsFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_my_bookings);
            } else if ("messages".equals(navigateTo)) {
                Log.d(TAG, "handleIntent: Navigating to MessagesFragment");
                loadFragment(new MessagesFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_messages);
            } else {
                Log.d(TAG, "handleIntent: NAVIGATE_TO not recognized, loading HomeFragment");
                loadFragment(new HomeFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
            intent.removeExtra("NAVIGATE_TO");

        } else {
            Log.d(TAG, "handleIntent: No NAVIGATE_TO extra found");
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                Log.d(TAG, "handleIntent: Loading default HomeFragment");
                loadFragment(new HomeFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    /**
     * Hàm để thay thế fragment hiện tại trong FrameLayout với animation mượt mà.
     */
    private void loadFragment(Fragment fragment) {
        Log.d(TAG, "loadFragment: Loading " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Thêm fade animation cho fragment transitions
        fragmentTransaction.setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
        );

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}