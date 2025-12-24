# Quick Start - Android App

Hướng dẫn nhanh để chạy Android App.

## ⚡ Chạy Nhanh (5 phút)

### 1. Mở Project
1. Mở Android Studio
2. File > Open > Chọn thư mục `App`
3. Đợi Gradle sync hoàn tất

### 2. Cấu hình API URL

Mở file: `App/src/main/java/com/fooddelivery/app/data/api/RetrofitClient.kt`

**Emulator:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"
```

**Thiết bị thật:**
```kotlin
private const val BASE_URL = "http://YOUR_IP_ADDRESS:8000/"
```
(Lấy IP bằng `ipconfig` trên Windows hoặc `ifconfig` trên Linux/Mac)

### 3. Chạy App
1. Chọn emulator hoặc thiết bị
2. Click Run (▶️) hoặc nhấn `Shift + F10`

## 📋 Chi Tiết

Xem file `SETUP_GUIDE.md` ở thư mục gốc để có hướng dẫn đầy đủ.

