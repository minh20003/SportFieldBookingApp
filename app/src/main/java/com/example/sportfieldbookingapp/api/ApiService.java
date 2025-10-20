package com.example.sportfieldbookingapp.api;

import com.example.sportfieldbookingapp.models.LoginResponse;
import com.example.sportfieldbookingapp.models.SportField;
import com.example.sportfieldbookingapp.models.SportFieldResponse;
import com.example.sportfieldbookingapp.models.User;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import com.example.sportfieldbookingapp.models.Booking; // Thêm model cho Booking
import retrofit2.http.Header; // Thêm import cho Header
import com.example.sportfieldbookingapp.models.BookingResponse;
public interface ApiService {

    // API để đăng nhập
    // Gửi yêu cầu POST đến endpoint "auth/login.php"
    // Dữ liệu gửi đi là một đối tượng User (chứa email, password)
    // Dữ liệu trả về mong đợi là một LoginResponse
    @POST("auth/login.php")
    Call<LoginResponse> loginUser(@Body User user);

    // API để đăng kí
    @POST("auth/register.php")
    Call<LoginResponse> registerUser(@Body User user);

    // API để lấy danh sách tất cả sân
    // Gửi yêu cầu GET đến "fields/read.php"
    @GET("fields/read.php")
    Call<SportFieldResponse> getAllFields();

    // API để lấy chi tiết một sân
    // Gửi yêu cầu GET đến "fields/read_single.php" với một tham số "id"
    @GET("fields/read_single.php")
    Call<SportField> getFieldById(@Query("id") int fieldId);

    @POST("bookings/create.php")
    Call<Booking> createBooking(
            @Header("Authorization") String authToken,
            @Body Booking bookingRequest
    );
    @GET("bookings/read_my.php")
    Call<BookingResponse> getMyBookings(@Header("Authorization") String authToken);
}