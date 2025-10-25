package com.example.sportfieldbookingapp.api;

import com.example.sportfieldbookingapp.models.DeletePostRequest;
import com.example.sportfieldbookingapp.models.JoinPostRequest;
import com.example.sportfieldbookingapp.models.LoginResponse;
import com.example.sportfieldbookingapp.models.ReviewResponse;
import com.example.sportfieldbookingapp.models.SportField;
import com.example.sportfieldbookingapp.models.SportFieldResponse;
import com.example.sportfieldbookingapp.models.TeammatePost;
import com.example.sportfieldbookingapp.models.User;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import com.example.sportfieldbookingapp.models.Booking; // Thêm model cho Booking
import retrofit2.http.Header; // Thêm import cho Header
import com.example.sportfieldbookingapp.models.BookingResponse;
import com.example.sportfieldbookingapp.models.PaymentResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import com.example.sportfieldbookingapp.models.TeammatePostResponse;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.Review;

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
    @FormUrlEncoded
    @POST("payment/vnpay_create_payment.php")
    Call<PaymentResponse> createVnPayPayment(
            @Field("order_id") String orderId,
            @Field("amount") long amount,
            @Field("order_desc") String orderDesc,
            @Field("order_type") String orderType,
            @Field("language") String language,
            @Field("bank_code") String bankCode
    );
    @GET("teammates/read.php")
    Call<TeammatePostResponse> getTeammatePosts();
    @POST("teammates/create.php")
    Call<GenericResponse> createTeammatePost(
            @Header("Authorization") String authToken,
            @Body TeammatePost post
    );
    @POST("reviews/create.php")
    Call<GenericResponse> postReview(
            @Header("Authorization") String authToken,
            @Body Review review
    );
    @GET("reviews/read.php")
    Call<ReviewResponse> getReviewsForField(@Query("field_id") int fieldId);
    @POST("teammates/join.php")
    Call<GenericResponse> joinTeammatePost(
            @Header("Authorization") String authToken,
            @Body JoinPostRequest request
    );
    @POST("teammates/delete.php")
    Call<GenericResponse> deleteTeammatePost(
            @Header("Authorization") String authToken,
            @Body DeletePostRequest request
    );
    @POST("teammates/update.php")
    Call<GenericResponse> updateTeammatePost(
            @Header("Authorization") String authToken,
            @Body TeammatePost post
    );
    @GET("users/get_profile.php")
    Call<User> getUserProfile(@Header("Authorization") String authToken);
    @POST("users/update_profile.php")
    Call<GenericResponse> updateUserProfile(
            @Header("Authorization") String authToken,
            @Body User user // Gửi đối tượng User chứa thông tin mới
    );
    // <<-- ADD FORGOT PASSWORD API -->>
    @FormUrlEncoded // Send data as form fields, not JSON
    @POST("auth/forgot_password.php")
    Call<GenericResponse> requestPasswordReset(@Field("email") String email);

    // <<-- ADD RESET PASSWORD API -->>
    @FormUrlEncoded // Send data as form fields
    @POST("auth/reset_password.php")
    Call<GenericResponse> resetPassword(
            @Field("email") String email,
            @Field("otp") String otp,
            @Field("new_password") String newPassword
    );
    @FormUrlEncoded
    @POST("auth/verify_otp.php")
    Call<GenericResponse> verifyOtp(
            @Field("email") String email,
            @Field("otp") String otp
    );
}