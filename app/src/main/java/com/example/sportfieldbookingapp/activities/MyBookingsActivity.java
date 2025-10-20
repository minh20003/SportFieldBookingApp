package com.example.sportfieldbookingapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.BookingAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.Booking;
import com.example.sportfieldbookingapp.models.BookingResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyBookings;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        recyclerViewMyBookings = findViewById(R.id.recyclerViewMyBookings);
        recyclerViewMyBookings.setLayoutManager(new LinearLayoutManager(this));

        bookingAdapter = new BookingAdapter(bookingList);
        recyclerViewMyBookings.setAdapter(bookingAdapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchMyBookings();
    }

    private void fetchMyBookings() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để xem lịch sử", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + token;
        Call<BookingResponse> call = apiService.getMyBookings(authToken);

        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookingList.clear();
                    bookingList.addAll(response.body().getRecords());
                    bookingAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyBookingsActivity.this, "Không có đơn đặt sân nào.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Toast.makeText(MyBookingsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}