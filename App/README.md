# Food Delivery App - Android Kotlin

Ứng dụng Android được xây dựng bằng Kotlin để kết nối với Food Delivery API.

## Công nghệ sử dụng

- **Kotlin**: Ngôn ngữ lập trình chính
- **Retrofit**: HTTP client cho API calls
- **Coroutines**: Xử lý bất đồng bộ
- **ViewModel & LiveData**: Quản lý UI state
- **Material Design**: UI components
- **SharedPreferences**: Lưu trữ local data

## Cấu trúc thư mục

```
App/
├── src/
│   └── main/
│       ├── java/com/fooddelivery/app/
│       │   ├── data/
│       │   │   ├── api/          # API service và Retrofit client
│       │   │   └── models/       # Data models
│       │   ├── ui/
│       │   │   ├── auth/         # Login screen
│       │   │   ├── registration/ # Registration screen
│       │   │   └── main/         # Main activity
│       │   └── utils/            # Utility classes
│       └── res/                  # Resources (layouts, strings, etc.)
└── build.gradle.kts              # Build configuration
```

## Cài đặt

### 1. Yêu cầu

- Android Studio Hedgehog hoặc mới hơn
- JDK 17
- Android SDK 24+
- Gradle 8.0+

### 2. Cấu hình

1. Mở project trong Android Studio
2. Cập nhật `BASE_URL` trong `RetrofitClient.kt`:

   - Emulator: `http://10.0.2.2:8000/`
   - Real device: `http://YOUR_IP_ADDRESS:8000/`

3. Sync Gradle files

### 3. Chạy ứng dụng

1. Đảm bảo backend API đang chạy
2. Kết nối thiết bị hoặc khởi động emulator
3. Chạy ứng dụng từ Android Studio

## Tính năng

### Đã hoàn thành

- ✅ Cấu trúc project cơ bản
- ✅ API service với Retrofit
- ✅ Data models
- ✅ Authentication flow
- ✅ Registration cho Seller, Buyer, Driver
- ✅ PreferenceManager để lưu token và user info

### Cần phát triển thêm

- ⏳ UI layouts cho các màn hình
- ⏳ Food list và detail screens
- ⏳ Order management screens
- ⏳ Payment screens
- ⏳ Cart functionality
- ⏳ Image loading với Glide
- ⏳ Error handling và loading states
- ⏳ Navigation component setup

## API Integration

Ứng dụng sử dụng các endpoints từ backend API:

- Authentication: `/api/auth/login`, `/api/auth/me`
- Registration: `/api/register/seller`, `/api/register/buyer`, `/api/register/driver`
- Food: `/api/foods/*`
- Order: `/api/orders/*`
- Payment: `/api/payments/*`

## Notes

- Token được lưu trong SharedPreferences
- Token tự động được thêm vào mỗi API request
- Cần implement UI layouts để hoàn thiện ứng dụng
- Cần thêm error handling và loading indicators
- Cần implement navigation giữa các screens
