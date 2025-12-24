# Food Delivery API - Backend

Backend API cho ứng dụng giao đồ ăn được xây dựng bằng FastAPI và MySQL.

## Công nghệ sử dụng

- **FastAPI**: Web framework cho Python
- **SQLAlchemy**: ORM cho database
- **MySQL**: Database
- **Alembic**: Database migration tool
- **JWT**: Authentication
- **Pydantic**: Data validation

## Cấu trúc thư mục

```
Backend-Api/
├── alembic/              # Database migrations
├── models/               # SQLAlchemy ORM models
│   ├── __init__.py
│   ├── user.py          # User, Seller, Buyer, Driver models
│   ├── food.py          # Food, FoodCategory models
│   ├── order.py         # Order, OrderItem models
│   └── payment.py       # Payment model
├── schemas/             # Pydantic schemas cho request/response
│   ├── __init__.py
│   ├── user.py
│   ├── auth.py
│   ├── food.py
│   ├── order.py
│   └── payment.py
├── routers/             # API routes
│   ├── __init__.py
│   ├── auth.py         # Authentication endpoints
│   ├── registration.py # Registration endpoints
│   ├── food.py         # Food management endpoints
│   ├── order.py        # Order management endpoints
│   └── payment.py      # Payment endpoints
├── config.py           # Configuration settings
├── database.py         # Database connection
├── auth.py             # Authentication utilities
├── main.py             # FastAPI app entry point
├── requirements.txt    # Python dependencies
└── .env.example        # Environment variables example
```

## Cài đặt

### 1. Tạo virtual environment

```bash
python -m venv venv
source venv/bin/activate  # Trên Windows: venv\Scripts\activate
```

### 2. Cài đặt dependencies

```bash
pip install -r requirements.txt
```

### 3. Cấu hình database

Tạo file `.env` từ `.env.example` và cập nhật thông tin database:

```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=food_delivery_db
SECRET_KEY=your-secret-key-here
```

### 4. Tạo database

Tạo database MySQL:

```sql
CREATE DATABASE food_delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 5. Chạy migrations

```bash
alembic upgrade head
```

Hoặc nếu chưa có migrations, tạo migration đầu tiên:

```bash
alembic revision --autogenerate -m "Initial migration"
alembic upgrade head
```

### 6. Seed dữ liệu mẫu (Tùy chọn)

Để có dữ liệu mẫu để test, chạy script seeder:

```bash
python seed_data.py
```

Script này sẽ tạo:

- **3 Sellers** với thông tin cửa hàng
- **3 Buyers** với địa chỉ giao hàng
- **3 Drivers** với thông tin phương tiện
- **6 Food Categories** (Cơm, Bún-Phở, Pizza, Đồ ăn nhanh, Đồ uống, Tráng miệng)
- **11 Foods** từ các sellers
- **5 Orders** với order items và payments

**Thông tin đăng nhập mẫu:**

- Seller: `seller1` / `password123`
- Buyer: `buyer1` / `password123`
- Driver: `driver1` / `password123`

> **Lưu ý:** Script sẽ xóa tất cả dữ liệu cũ trước khi seed dữ liệu mới.

### 7. Chạy server

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

API sẽ chạy tại: `http://localhost:8000`

API Documentation: `http://localhost:8000/docs`

## API Endpoints

### Authentication

- `POST /api/auth/login` - Đăng nhập
- `GET /api/auth/me` - Lấy thông tin user hiện tại

### Registration

- `POST /api/register/seller` - Đăng ký Seller
- `POST /api/register/buyer` - Đăng ký Buyer
- `POST /api/register/driver` - Đăng ký Driver

### Food Management (Seller only)

- `POST /api/foods` - Đăng đồ ăn mới
- `GET /api/foods` - Lấy danh sách đồ ăn
- `GET /api/foods/{food_id}` - Lấy chi tiết đồ ăn
- `PUT /api/foods/{food_id}` - Cập nhật đồ ăn
- `DELETE /api/foods/{food_id}` - Xóa đồ ăn
- `POST /api/foods/categories` - Tạo danh mục
- `GET /api/foods/categories` - Lấy danh sách danh mục

### Order Management

- `POST /api/orders` - Tạo đơn hàng (Buyer only)
- `GET /api/orders` - Lấy danh sách đơn hàng
- `GET /api/orders/{order_id}` - Lấy chi tiết đơn hàng
- `PATCH /api/orders/{order_id}/status` - Cập nhật trạng thái đơn hàng

### Payment

- `POST /api/payments` - Tạo thanh toán
- `GET /api/payments` - Lấy danh sách thanh toán
- `GET /api/payments/{payment_id}` - Lấy chi tiết thanh toán
- `PATCH /api/payments/{payment_id}/status` - Cập nhật trạng thái thanh toán

## Sử dụng API

### 1. Đăng ký tài khoản

```bash
curl -X POST "http://localhost:8000/api/register/buyer" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "buyer@example.com",
    "username": "buyer123",
    "password": "password123",
    "full_name": "Nguyen Van A",
    "phone_number": "0123456789",
    "address": "123 Main Street"
  }'
```

### 2. Đăng nhập

```bash
curl -X POST "http://localhost:8000/api/auth/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=buyer123&password=password123"
```

Response sẽ trả về `access_token`.

### 3. Sử dụng token để gọi API

```bash
curl -X GET "http://localhost:8000/api/foods" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Database Models

### User Roles

- **Seller**: Người bán đồ ăn
- **Buyer**: Người mua đồ ăn
- **Driver**: Người giao hàng

### Main Entities

- **User**: Thông tin người dùng cơ bản
- **Seller**: Thông tin cửa hàng
- **Buyer**: Thông tin người mua
- **Driver**: Thông tin tài xế
- **Food**: Đồ ăn
- **FoodCategory**: Danh mục đồ ăn
- **Order**: Đơn hàng
- **OrderItem**: Chi tiết đơn hàng
- **Payment**: Thanh toán

## Notes

- Tất cả các endpoint (trừ registration và login) đều yêu cầu authentication
- Sử dụng JWT token để xác thực
- Token có thời hạn mặc định là 30 phút
- Database sử dụng UTF-8 encoding để hỗ trợ tiếng Việt
