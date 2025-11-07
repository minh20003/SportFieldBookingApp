package com.example.sportfieldbookingapp.utils;

import android.net.Uri;
import android.text.TextUtils;

public final class ImageUrlHelper {

    private static final String BASE_HOST = "http://10.0.2.2";
    private static final String BASE_APP = "http://10.0.2.2/sport-booking-api/";
    
    // Fallback placeholder images - ảnh sân thể thao miễn phí
    private static final String[] FALLBACK_IMAGES = {
        "https://images.unsplash.com/photo-1551958219-acbc608c6377?w=800&q=80", // Soccer field
        "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800&q=80", // Basketball court
        "https://images.unsplash.com/photo-1624526267942-ab0ff8a3e972?w=800&q=80", // Tennis court
        "https://images.unsplash.com/photo-1587280501635-68a0e82cd5ff?w=800&q=80", // Badminton court
        "https://images.unsplash.com/photo-1519766304817-4f37bda74a26?w=800&q=80"  // Sport field
    };

    private ImageUrlHelper() {}

    public static String buildUrl(String raw) {
        if (TextUtils.isEmpty(raw)) {
            return raw;
        }

        String trimmed = raw.trim();

        // Already a valid web url or file/content uri
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") ||
                trimmed.startsWith("content://") || trimmed.startsWith("file://")) {
            return trimmed;
        }

        // If it's a plain path, normalize and prefix with app base
        if (trimmed.startsWith("/")) {
            return BASE_HOST + trimmed;
        }

        // Default: treat as relative to app base
        return BASE_APP + trimmed;
    }
    
    /**
     * Trả về URL ảnh placeholder ngẫu nhiên từ Unsplash
     * Sử dụng khi không có ảnh từ server
     */
    public static String getFallbackImageUrl(int fieldId) {
        // Sử dụng fieldId để chọn ảnh nhất quán cho mỗi field
        int index = Math.abs(fieldId) % FALLBACK_IMAGES.length;
        return FALLBACK_IMAGES[index];
    }
}


