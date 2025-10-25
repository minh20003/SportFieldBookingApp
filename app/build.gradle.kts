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
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
// OkHttp Logging Interceptor - THÊM DÒNG NÀY
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
// Gson
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.annotation:annotation:1.7.1")
}