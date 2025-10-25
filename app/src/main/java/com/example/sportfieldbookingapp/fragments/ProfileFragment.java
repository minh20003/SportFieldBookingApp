package com.example.sportfieldbookingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.content.Intent;
import com.example.sportfieldbookingapp.activities.EditProfileActivity;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.activities.MainActivity;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName;
    private Button btnLogout,btnEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Lấy và hiển thị tên người dùng
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("USER_NAME", "Người dùng");
        tvProfileName.setText("Xin chào, " + userName);

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            // Xóa SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Chuyển về màn hình đăng nhập và xóa các màn hình cũ
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }
}