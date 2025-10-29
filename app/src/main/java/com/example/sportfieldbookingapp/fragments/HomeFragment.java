package com.example.sportfieldbookingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.activities.FieldDetailActivity;
import com.example.sportfieldbookingapp.adapters.SportFieldAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.SportField;
import com.example.sportfieldbookingapp.models.SportFieldResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewFields;
    private SportFieldAdapter sportFieldAdapter;
    private List<SportField> fieldList = new ArrayList<>();
    private List<SportField> filteredList = new ArrayList<>();
    private ApiService apiService;
    private TextInputEditText etSearch;
    private ProgressBar progressBar;
    private Handler searchHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View từ layout của fragment
        recyclerViewFields = view.findViewById(R.id.recyclerViewFields);
        etSearch = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        
        recyclerViewFields.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Thêm animation cho RecyclerView
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
            getContext(), R.anim.layout_animation_fall_down);
        recyclerViewFields.setLayoutAnimation(animation);

        // 2. Khởi tạo Adapter và gán cho RecyclerView
        sportFieldAdapter = new SportFieldAdapter(getContext(), filteredList);
        recyclerViewFields.setAdapter(sportFieldAdapter);

        // 3. Xử lý sự kiện click item với animation
        sportFieldAdapter.setOnItemClickListener(position -> {
            SportField clickedField = filteredList.get(position);
            Intent intent = new Intent(getActivity(), FieldDetailActivity.class);
            intent.putExtra("FIELD_ID", clickedField.getId());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // 4. Setup search functionality
        setupSearch();

        // 5. Khởi tạo ApiService và lấy dữ liệu
        apiService = ApiClient.getClient().create(ApiService.class);
        fetchSportFields();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Debounce search để tránh tìm kiếm quá nhiều lần
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() -> filterFields(s.toString()), 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterFields(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(fieldList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (SportField field : fieldList) {
                if (field.getName().toLowerCase().contains(lowerQuery) ||
                    field.getAddress().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(field);
                }
            }
        }
        sportFieldAdapter.notifyDataSetChanged();
        recyclerViewFields.scheduleLayoutAnimation();
    }

    private void fetchSportFields() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        Call<SportFieldResponse> call = apiService.getAllFields();
        call.enqueue(new Callback<SportFieldResponse>() {
            @Override
            public void onResponse(@NonNull Call<SportFieldResponse> call, @NonNull Response<SportFieldResponse> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    fieldList.clear();
                    fieldList.addAll(response.body().getRecords());
                    filteredList.clear();
                    filteredList.addAll(fieldList);
                    sportFieldAdapter.notifyDataSetChanged();
                    recyclerViewFields.scheduleLayoutAnimation();
                } else {
                    Toast.makeText(getContext(), "Không thể tải dữ liệu sân.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SportFieldResponse> call, @NonNull Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}