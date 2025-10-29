package com.example.sportfieldbookingapp.activities; // <<< THAY BẰNG PACKAGE CỦA BẠN

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher; // <<< Import mới
import androidx.activity.result.contract.ActivityResultContracts; // <<< Import mới
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Imports cho Google Sign In
import com.example.sportfieldbookingapp.models.GoogleSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton; // <<< Import nút Google
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
// Imports khác
import com.google.android.material.textfield.TextInputEditText;
import com.example.sportfieldbookingapp.R; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.api.ApiClient; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.api.ApiService; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.models.GenericResponse; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.models.LoginResponse; // <<< THAY BẰNG PACKAGE CỦA BẠN
import com.example.sportfieldbookingapp.models.User; // <<< THAY BẰNG PACKAGE CỦA BẠN

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Tag cho Logcat

    // Views
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private SignInButton btnGoogleSignIn; // Nút Google
    private TextView tvGoToRegister, tvForgotPassword;

    // API Service
    private ApiService apiService;

    // Google Sign In
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Activity created");

        // 1. Ánh xạ View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn); // Ánh xạ nút Google
        Log.d(TAG, "onCreate: Views mapped");

        // 2. Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);
        Log.d(TAG, "onCreate: ApiService initialized");

        // --- CẤU HÌNH GOOGLE SIGN IN ---
        configureGoogleSignIn();
        // --- KẾT THÚC CẤU HÌNH ---

        // --- KHỞI TẠO LAUNCHER ĐỂ NHẬN KẾT QUẢ GOOGLE SIGN IN ---
        setupGoogleSignInLauncher();
        // --- KẾT THÚC KHỞI TẠO LAUNCHER ---


        // 3. Set sự kiện click
        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "btnLogin clicked");
            loginUser();
        });
        tvGoToRegister.setOnClickListener(v -> {
            Log.d(TAG, "tvGoToRegister clicked");
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        tvForgotPassword.setOnClickListener(v -> {
            Log.d(TAG, "tvForgotPassword clicked");
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        btnGoogleSignIn.setOnClickListener(v -> {
            Log.d(TAG, "btnGoogleSignIn clicked");
            signInWithGoogle(); // Gọi hàm đăng nhập Google
        });
        // Cấu hình kích thước chữ cho nút Google (tùy chọn)
        TextView textView = (TextView) btnGoogleSignIn.getChildAt(0);
        textView.setText("Đăng nhập với Google");

        Log.d(TAG, "onCreate: Click listeners set");
    }

    // --- CẤU HÌNH GOOGLE SIGN IN ---
    private void configureGoogleSignIn() {
        // Yêu cầu lấy ID Token (từ Web Client ID) và thông tin cơ bản
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Lấy Web Client ID từ strings.xml
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Log.d(TAG, "configureGoogleSignIn: GoogleSignInClient configured");
    }

    // --- KHỞI TẠO LAUNCHER NHẬN KẾT QUẢ ---
    private void setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "GoogleSignInLauncher: result received, resultCode=" + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleGoogleSignInResult(task);
                    } else {
                        Log.w(TAG, "GoogleSignInLauncher: Sign in failed or cancelled, resultCode=" + result.getResultCode());
                        Toast.makeText(MainActivity.this, "Đăng nhập Google thất bại hoặc bị hủy", Toast.LENGTH_SHORT).show();
                    }
                });
        Log.d(TAG, "setupGoogleSignInLauncher: Launcher registered");
    }

    // --- HÀM BẮT ĐẦU LUỒNG ĐĂNG NHẬP GOOGLE ---
    private void signInWithGoogle() {
        Log.d(TAG, "signInWithGoogle: Starting Google Sign In intent");

        // <<-- THÊM DÒNG NÀY ĐỂ BẮT BUỘC HIỂN THỊ CHỌN TÀI KHOẢN -->>
        // Đăng xuất khỏi Google Sign-In client trước khi đăng nhập mới
        // Việc này sẽ xóa bộ nhớ đệm (cache) về tài khoản đã chọn lần trước
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Sau khi đã đăng xuất (hoặc nếu có lỗi, cũng không sao),
            // chúng ta tiếp tục mở màn hình đăng nhập
            Log.d(TAG, "signInWithGoogle: signOut complete, launching sign-in intent.");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent); // Sử dụng launcher mới
        });
    }

    // --- HÀM XỬ LÝ KẾT QUẢ GOOGLE SIGN IN ---
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Đăng nhập thành công với Google, lấy ID Token
            String idToken = account.getIdToken();

            if (idToken == null) {
                Log.e(TAG, "handleGoogleSignInResult: Google ID Token is null");
                Toast.makeText(this, "Không thể lấy Google ID Token", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i(TAG, "handleGoogleSignInResult: Google Sign-In Success! Sending token to server...");

            // Gửi idToken lên server backend để xác thực và lấy JWT của hệ thống
            sendGoogleTokenToServer(idToken);

        } catch (ApiException e) {
            // Đăng nhập Google thất bại
            Log.e(TAG, "handleGoogleSignInResult: ApiException code=" + e.getStatusCode(), e);
            Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "handleGoogleSignInResult: Unexpected error", e);
            Toast.makeText(this, "Có lỗi xảy ra khi xử lý đăng nhập Google.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Hàm Đăng nhập bằng Email/Password (giữ nguyên logic, chỉ sửa kiểu EditText) ---
    private void loginUser() {
        Log.d(TAG, "loginUser: Function started");
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Log.w(TAG, "loginUser: Email or password empty");
            Toast.makeText(MainActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "loginUser: Input validated");

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        User userToLogin = new User(email, password);

        Call<LoginResponse> call = apiService.loginUser(userToLogin);
        Log.d(TAG, "loginUser: Calling API...");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                Log.d(TAG, "loginUser onResponse: Code=" + response.code());
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng Nhập");

                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        Log.i(TAG, "loginUser onResponse: Success - User: " + loginResponse.getUser().getFullName());
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công! Chào " + loginResponse.getUser().getFullName(), Toast.LENGTH_LONG).show();

                        // Lưu thông tin và điều hướng
                        saveUserInfoAndNavigate(loginResponse.getToken(), loginResponse.getUser().getId(), loginResponse.getUser().getFullName());

                    } else {
                        Log.e(TAG, "loginUser onResponse: Login failed - Code=" + response.code());
                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại! Sai email hoặc mật khẩu.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "loginUser onFailure: " + t.getMessage(), t);
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng Nhập");
                if (!isFinishing()) {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Trong file MainActivity.java (thêm hàm mới này)

    private void sendGoogleTokenToServer(String googleIdToken) {
        Log.d(TAG, "sendGoogleTokenToServer: Sending Google ID Token to backend...");
        // Hiển thị trạng thái đang tải (ví dụ: vô hiệu hóa nút)
        btnLogin.setEnabled(false);
        btnGoogleSignIn.setEnabled(false);

        GoogleSignInRequest request = new GoogleSignInRequest(googleIdToken);

        Call<LoginResponse> call = apiService.loginWithGoogle(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnGoogleSignIn.setEnabled(true);

                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Backend đã xác thực thành công và trả về JWT của hệ thống
                        LoginResponse loginResponse = response.body();
                        Log.i(TAG, "sendGoogleTokenToServer: Backend login success! User: " + loginResponse.getUser().getFullName());
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công: " + loginResponse.getUser().getFullName(), Toast.LENGTH_LONG).show();

                        // Sử dụng lại hàm lưu thông tin và chuyển màn hình
                        saveUserInfoAndNavigate(loginResponse.getToken(), loginResponse.getUser().getId(), loginResponse.getUser().getFullName());

                    } else {
                        // Backend từ chối token hoặc có lỗi
                        Log.e(TAG, "sendGoogleTokenToServer: Backend login failed - Code=" + response.code());
                        Toast.makeText(MainActivity.this, "Xác thực server thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        // Đăng xuất khỏi Google để người dùng có thể chọn lại tài khoản
                        mGoogleSignInClient.signOut();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "sendGoogleTokenToServer: Network failure: " + t.getMessage(), t);
                btnLogin.setEnabled(true);
                btnGoogleSignIn.setEnabled(true);
                if (!isFinishing()) {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    mGoogleSignInClient.signOut();
                }
            }
        });
    }
    private void saveUserInfoAndNavigate(String authToken, int userId, String userName) {
        // Lưu thông tin vào SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_TOKEN", authToken);
        editor.putString("USER_NAME", userName);
        editor.putInt("USER_ID", userId);
        editor.apply();
        Log.d(TAG, "saveUserInfoAndNavigate: User info saved. UserID: " + userId + ", Name: " + userName);

        // Kiểm tra và gửi FCM token còn thiếu
        String pendingFcmToken = sharedPreferences.getString("PENDING_FCM_TOKEN", null);
        if (pendingFcmToken != null) {
            sendFcmTokenToServer(pendingFcmToken);
            SharedPreferences.Editor editorFcm = sharedPreferences.edit();
            editorFcm.remove("PENDING_FCM_TOKEN");
            editorFcm.apply();
            Log.d(TAG, "saveUserInfoAndNavigate: Pending FCM token found and send attempt initiated.");
        }

        // Chuyển sang màn hình chính với animation
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // Đóng màn hình đăng nhập
    }


    // Hàm gửi FCM Token còn thiếu lên server (giữ nguyên)
    private void sendFcmTokenToServer(String token) {
        if (token == null) return;
        Log.d(TAG, "Attempting to send pending FCM token: " + token);
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String authTokenHeader = sharedPreferences.getString("USER_TOKEN", null); // Lấy token đã lưu

        if (authTokenHeader != null) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<GenericResponse> call = apiService.updateFcmToken("Bearer " + authTokenHeader, token); // Gửi token đã lưu
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Pending FCM Token sent successfully.");
                    } else {
                        Log.w(TAG, "Failed to send pending FCM Token. Code: " + response.code());
                        // Cân nhắc lưu lại token nếu gửi thất bại
                    }
                }
                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Error sending pending FCM Token: " + t.getMessage());
                    // Cân nhắc lưu lại token nếu gửi thất bại
                }
            });
        } else {
            Log.e(TAG, "sendFcmTokenToServer: Auth token is null, cannot send FCM token.");
        }
    }
}