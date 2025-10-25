package com.example.sportfieldbookingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
    private ApiService apiService;

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
        recyclerViewFields.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Khởi tạo Adapter và gán cho RecyclerView
        // Lưu ý: Fragment không có SharedPreferences, nên chúng ta tạm bỏ qua currentUserId
        sportFieldAdapter = new SportFieldAdapter(getContext(), fieldList);
        recyclerViewFields.setAdapter(sportFieldAdapter);

        // 3. Xử lý sự kiện click item
        sportFieldAdapter.setOnItemClickListener(position -> {
            SportField clickedField = fieldList.get(position);
            Intent intent = new Intent(getActivity(), FieldDetailActivity.class);
            intent.putExtra("FIELD_ID", clickedField.getId());
            startActivity(intent);
        });

        // 4. Khởi tạo ApiService và lấy dữ liệu
        apiService = ApiClient.getClient().create(ApiService.class);
        fetchSportFields();
    }

    private void fetchSportFields() {
        Call<SportFieldResponse> call = apiService.getAllFields();
        call.enqueue(new Callback<SportFieldResponse>() {
            @Override
            public void onResponse(@NonNull Call<SportFieldResponse> call, @NonNull Response<SportFieldResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldList.clear();
                    fieldList.addAll(response.body().getRecords());
                    sportFieldAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải dữ liệu sân.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SportFieldResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}