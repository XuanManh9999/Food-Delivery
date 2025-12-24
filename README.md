# Food Delivery Application

Ứng dụng giao đồ ăn với Backend FastAPI + MySQL và Frontend Android Kotlin.

## 🚀 Quick Start

**Xem hướng dẫn chi tiết:** [SETUP_GUIDE.md](./SETUP_GUIDE.md)

### Backend (5 phút):
```bash
cd Backend-Api
python -m venv venv
venv\Scripts\activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
# Tạo và cấu hình .env file
python run.py
```

### Frontend:
1. Mở Android Studio
2. Open Project → Chọn thư mục `App`
3. Cấu hình BASE_URL trong RetrofitClient.kt
4. Click Run (▶️)

## Cấu trúc Project

```
Tran-Chuong-app-5tr/
├── Backend-Api/          # FastAPI Backend
│   ├── models/          # SQLAlchemy ORM models
│   ├── schemas/         # Pydantic schemas
│   ├── routers/         # API routes
│   ├── main.py          # FastAPI app entry point
│   └── requirements.txt  # Python dependencies
└── App/                  # Android Kotlin Frontend
    ├── src/
    │   └── main/
    │       └── java/com/fooddelivery/app/
    │           ├── data/    # API service và models
    │           ├── ui/      # Activities và screens
    │           └── utils/   # Utility classes
    └── build.gradle.kts     # Build configuration
```

## Backend Setup

### Yêu cầu
- Python 3.8+
- MySQL 5.7+
- pip

### Cài đặt

1. **Tạo virtual environment:**
```bash
cd Backend-Api
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
```

2. **Cài đặt dependencies:**
```bash
pip install -r requirements.txt
```

3. **Cấu hình database:**
- Tạo file `.env` từ `.env.example`
- Cập nhật thông tin database MySQL

4. **Tạo database:**
```sql
CREATE DATABASE food_delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

5. **Chạy migrations:**
```bash
alembic revision --autogenerate -m "Initial migration"
alembic upgrade head
```

6. **Chạy server:**
```bash
python run.py
# hoặc
uvicorn main:app --reload
```

API sẽ chạy tại: `http://localhost:8000`
API Documentation: `http://localhost:8000/docs`

## Frontend Setup

### Yêu cầu
- Android Studio Hedgehog+
- JDK 17
- Android SDK 24+

### Cài đặt

1. **Mở project trong Android Studio**

2. **Cấu hình API URL:**
- Mở `App/src/main/java/com/fooddelivery/app/data/api/RetrofitClient.kt`
- Cập nhật `BASE_URL`:
  - Emulator: `http://10.0.2.2:8000/`
  - Real device: `http://YOUR_IP_ADDRESS:8000/`

3. **Sync Gradle và chạy ứng dụng**

## Tính năng đã hoàn thành

### Backend
- ✅ Database models với SQLAlchemy ORM
- ✅ Authentication với JWT
- ✅ API đăng ký cho Seller, Buyer, Driver
- ✅ API quản lý đồ ăn (CRUD)
- ✅ API đặt hàng
- ✅ API thanh toán
- ✅ Database migrations với Alembic
- ✅ Cấu trúc project khoa học và hợp lý

### Frontend
- ✅ Cấu trúc project Android Kotlin
- ✅ API service với Retrofit
- ✅ Data models
- ✅ Authentication flow
- ✅ Registration screens structure
- ✅ PreferenceManager cho local storage

## API Endpoints

### Authentication
- `POST /api/auth/login` - Đăng nhập
- `GET /api/auth/me` - Lấy thông tin user

### Registration
- `POST /api/register/seller` - Đăng ký Seller
- `POST /api/register/buyer` - Đăng ký Buyer
- `POST /api/register/driver` - Đăng ký Driver

### Food Management
- `GET /api/foods` - Lấy danh sách đồ ăn
- `POST /api/foods` - Đăng đồ ăn (Seller only)
- `GET /api/foods/{id}` - Chi tiết đồ ăn
- `PUT /api/foods/{id}` - Cập nhật đồ ăn
- `DELETE /api/foods/{id}` - Xóa đồ ăn

### Order Management
- `POST /api/orders` - Tạo đơn hàng
- `GET /api/orders` - Lấy danh sách đơn hàng
- `GET /api/orders/{id}` - Chi tiết đơn hàng
- `PATCH /api/orders/{id}/status` - Cập nhật trạng thái

### Payment
- `POST /api/payments` - Tạo thanh toán
- `GET /api/payments` - Lấy danh sách thanh toán
- `GET /api/payments/{id}` - Chi tiết thanh toán
- `PATCH /api/payments/{id}/status` - Cập nhật trạng thái

## Database Schema

### Tables
- `users` - Thông tin người dùng
- `sellers` - Thông tin cửa hàng
- `buyers` - Thông tin người mua
- `drivers` - Thông tin tài xế
- `foods` - Đồ ăn
- `food_categories` - Danh mục đồ ăn
- `orders` - Đơn hàng
- `order_items` - Chi tiết đơn hàng
- `payments` - Thanh toán

## Notes

- Backend sử dụng FastAPI với SQLAlchemy ORM
- Database sử dụng MySQL với UTF-8 encoding
- Frontend sử dụng Kotlin với Retrofit cho API calls
- JWT token được sử dụng cho authentication
- Token được lưu trong SharedPreferences (Android)

## Phát triển tiếp theo

- [ ] Hoàn thiện UI layouts cho Android app
- [ ] Thêm tính năng upload ảnh cho đồ ăn
- [ ] Implement real-time order tracking
- [ ] Thêm push notifications
- [ ] Implement cart functionality
- [ ] Thêm rating và review system
- [ ] Optimize database queries
- [ ] Thêm unit tests và integration tests

