# Hướng dẫn cấu hình Zegocloud Video/Voice Call

## Bước 1: Đăng ký tài khoản Zegocloud

1. Truy cập [Zegocloud Console](https://console.zegocloud.com/)
2. Đăng ký tài khoản miễn phí
3. Xác nhận email

## Bước 2: Tạo Project

1. Đăng nhập vào Console
2. Click "Create Project"
3. Chọn "Voice & Video Call"
4. Đặt tên project (ví dụ: "SportFieldBooking")
5. Chọn "Start with UIKits" để dễ tích hợp

## Bước 3: Lấy Credentials

1. Vào project vừa tạo
2. Tìm và copy:
   - **AppID**: Số dạng `123456789`
   - **AppSign**: Chuỗi dạng `abcdef1234567890...`

## Bước 4: Cấu hình trong App

Mở file `CallActivity.java` và thay thế:

```java
// Thay bằng credentials của bạn
private static final long APP_ID = 123456789L; // AppID từ Console
private static final String APP_SIGN = "abcdef1234567890..."; // AppSign từ Console
```

## Bước 5: Sync Gradle

1. Mở Android Studio
2. Click "Sync Project with Gradle Files"
3. Đợi download dependencies

## Bước 6: Test

1. Build và chạy app trên 2 thiết bị
2. Đăng nhập 2 tài khoản khác nhau
3. Mở chat giữa 2 người
4. Bấm nút gọi điện/video

## Tính năng

- ✅ Gọi thoại 1-1
- ✅ Gọi video 1-1
- ✅ Tự động xin quyền camera/microphone
- ✅ UI có sẵn (không cần tự thiết kế)
- ✅ Chất lượng HD
- ✅ Độ trễ thấp

## Giới hạn miễn phí

- 10,000 phút/tháng
- Đủ cho testing và small-scale usage

## Troubleshooting

### Lỗi "Credentials not configured"
- Kiểm tra APP_ID và APP_SIGN đã được thay thế chưa

### Không kết nối được cuộc gọi
- Đảm bảo cả 2 người dùng cùng APP_ID
- Kiểm tra kết nối internet
- Kiểm tra quyền camera/microphone

### Lỗi build
- Chạy `./gradlew clean build`
- Kiểm tra JitPack repository đã được thêm vào settings.gradle.kts

## Tài liệu tham khảo

- [Zegocloud Android Quick Start](https://www.zegocloud.com/docs/uikit/callkit-android/quick-start)
- [Zegocloud API Reference](https://www.zegocloud.com/docs/api)
