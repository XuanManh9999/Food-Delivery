# Quick Start - Backend API

Hướng dẫn nhanh để chạy Backend API.

## ⚡ Chạy Nhanh (5 phút)

### 1. Cài đặt Dependencies
```bash
cd Backend-Api
python -m venv venv

# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate

pip install -r requirements.txt
```

### 2. Cấu hình Database

Tạo database MySQL:
```sql
CREATE DATABASE food_delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Tạo file .env
```bash
# Copy từ .env.example
copy .env.example .env  # Windows
cp .env.example .env   # Linux/Mac
```

Cập nhật thông tin trong `.env`:
```env
DB_PASSWORD=your_mysql_password
SECRET_KEY=your-secret-key-min-32-chars
```

### 4. Chạy Server
```bash
python run.py
```

### 5. Kiểm tra
Mở trình duyệt: http://localhost:8000/docs

## 📋 Chi Tiết

Xem file `SETUP_GUIDE.md` ở thư mục gốc để có hướng dẫn đầy đủ.

