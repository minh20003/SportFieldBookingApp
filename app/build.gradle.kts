plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.sportfieldbookingapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.sportfieldbookingapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Retrofit (API calls)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide (Image loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Dòng annotationProcessor này có thể không cần thiết với các phiên bản mới,
    // nhưng cứ thêm vào theo tài liệu để đảm bảo.
    // ksp("com.github.bumptech.glide:compiler:4.16.0") // Nếu dùng KSP
    // annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // Nếu dùng KAPT
}