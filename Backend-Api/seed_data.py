"""
Script để seed dữ liệu mẫu vào database
Chạy: python seed_data.py
"""
from sqlalchemy.orm import Session
from sqlalchemy import text
from database import SessionLocal, engine, Base
# Import tất cả models để SQLAlchemy có thể configure relationships
from models import user, food, order, payment, email_token
from models.user import User, Seller, Buyer, Driver, UserRole
from models.food import Food, FoodCategory
from models.order import Order, OrderItem, OrderStatus
from models.payment import Payment, PaymentMethod, PaymentStatus
from models.email_token import PasswordResetToken, EmailVerificationToken
from datetime import datetime, timedelta, timezone
import random
import bcrypt

# Đảm bảo tất cả models được load để SQLAlchemy có thể configure relationships
# Base.metadata.create_all sẽ được gọi nếu cần, nhưng ở đây chỉ cần import

# Import get_password_hash từ auth, nhưng có fallback
try:
    from auth import get_password_hash
except ImportError:
    def get_password_hash(password: str) -> str:
        password_bytes = password.encode('utf-8')
        if len(password_bytes) > 72:
            password_bytes = password_bytes[:72]
        salt = bcrypt.gensalt(rounds=12)
        hashed = bcrypt.hashpw(password_bytes, salt)
        return hashed.decode('utf-8')

# Tạo session
db: Session = SessionLocal()


def clear_database():
    """Xóa tất cả dữ liệu cũ (theo thứ tự để tránh lỗi foreign key)"""
    print("Đang xóa dữ liệu cũ...")
    try:
        # Xóa theo thứ tự để tránh lỗi foreign key (dùng raw SQL để tránh lỗi relationship)
        db.execute(text("DELETE FROM payments"))
        db.execute(text("DELETE FROM order_items"))
        db.execute(text("DELETE FROM orders"))
        db.execute(text("DELETE FROM foods"))
        db.execute(text("DELETE FROM food_categories"))
        db.execute(text("DELETE FROM password_reset_tokens"))
        db.execute(text("DELETE FROM email_verification_tokens"))
        db.execute(text("DELETE FROM drivers"))
        db.execute(text("DELETE FROM buyers"))
        db.execute(text("DELETE FROM sellers"))
        db.execute(text("DELETE FROM users"))
        db.commit()
        print("✓ Đã xóa dữ liệu cũ")
    except Exception as e:
        db.rollback()
        print(f"Lỗi khi xóa dữ liệu: {e}")
        # Nếu vẫn lỗi, thử tiếp tục (có thể bảng chưa tồn tại)


def seed_users():
    """Tạo users với các role khác nhau"""
    print("\nĐang tạo users...")
    
    users_data = [
        # Sellers
        {
            "email": "seller1@example.com",
            "username": ""seller1,
            "password": "password123",
            "full_name": "Nguyễn Văn Bán",
            "phone_number": "0912345678",
            "role": UserRole.SELLER,
            "is_active": True,
            "is_verified": True
        },
        {
            "email": "seller2@example.com",
            "username": "seller2",
            "password": "password123",
            "full_name": "Trần Thị Quán",
            "phone_number": "0923456789",
            "role": UserRole.SELLER,
            "is_active": True,
            "is_verified": True
        },
        {
            "email": "seller3@example.com",
            "username": "seller3",
            "password": "password123",
            "full_name": "Lê Văn Nhà Hàng",
            "phone_number": "0934567890",
            "role": UserRole.SELLER,
            "is_active": True,
            "is_verified": True
        },
        # Buyers
        {
            "email": "buyer1@example.com",
            "username": "buyer1",
            "password": "password123",
            "full_name": "Phạm Văn Mua",
            "phone_number": "0945678901",
            "role": UserRole.BUYER,
            "is_active": True,
            "is_verified": True
        },
        {
            "email": "buyer2@example.com",
            "username": "buyer2",
            "password": "password123",
            "full_name": "Hoàng Thị Khách",
            "phone_number": "0956789012",
            "role": UserRole.BUYER,
            "is_active": True,
            "is_verified": True
        },
        {
            "email": "buyer3@example.com",
            "username": "buyer3",
            "password": "password123",
            "full_name": "Vũ Văn Đặt",
            "phone_number": "0967890123",
            "role": UserRole.BUYER,
            "is_active": True,
            "is_verified": True
        },
        # Drivers
        {
            "email": "driver1@example.com",
            "username": "driver1",
            "password": "password123",
            "full_name": "Đỗ Văn Giao",
            "phone_number": "0978901234",
            "role": UserRole.DRIVER,
            "is_active": True,
            "is_verified": True
        },
        {
            "email": "driver2@example.com",
            "username": "driver2",
            "password": "password123",
            "full_name": "Bùi Thị Vận",
            "phone_number": "0989012345",
            "role": UserRole.DRIVER,
            "is_active": True,
            "is_verified": True
        },
        {
            "email": "driver3@example.com",
            "username": "driver3",
            "password": "password123",
            "full_name": "Ngô Văn Chuyển",
            "phone_number": "0990123456",
            "role": UserRole.DRIVER,
            "is_active": True,
            "is_verified": True
        },
    ]
    
    def hash_password(password: str) -> str:
        """Hash password với xử lý lỗi bcrypt"""
        try:
            return get_password_hash(password)
        except (ValueError, AttributeError, Exception) as e:
            # Fallback: dùng bcrypt trực tiếp nếu passlib gặp lỗi
            password_bytes = password.encode('utf-8')
            if len(password_bytes) > 72:
                password_bytes = password_bytes[:72]
            salt = bcrypt.gensalt(rounds=12)
            hashed = bcrypt.hashpw(password_bytes, salt)
            return hashed.decode('utf-8')
    
    created_users = []
    for user_data in users_data:
        user = User(
            email=user_data["email"],
            username=user_data["username"],
            hashed_password=hash_password(user_data["password"]),
            full_name=user_data["full_name"],
            phone_number=user_data["phone_number"],
            role=user_data["role"],
            is_active=user_data["is_active"],
            is_verified=user_data["is_verified"]
        )
        db.add(user)
        created_users.append(user)
    
    db.commit()
    print(f"✓ Đã tạo {len(created_users)} users")
    return created_users


def seed_sellers(users):
    """Tạo seller profiles"""
    print("\nĐang tạo sellers...")
    
    sellers_data = [
        {
            "store_name": "Quán Cơm Gà Ngon",
            "store_address": "123 Đường Nguyễn Huệ, Quận 1, TP.HCM",
            "store_phone": "0912345678",
            "store_description": "Chuyên cơm gà, cơm tấm ngon nhất thành phố",
            "license_number": "GP001",
            "rating": 4.5,
            "total_orders": 150
        },
        {
            "store_name": "Pizza Italia",
            "store_address": "456 Đường Lê Lợi, Quận 3, TP.HCM",
            "store_phone": "0923456789",
            "store_description": "Pizza Ý chính hiệu, bánh mì sandwich",
            "license_number": "GP002",
            "rating": 4.8,
            "total_orders": 200
        },
        {
            "store_name": "Bún Bò Huế",
            "store_address": "789 Đường Trần Hưng Đạo, Quận 5, TP.HCM",
            "store_phone": "0934567890",
            "store_description": "Bún bò Huế, phở, bánh canh đặc sản",
            "license_number": "GP003",
            "rating": 4.3,
            "total_orders": 120
        },
    ]
    
    seller_users = [u for u in users if u.role == UserRole.SELLER]
    created_sellers = []
    
    for i, seller_data in enumerate(sellers_data):
        if i < len(seller_users):
            seller = Seller(
                user_id=seller_users[i].id,
                **seller_data
            )
            db.add(seller)
            created_sellers.append(seller)
    
    db.commit()
    print(f"✓ Đã tạo {len(created_sellers)} sellers")
    return created_sellers


def seed_buyers(users):
    """Tạo buyer profiles"""
    print("\nĐang tạo buyers...")
    
    buyers_data = [
        {
            "address": "10/5 Đường Võ Văn Tần, Quận 3, TP.HCM",
            "default_payment_method": "e_wallet",
            "total_orders": 25,
            "total_spent": 2500000
        },
        {
            "address": "20 Nguyễn Thị Minh Khai, Quận 1, TP.HCM",
            "default_payment_method": "bank_transfer",
            "total_orders": 40,
            "total_spent": 5000000
        },
        {
            "address": "30 Đường Cách Mạng Tháng 8, Quận 10, TP.HCM",
            "default_payment_method": "cash",
            "total_orders": 15,
            "total_spent": 1800000
        },
    ]
    
    buyer_users = [u for u in users if u.role == UserRole.BUYER]
    created_buyers = []
    
    for i, buyer_data in enumerate(buyers_data):
        if i < len(buyer_users):
            buyer = Buyer(
                user_id=buyer_users[i].id,
                **buyer_data
            )
            db.add(buyer)
            created_buyers.append(buyer)
    
    db.commit()
    print(f"✓ Đã tạo {len(created_buyers)} buyers")
    return created_buyers


def seed_drivers(users):
    """Tạo driver profiles"""
    print("\nĐang tạo drivers...")
    
    drivers_data = [
        {
            "license_number": "DL001",
            "vehicle_type": "xe máy",
            "vehicle_number": "51A-12345",
            "is_available": True,
            "current_location_lat": "10.7769",
            "current_location_lng": "106.7009",
            "rating": 4.7,
            "total_deliveries": 300
        },
        {
            "license_number": "DL002",
            "vehicle_type": "xe máy",
            "vehicle_number": "51B-67890",
            "is_available": True,
            "current_location_lat": "10.7626",
            "current_location_lng": "106.6602",
            "rating": 4.5,
            "total_deliveries": 250
        },
        {
            "license_number": "DL003",
            "vehicle_type": "xe đạp điện",
            "vehicle_number": "51C-11111",
            "is_available": False,
            "current_location_lat": "10.8152",
            "current_location_lng": "106.6287",
            "rating": 4.6,
            "total_deliveries": 180
        },
    ]
    
    driver_users = [u for u in users if u.role == UserRole.DRIVER]
    created_drivers = []
    
    for i, driver_data in enumerate(drivers_data):
        if i < len(driver_users):
            driver = Driver(
                user_id=driver_users[i].id,
                **driver_data
            )
            db.add(driver)
            created_drivers.append(driver)
    
    db.commit()
    print(f"✓ Đã tạo {len(created_drivers)} drivers")
    return created_drivers


def seed_categories():
    """Tạo food categories"""
    print("\nĐang tạo food categories...")
    
    categories_data = [
        {"name": "Cơm", "description": "Các món cơm, cơm tấm, cơm gà"},
        {"name": "Bún - Phở", "description": "Bún, phở, bánh canh các loại"},
        {"name": "Pizza", "description": "Pizza các loại"},
        {"name": "Đồ ăn nhanh", "description": "Burger, gà rán, khoai tây chiên"},
        {"name": "Đồ uống", "description": "Nước ngọt, trà, cà phê"},
        {"name": "Tráng miệng", "description": "Bánh ngọt, kem, chè"},
    ]
    
    created_categories = []
    for cat_data in categories_data:
        category = FoodCategory(**cat_data)
        db.add(category)
        created_categories.append(category)
    
    db.commit()
    print(f"✓ Đã tạo {len(created_categories)} categories")
    return created_categories


def seed_foods(sellers, categories):
    """Tạo foods"""
    print("\nĐang tạo foods...")
    
    foods_data = [
        # Seller 1 - Quán Cơm Gà
        {"seller": 0, "category": 0, "name": "Cơm Gà Nướng", "description": "Cơm gà nướng thơm lừng, kèm rau sống", "price": 45000, "is_available": True, "stock_quantity": 50, "rating": 4.5, "total_orders": 120},
        {"seller": 0, "category": 0, "name": "Cơm Tấm Sườn", "description": "Cơm tấm sườn nướng, bì, chả trứng", "price": 55000, "is_available": True, "stock_quantity": 40, "rating": 4.7, "total_orders": 150},
        {"seller": 0, "category": 0, "name": "Cơm Gà Xối Mỡ", "description": "Cơm gà xối mỡ giòn tan", "price": 50000, "is_available": True, "stock_quantity": 30, "rating": 4.4, "total_orders": 80},
        
        # Seller 2 - Pizza Italia
        {"seller": 1, "category": 2, "name": "Pizza Margherita", "description": "Pizza cổ điển với phô mai mozzarella và cà chua", "price": 180000, "is_available": True, "stock_quantity": 20, "rating": 4.8, "total_orders": 200},
        {"seller": 1, "category": 2, "name": "Pizza Pepperoni", "description": "Pizza với pepperoni và phô mai", "price": 220000, "is_available": True, "stock_quantity": 15, "rating": 4.9, "total_orders": 180},
        {"seller": 1, "category": 2, "name": "Pizza Hải Sản", "description": "Pizza với tôm, mực, cá ngừ", "price": 250000, "is_available": True, "stock_quantity": 10, "rating": 4.7, "total_orders": 120},
        {"seller": 1, "category": 3, "name": "Burger Bò", "description": "Burger bò thịt nướng, rau sống, sốt đặc biệt", "price": 95000, "is_available": True, "stock_quantity": 25, "rating": 4.6, "total_orders": 100},
        
        # Seller 3 - Bún Bò Huế
        {"seller": 2, "category": 1, "name": "Bún Bò Huế", "description": "Bún bò Huế đậm đà, cay nồng", "price": 60000, "is_available": True, "stock_quantity": 60, "rating": 4.6, "total_orders": 200},
        {"seller": 2, "category": 1, "name": "Phở Bò", "description": "Phở bò truyền thống", "price": 65000, "is_available": True, "stock_quantity": 50, "rating": 4.5, "total_orders": 150},
        {"seller": 2, "category": 1, "name": "Bánh Canh Cua", "description": "Bánh canh cua thơm ngon", "price": 70000, "is_available": True, "stock_quantity": 40, "rating": 4.4, "total_orders": 100},
        {"seller": 2, "category": 5, "name": "Chè Đậu Xanh", "description": "Chè đậu xanh mát lạnh", "price": 25000, "is_available": True, "stock_quantity": 100, "rating": 4.3, "total_orders": 80},
    ]
    
    created_foods = []
    for food_data in foods_data:
        food = Food(
            seller_id=sellers[food_data["seller"]].id,
            category_id=categories[food_data["category"]].id if food_data["category"] < len(categories) else None,
            name=food_data["name"],
            description=food_data["description"],
            price=food_data["price"],
            is_available=food_data["is_available"],
            stock_quantity=food_data["stock_quantity"],
            rating=food_data["rating"],
            total_orders=food_data["total_orders"],
            image_url=f"https://via.placeholder.com/400x300?text={food_data['name'].replace(' ', '+')}"
        )
        db.add(food)
        created_foods.append(food)
    
    db.commit()
    print(f"✓ Đã tạo {len(created_foods)} foods")
    return created_foods


def seed_orders(users, sellers, drivers, foods):
    """Tạo orders với order items"""
    print("\nĐang tạo orders...")
    
    buyer_users = [u for u in users if u.role == UserRole.BUYER]
    available_drivers = [d for d in drivers if d.is_available]
    
    # Tạo map seller -> foods trước
    seller_foods_map = {}  # Map seller index to their foods
    for i, seller in enumerate(sellers):
        seller_foods_map[i] = [f for f in foods if f.seller_id == seller.id]
    
    # Orders data với food_index dựa trên foods của seller đó (index trong seller_foods_map)
    orders_data = [
        {
            "buyer": 0,
            "seller": 0,
            "driver": 0 if available_drivers else None,
            "status": OrderStatus.DELIVERED,
            "foods": [(0, 2), (1, 1)],  # (food_index trong seller_foods_map[seller], quantity)
            "delivery_address": "10/5 Đường Võ Văn Tần, Quận 3, TP.HCM",
            "delivery_phone": "0945678901",
            "delivery_fee": 20000,
            "payment_method": PaymentMethod.E_WALLET,
            "payment_status": PaymentStatus.COMPLETED
        },
        {
            "buyer": 1,
            "seller": 1,
            "driver": 1 if len(available_drivers) > 1 else 0 if available_drivers else None,
            "status": OrderStatus.DELIVERING,
            "foods": [(0, 1), (1, 1)],  # Pizza Margherita, Pizza Pepperoni
            "delivery_address": "20 Nguyễn Thị Minh Khai, Quận 1, TP.HCM",
            "delivery_phone": "0956789012",
            "delivery_fee": 25000,
            "payment_method": PaymentMethod.BANK_TRANSFER,
            "payment_status": PaymentStatus.COMPLETED
        },
        {
            "buyer": 2,
            "seller": 2,
            "driver": None,
            "status": OrderStatus.PREPARING,
            "foods": [(0, 2), (1, 1)],  # Bún Bò Huế x2, Phở Bò x1
            "delivery_address": "30 Đường Cách Mạng Tháng 8, Quận 10, TP.HCM",
            "delivery_phone": "0967890123",
            "delivery_fee": 15000,
            "payment_method": PaymentMethod.CASH,
            "payment_status": PaymentStatus.PENDING
        },
        {
            "buyer": 0,
            "seller": 1,
            "driver": None,
            "status": OrderStatus.CONFIRMED,
            "foods": [(3, 1)],  # Burger Bò
            "delivery_address": "10/5 Đường Võ Văn Tần, Quận 3, TP.HCM",
            "delivery_phone": "0945678901",
            "delivery_fee": 20000,
            "payment_method": PaymentMethod.E_WALLET,
            "payment_status": PaymentStatus.PROCESSING
        },
        {
            "buyer": 1,
            "seller": 0,
            "driver": 0 if available_drivers else None,
            "status": OrderStatus.READY,
            "foods": [(0, 1), (2, 1)],  # Cơm Gà Nướng, Cơm Gà Xối Mỡ
            "delivery_address": "20 Nguyễn Thị Minh Khai, Quận 1, TP.HCM",
            "delivery_phone": "0956789012",
            "delivery_fee": 25000,
            "payment_method": PaymentMethod.BANK_TRANSFER,
            "payment_status": PaymentStatus.COMPLETED
        },
    ]
    
    created_orders = []
    
    for idx, order_data in enumerate(orders_data):
        # Lấy foods của seller này
        seller_foods = seller_foods_map.get(order_data["seller"], [])
        if not seller_foods:
            continue
        
        # Tính subtotal
        subtotal = 0
        order_items_data = []
        for food_idx, quantity in order_data["foods"]:
            if food_idx < len(seller_foods):
                food = seller_foods[food_idx]
                unit_price = food.price
                item_subtotal = unit_price * quantity
                subtotal += item_subtotal
                order_items_data.append({
                    "food": food,
                    "quantity": quantity,
                    "unit_price": unit_price,
                    "subtotal": item_subtotal
                })
        
        total_amount = subtotal + order_data["delivery_fee"]
        
        # Tạo order
        order_number = f"ORD{datetime.now().strftime('%Y%m%d')}{idx+1:04d}"
        order = Order(
            buyer_id=buyer_users[order_data["buyer"]].id,
            seller_id=sellers[order_data["seller"]].id,
            driver_id=available_drivers[order_data["driver"]].id if order_data["driver"] is not None and order_data["driver"] < len(available_drivers) else None,
            order_number=order_number,
            status=order_data["status"],
            subtotal=subtotal,
            delivery_fee=order_data["delivery_fee"],
            total_amount=total_amount,
            delivery_address=order_data["delivery_address"],
            delivery_phone=order_data["delivery_phone"],
            delivery_notes=f"Giao hàng nhanh" if idx % 2 == 0 else None,
            delivered_at=datetime.now(timezone.utc) if order_data["status"] == OrderStatus.DELIVERED else None
        )
        db.add(order)
        db.flush()  # Để lấy order.id
        
        # Tạo order items
        for item_data in order_items_data:
            order_item = OrderItem(
                order_id=order.id,
                food_id=item_data["food"].id,
                quantity=item_data["quantity"],
                unit_price=item_data["unit_price"],
                subtotal=item_data["subtotal"]
            )
            db.add(order_item)
        
        # Tạo payment
        payment_number = f"PAY{datetime.now().strftime('%Y%m%d')}{idx+1:04d}"
        payment = Payment(
            order_id=order.id,
            payment_number=payment_number,
            payment_method=order_data["payment_method"],
            amount=total_amount,
            status=order_data["payment_status"],
            transaction_id=f"TXN{random.randint(100000, 999999)}" if order_data["payment_status"] == PaymentStatus.COMPLETED else None,
            paid_at=datetime.now(timezone.utc) if order_data["payment_status"] == PaymentStatus.COMPLETED else None
        )
        db.add(payment)
        
        created_orders.append(order)
    
    db.commit()
    print(f"✓ Đã tạo {len(created_orders)} orders với order items và payments")
    return created_orders


def main():
    """Hàm chính để seed dữ liệu"""
    print("=" * 50)
    print("BẮT ĐẦU SEED DỮ LIỆU")
    print("=" * 50)
    
    try:
        # Xóa dữ liệu cũ
        clear_database()
        
        # Seed theo thứ tự
        users = seed_users()
        sellers = seed_sellers(users)
        buyers = seed_buyers(users)
        drivers = seed_drivers(users)
        categories = seed_categories()
        foods = seed_foods(sellers, categories)
        orders = seed_orders(users, sellers, drivers, foods)
        
        print("\n" + "=" * 50)
        print("SEED DỮ LIỆU THÀNH CÔNG!")
        print("=" * 50)
        print(f"\nTổng kết:")
        print(f"  - Users: {len(users)}")
        print(f"  - Sellers: {len(sellers)}")
        print(f"  - Buyers: {len(buyers)}")
        print(f"  - Drivers: {len(drivers)}")
        print(f"  - Categories: {len(categories)}")
        print(f"  - Foods: {len(foods)}")
        print(f"  - Orders: {len(orders)}")
        print(f"\nThông tin đăng nhập:")
        print(f"  - Seller: seller1 / password123")
        print(f"  - Buyer: buyer1 / password123")
        print(f"  - Driver: driver1 / password123")
        print("=" * 50)
        
    except Exception as e:
        db.rollback()
        print(f"\n❌ Lỗi khi seed dữ liệu: {e}")
        import traceback
        traceback.print_exc()
    finally:
        db.close()


if __name__ == "__main__":
    main()

