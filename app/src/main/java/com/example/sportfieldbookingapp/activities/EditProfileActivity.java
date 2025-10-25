package com.example.sportfieldbookingapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone;
    private Button btnSaveProfile;
    private ApiService apiService;
    private User currentUser; // To hold the user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 1. Map Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // 2. Initialize ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // 3. Fetch current profile data
        fetchUserProfile();

        // 4. Set Save button click listener
        btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
    }

    private void fetchUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String authToken = "Bearer " + token;

        Call<User> call = apiService.getUserProfile(authToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    populateUI(currentUser);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fill the EditText fields with data
    private void populateUI(User user) {
        etFullName.setText(user.getFullName());
        etEmail.setText(user.getEmail()); // Email is disabled for editing
        etPhone.setText(user.getPhone());
    }

    private void saveProfileChanges() {
        String newFullName = etFullName.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(newFullName)) {
            etFullName.setError("Name cannot be empty");
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the currentUser object with new data
        currentUser.setFullName(newFullName);
        currentUser.setPhone(newPhone);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("USER_TOKEN", null);
        if (token == null) { return; } // Should not happen if fetch worked
        String authToken = "Bearer " + token;

        // Call the update API
        Call<GenericResponse> call = apiService.updateUserProfile(authToken, currentUser);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    // Update the saved name in SharedPreferences too for immediate effect on ProfileFragment
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USER_NAME", newFullName);
                    editor.apply();
                    finish(); // Close the edit screen
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}