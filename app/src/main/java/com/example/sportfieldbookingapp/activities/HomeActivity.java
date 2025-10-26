package com.example.sportfieldbookingapp.activities; // <<< THAY BẰNG PACKAGE CỦA BẠN

import android.content.Intent; // <<< THÊM IMPORT NÀY
import android.os.Bundle;
import android.util.Log; // <<< THÊM IMPORT NÀY
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
// Thay 'com.example.sportfieldbookingapp' bằng package của bạn trong các dòng import dưới đây
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.fragments.FindTeammateFragment;
import com.example.sportfieldbookingapp.fragments.HomeFragment;
import com.example.sportfieldbookingapp.fragments.MyBookingsFragment;
import com.example.sportfieldbookingapp.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity"; // Tag cho Logcat
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: Activity created");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load fragment mặc định hoặc xử lý intent từ notification
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: savedInstanceState is null, handling initial intent");
            handleIntent(getIntent()); // Xử lý intent ban đầu khi activity được tạo
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
            } else if (itemId == R.id.nav_find_teammate) {
                selectedFragment = new FindTeammateFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true; // Quan trọng: return true để đánh dấu item đã được chọn
        });
    }

    /**
     * Được gọi khi Activity đã chạy và nhận một Intent mới (ví dụ: từ notification).
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: Received new intent");
        // Xử lý intent mới nhận được (thường là từ notification khi app đang mở)
        handleIntent(intent);
    }

    /**
     * Kiểm tra Intent đến và điều hướng đến Fragment phù hợp.
     */
    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("NAVIGATE_TO")) {
            String navigateTo = intent.getStringExtra("NAVIGATE_TO");
            Log.d(TAG, "handleIntent: NAVIGATE_TO extra found: " + navigateTo);

            if ("find_teammate".equals(navigateTo)) {
                // Lấy dữ liệu đi kèm (nếu cần)
                String postId = intent.getStringExtra("POST_ID");
                Log.d(TAG, "handleIntent: Navigating to FindTeammateFragment, postId=" + postId);

                // Tạo Fragment và truyền dữ liệu (nếu cần)
                FindTeammateFragment fragment = new FindTeammateFragment();
                // Bundle args = new Bundle();
                // args.putString("POST_ID", postId);
                // fragment.setArguments(args);

                // Load Fragment và chọn tab tương ứng
                loadFragment(fragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_find_teammate);

            } else if ("my_bookings".equals(navigateTo)) {
                Log.d(TAG, "handleIntent: Navigating to MyBookingsFragment");
                loadFragment(new MyBookingsFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_my_bookings);
                // Lấy bookingId nếu cần: String bookingId = intent.getStringExtra("BOOKING_ID");
            }
            // TODO: Thêm các else if cho các loại thông báo khác
            else {
                // Nếu NAVIGATE_TO không khớp, mở HomeFragment mặc định
                Log.d(TAG, "handleIntent: NAVIGATE_TO not recognized, loading HomeFragment");
                loadFragment(new HomeFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
            // Xóa dữ liệu extra để tránh xử lý lại khi xoay màn hình
            intent.removeExtra("NAVIGATE_TO");

        } else {
            Log.d(TAG, "handleIntent: No NAVIGATE_TO extra found");
            // Nếu không có yêu cầu điều hướng (mở app bình thường hoặc không có extra)
            // Chỉ load HomeFragment mặc định nếu chưa có fragment nào được load
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                Log.d(TAG, "handleIntent: Loading default HomeFragment");
                loadFragment(new HomeFragment());
                // Đảm bảo tab home được chọn
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    /**
     * Hàm để thay thế fragment hiện tại trong FrameLayout.
     */
    private void loadFragment(Fragment fragment) {
        Log.d(TAG, "loadFragment: Loading " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // fragmentTransaction.addToBackStack(null); // Không nên thêm vào back stack cho các tab chính
        fragmentTransaction.commit();
    }
}