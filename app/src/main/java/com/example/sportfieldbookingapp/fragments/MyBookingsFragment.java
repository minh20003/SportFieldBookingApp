package com.example.sportfieldbookingapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.BookingAdapter;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.Booking;
import com.example.sportfieldbookingapp.models.BookingResponse;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.Review;
import com.example.sportfieldbookingapp.models.CancelBookingRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingsFragment extends Fragment {
    private static final String TAG = "MyBookingsFragment";
    private RecyclerView recyclerViewMyBookings;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewMyBookings = view.findViewById(R.id.recyclerViewMyBookings);
        recyclerViewMyBookings.setLayoutManager(new LinearLayoutManager(getContext()));

        bookingAdapter = new BookingAdapter(bookingList);
        recyclerViewMyBookings.setAdapter(bookingAdapter);

        bookingAdapter.setOnReviewButtonClickListener(booking -> showReviewDialog(booking));
        bookingAdapter.setOnCancelButtonClickListener((booking, position) -> showCancelConfirmationDialog(booking, position));
        apiService = ApiClient.getClient().create(ApiService.class);

        fetchMyBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại danh sách khi quay lại Fragment này
        fetchMyBookings();
    }

    private void fetchMyBookings() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);

        if (token == null) {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để xem lịch sử", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + token;
        Call<BookingResponse> call = apiService.getMyBookings(authToken);

        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookingList.clear();
                    bookingList.addAll(response.body().getRecords());
                    bookingAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không có đơn đặt sân nào.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Các hàm showReviewDialog và postReview được copy y hệt từ MyBookingsActivity
    // chỉ thay 'this' bằng 'getContext()' hoặc 'getActivity()'
    private void showReviewDialog(final Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_post_review, null);
        builder.setView(dialogView);

        final RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        final EditText etComment = dialogView.findViewById(R.id.etComment);

        builder.setTitle("Gửi đánh giá")
                .setPositiveButton("Gửi", (dialog, id) -> {
                    float rating = ratingBar.getRating();
                    String comment = etComment.getText().toString().trim();
                    if (rating > 0) {
                        postReview(booking.getId(), (int) rating, comment);
                    } else {
                        Toast.makeText(getContext(), "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void postReview(int bookingId, int rating, String comment) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            return;
        }

        String authToken = "Bearer " + token;
        Review review = new Review(bookingId, rating, comment);

        Call<GenericResponse> call = apiService.postReview(authToken, review);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gửi đánh giá thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelConfirmationDialog(Booking booking, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận hủy đơn")
                .setMessage("Bạn có chắc chắn muốn hủy đơn đặt sân này không?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelBooking(booking, position))
                .setNegativeButton("Không", null)
                .show();
    }

    // <<-- THÊM HÀM MỚI ĐỂ GỌI API HỦY -->>
    private void cancelBooking(Booking booking, int position) {
        Log.d(TAG, "Attempting to cancel booking ID: " + booking.getId());
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            Toast.makeText(getContext(), "Lỗi xác thực, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + token;
        CancelBookingRequest request = new CancelBookingRequest(booking.getId());

        Call<GenericResponse> call = apiService.cancelBooking(authToken, request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (isAdded()) { // Kiểm tra Fragment còn tồn tại
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Booking cancelled successfully, ID: " + booking.getId());
                        Toast.makeText(getContext(), "Hủy đơn thành công", Toast.LENGTH_SHORT).show();

                        // Cập nhật trạng thái đơn hàng trong danh sách ngay lập tức
                        booking.setStatus("cancelled"); // <<< Cần thêm hàm setStatus vào Booking.java
                        bookingAdapter.notifyItemChanged(position);

                        // Hoặc tải lại toàn bộ danh sách
                        // fetchMyBookings();
                    } else {
                        String errorMessage = "Hủy đơn thất bại. Có thể đơn đã qua trạng thái cho phép hủy.";
                        try {
                            if (response.errorBody() != null) {
                                Gson gson = new Gson();
                                GenericResponse errorResponse = gson.fromJson(response.errorBody().string(), GenericResponse.class);
                                if (errorResponse != null && errorResponse.getMessage() != null) {
                                    errorMessage = errorResponse.getMessage();
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                        Log.e(TAG, "Failed to cancel booking ID: " + booking.getId() + ", Error: " + errorMessage);
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Log.e(TAG, "Error cancelling booking ID: " + booking.getId(), t);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}