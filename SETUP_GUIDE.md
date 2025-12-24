# Hướng Dẫn Chạy Dự Án Food Delivery

Hướng dẫn chi tiết để chạy Backend (FastAPI) và Frontend (Android Kotlin).

---

## 📋 Mục Lục

1. [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
2. [Backend Setup (FastAPI)](#backend-setup-fastapi)
3. [Frontend Setup (Android Kotlin)](#frontend-setup-android-kotlin)
4. [Kiểm Tra Kết Nối](#kiểm-tra-kết-nối)
5. [Troubleshooting](#troubleshooting)

---

## 🔧 Yêu Cầu Hệ Thống

### Backend:

- Python 3.8+
- MySQL 5.7+ hoặc 8.0+
- pip (Python package manager)

### Frontend:

- Android Studio Hedgehog (2023.1.1) hoặc mới hơn
- JDK 17
- Android SDK 24+
- Gradle 8.0+

---

## 🚀 Backend Setup (FastAPI)

### Bước 1: Cài đặt Python và MySQL

#### Windows:

1. Tải Python từ https://www.python.org/downloads/
2. Tải MySQL từ https://dev.mysql.com/downloads/installer/
3. Cài đặt cả 2 và đảm bảo đã thêm vào PATH

#### Linux/Mac:

```bash
# Python (thường đã có sẵn)
python3 --version

# MySQL
sudo apt-get install mysql-server  # Ubuntu/Debian
brew install mysql  # Mac
```

### Bước 2: Tạo Database

1. **Mở MySQL Command Line hoặc MySQL Workbench**

2. **Tạo database:**

```sql
CREATE DATABASE food_delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Kiểm tra database đã tạo:**

```sql
SHOW DATABASES;
```

### Bước 3: Cấu hình Backend

1. **Mở terminal và di chuyển vào thư mục Backend:**

```bash
cd Backend-Api
```

2. **Tạo virtual environment:**

```bash
# Windows
python -m venv venv
venv\Scripts\activate

# Linux/Mac
python3 -m venv venv
source venv/bin/activate
```

3. **Cài đặt dependencies:**

```bash
pip install -r requirements.txt
```

4. **Tạo file .env:**

```bash
# Windows
copy .env.example .env

# Linux/Mac
cp .env.example .env
```

5. **Cập nhật file .env với thông tin của bạn:**

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_mysql_password
DB_NAME=food_delivery_db

# JWT Configuration
SECRET_KEY=your-super-secret-key-change-this-in-production-min-32-chars
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=30

# Server Configuration
HOST=0.0.0.0
PORT=8000
FRONTEND_URL=http://localhost:3000

# Email Configuration (Optional - có thể để trống để test)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM_EMAIL=your-email@gmail.com
SMTP_FROM_NAME=Food Delivery
SMTP_USE_TLS=True

# Email Settings
EMAIL_VERIFICATION_EXPIRE_HOURS=24
PASSWORD_RESET_EXPIRE_HOURS=1
```

**Lưu ý:**

- Thay `your_mysql_password` bằng mật khẩu MySQL của bạn
- Thay `SECRET_KEY` bằng một chuỗi ngẫu nhiên dài (ít nhất 32 ký tự)
- Email có thể để trống để test (sẽ không gửi email nhưng app vẫn chạy được)

### Bước 4: Chạy Database Migrations

```bash
# Tạo migration đầu tiên
alembic revision --autogenerate -m "Initial migration"

# Apply migrations
alembic upgrade head
```

**Nếu gặp lỗi:** Có thể database tables đã được tạo tự động. Bỏ qua bước này và tiếp tục.

### Bước 5: Chạy Backend Server

```bash
# Cách 1: Dùng run.py
python run.py

# Cách 2: Dùng uvicorn trực tiếp
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

**Kết quả mong đợi:**

```
INFO:     Started server process
INFO:     Waiting for application startup.
INFO:     Application startup complete.
INFO:     Uvicorn running on http://0.0.0.0:8000 (Press CTRL+C to quit)
```

### Bước 6: Kiểm tra Backend

1. **Mở trình duyệt và truy cập:**

   - API Documentation: http://localhost:8000/docs
   - Alternative docs: http://localhost:8000/redoc
   - Health check: http://localhost:8000/health

2. **Test API:**
   - Vào http://localhost:8000/docs
   - Thử endpoint `GET /health` để kiểm tra server đang chạy

---

## 📱 Frontend Setup (Android Kotlin)

### Bước 1: Cài đặt Android Studio

1. **Tải Android Studio:**

   - Truy cập: https://developer.android.com/studio
   - Tải phiên bản mới nhất
   - Cài đặt theo hướng dẫn

2. **Cấu hình Android Studio:**
   - Mở Android Studio
   - Chọn "More Actions" > "SDK Manager"
   - Cài đặt:
     - Android SDK Platform 34
     - Android SDK Build-Tools
     - Android Emulator
     - Intel x86 Emulator Accelerator (HAXM installer) - nếu dùng Windows/Intel Mac

### Bước 2: Mở Project

1. **Mở Android Studio**

2. **Chọn "Open an Existing Project"**

3. **Chọn thư mục `App` trong project:**

   ```
   Tran-Chuong-app-5tr/App
   ```

4. **Đợi Gradle sync hoàn tất** (có thể mất vài phút lần đầu)

### Bước 3: Cấu hình API URL

1. **Mở file:**

   ```
   App/src/main/java/com/fooddelivery/app/data/api/RetrofitClient.kt
   ```

2. **Cập nhật BASE_URL:**

   **Nếu chạy trên Emulator:**

   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8000/"
   ```

   **Nếu chạy trên thiết bị thật:**

   ```kotlin
   // Thay YOUR_IP_ADDRESS bằng IP máy tính của bạn
   private const val BASE_URL = "http://192.168.1.100:8000/"
   ```

3. **Lấy IP Address của máy:**

   **Windows:**

   ```cmd
   ipconfig
   ```

   Tìm "IPv4 Address" (ví dụ: 192.168.1.100)

   **Linux/Mac:**

   ```bash
   ifconfig
   # hoặc
   ip addr show
   ```

### Bước 4: Tạo Android Emulator (Nếu chưa có)

1. **Mở AVD Manager:**

   - Tools > Device Manager
   - Hoặc click icon điện thoại trên toolbar

2. **Create Device:**
   - Chọn "Create Device"
   - Chọn device (ví dụ: Pixel 5)
   - Chọn System Image (API 34 - Android 14)
   - Finish

### Bước 5: Chạy App

1. **Chọn emulator hoặc thiết bị thật** từ dropdown

2. **Click nút Run (▶️)** hoặc nhấn `Shift + F10`

3. **Đợi app build và install** (lần đầu có thể mất vài phút)

4. **App sẽ tự động mở trên emulator/thiết bị**

---

## 🔗 Kiểm Tra Kết Nối

### Test Backend → Frontend:

1. **Đảm bảo Backend đang chạy:**

   ```bash
   # Kiểm tra trong terminal Backend
   # Phải thấy: "Uvicorn running on http://0.0.0.0:8000"
   ```

2. **Test từ Android App:**

   - Mở app trên emulator/thiết bị
   - Thử đăng ký tài khoản mới
   - Kiểm tra logs trong Android Studio Logcat để xem có lỗi không

3. **Test từ Browser:**
   - Mở http://localhost:8000/docs
   - Thử POST `/api/register/buyer` với data:
   ```json
   {
     "email": "test@example.com",
     "username": "testuser",
     "password": "password123",
     "full_name": "Test User",
     "phone_number": "0123456789",
     "address": "123 Test Street"
   }
   ```

### Kiểm tra Logs:

**Backend Logs:**

- Xem trong terminal nơi chạy `python run.py`
- Sẽ hiển thị các API requests

**Frontend Logs:**

- Mở Android Studio
- Tab "Logcat" ở dưới cùng
- Filter theo "fooddelivery" hoặc package name

---

## 🐛 Troubleshooting

### Backend Issues:

#### 1. Lỗi kết nối Database:

```
Error: (2003, "Can't connect to MySQL server")
```

**Giải pháp:**

- Kiểm tra MySQL đang chạy:

  ```bash
  # Windows
  services.msc → Tìm MySQL → Start

  # Linux
  sudo systemctl start mysql

  # Mac
  brew services start mysql
  ```

- Kiểm tra thông tin trong `.env` đúng chưa
- Kiểm tra MySQL port (mặc định 3306)

#### 2. Lỗi Import:

```
ModuleNotFoundError: No module named 'xxx'
```

**Giải pháp:**

```bash
# Đảm bảo đã activate virtual environment
# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate

# Cài lại dependencies
pip install -r requirements.txt
```

#### 3. Lỗi Port đã được sử dụng:

```
Error: Address already in use
```

**Giải pháp:**

```bash
# Tìm process đang dùng port 8000
# Windows
netstat -ano | findstr :8000
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8000
kill -9 <PID>

# Hoặc đổi port trong .env
PORT=8001
```

### Frontend Issues:

#### 1. Lỗi Gradle Sync:

```
Failed to sync Gradle project
```

**Giải pháp:**

- File > Invalidate Caches / Restart
- File > Sync Project with Gradle Files
- Kiểm tra internet connection (cần tải dependencies)

#### 2. Lỗi kết nối API:

```
Failed to connect to /10.0.2.2:8000
```

**Giải pháp:**

- Kiểm tra Backend đang chạy
- Kiểm tra BASE_URL trong RetrofitClient.kt
- Nếu dùng thiết bị thật:
  - Đảm bảo điện thoại và máy tính cùng WiFi
  - Kiểm tra firewall không chặn port 8000
  - Thử tắt Windows Firewall tạm thời để test

#### 3. Lỗi Build:

```
Build failed
```

**Giải pháp:**

- Clean Project: Build > Clean Project
- Rebuild: Build > Rebuild Project
- Kiểm tra JDK version (phải là 17)
- File > Project Structure > SDK Location > JDK location

#### 4. App crash khi mở:

- Kiểm tra Logcat để xem lỗi cụ thể
- Đảm bảo đã cấu hình BASE_URL
- Kiểm tra AndroidManifest.xml có đúng permissions

---

## 📝 Checklist Chạy Dự Án

### Backend:

- [ ] Python 3.8+ đã cài đặt
- [ ] MySQL đã cài đặt và chạy
- [ ] Database `food_delivery_db` đã tạo
- [ ] Virtual environment đã tạo và activate
- [ ] Dependencies đã cài đặt (`pip install -r requirements.txt`)
- [ ] File `.env` đã tạo và cấu hình đúng
- [ ] Migrations đã chạy (`alembic upgrade head`)
- [ ] Backend server đang chạy (`python run.py`)
- [ ] Có thể truy cập http://localhost:8000/docs

### Frontend:

- [ ] Android Studio đã cài đặt
- [ ] JDK 17 đã cài đặt
- [ ] Android SDK 24+ đã cài đặt
- [ ] Project đã mở trong Android Studio
- [ ] Gradle sync thành công
- [ ] BASE_URL đã cấu hình đúng trong RetrofitClient.kt
- [ ] Emulator hoặc thiết bị thật đã sẵn sàng
- [ ] App đã build và chạy thành công

---

## 🎯 Quick Start Commands

### Backend:

```bash
cd Backend-Api
python -m venv venv
venv\Scripts\activate  # Windows
# source venv/bin/activate  # Linux/Mac
pip install -r requirements.txt
# Tạo và cấu hình .env file
python run.py
```

### Frontend:

1. Mở Android Studio
2. Open Project → Chọn thư mục `App`
3. Đợi Gradle sync
4. Cấu hình BASE_URL trong RetrofitClient.kt
5. Click Run (▶️)

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề:

1. Kiểm tra phần Troubleshooting ở trên
2. Xem logs chi tiết trong terminal (Backend) và Logcat (Frontend)
3. Đảm bảo đã làm đúng các bước trong checklist

**Chúc bạn thành công! 🚀**
