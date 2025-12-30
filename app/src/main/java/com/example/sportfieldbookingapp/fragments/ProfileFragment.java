package com.example.sportfieldbookingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.activities.EditProfileActivity;
import com.example.sportfieldbookingapp.activities.MainActivity;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvPhone, tvEmail;
    private LinearLayout btnLogout, menuEditProfile, menuHelp, menuAbout;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ views
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        menuEditProfile = view.findViewById(R.id.menuEditProfile);
        menuHelp = view.findViewById(R.id.menuHelp);
        menuAbout = view.findViewById(R.id.menuAbout);

        // Khởi tạo API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Load thông tin từ SharedPreferences trước
        loadBasicInfo();

        // Load thông tin chi tiết từ API
        loadUserProfile();

        // Setup click listeners
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBasicInfo();
        loadUserProfile();
    }

    private void loadBasicInfo() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("USER_NAME", "Người dùng");
        tvProfileName.setText(userName);
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);

        if (token == null) return;

        String authToken = "Bearer " + token;

        Call<User> call = apiService.getUserProfile(authToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    User user = response.body();
                    
                    tvProfileName.setText(user.getFullName() != null ? user.getFullName() : "Người dùng");
                    tvProfileEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "Chưa cập nhật");
                    
                    String phone = user.getPhone();
                    if (phone != null && !phone.isEmpty()) {
                        tvPhone.setText(phone);
                    } else {
                        tvPhone.setText("Chưa cập nhật");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // Silent fail
            }
        });
    }

    private void setupClickListeners() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        menuEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        menuHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Liên hệ: support@sportbooking.vn", Toast.LENGTH_LONG).show();
        });

        menuAbout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sport Field Booking App v1.0.0", Toast.LENGTH_LONG).show();
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                getActivity().finish();
            }
        });
    }
}
