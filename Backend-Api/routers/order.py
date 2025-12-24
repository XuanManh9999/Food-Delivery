from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime, timezone
import uuid
from database import get_db
from auth import get_current_active_user
from models.user import User, UserRole, Buyer
from models.food import Food
from models.order import Order, OrderItem, OrderStatus
from schemas.order import OrderCreate, OrderResponse, OrderStatusUpdate
from schemas.common import PaginatedResponse
from services.email_service import EmailService

router = APIRouter(prefix="/orders", tags=["Order"])


def get_current_buyer(current_user: User = Depends(get_current_active_user), db: Session = Depends(get_db)):
    """Kiểm tra user là buyer"""
    if current_user.role != UserRole.BUYER:
        raise HTTPException(status_code=403, detail="Only buyers can access this endpoint")
    buyer = db.query(Buyer).filter(Buyer.user_id == current_user.id).first()
    if not buyer:
        raise HTTPException(status_code=404, detail="Buyer profile not found")
    return buyer


def generate_order_number() -> str:
    """Tạo mã đơn hàng duy nhất"""
    return f"ORD-{datetime.now().strftime('%Y%m%d')}-{uuid.uuid4().hex[:8].upper()}"


@router.post("", response_model=OrderResponse, status_code=status.HTTP_201_CREATED)
async def create_order(
    order_data: OrderCreate,
    current_buyer: Buyer = Depends(get_current_buyer),
    db: Session = Depends(get_db)
):
    """Tạo đơn hàng mới (chỉ Buyer)"""
    # Tính toán subtotal
    subtotal = 0.0
    order_items_data = []
    
    for item_data in order_data.items:
        food = db.query(Food).filter(Food.id == item_data.food_id).first()
        if not food:
            raise HTTPException(status_code=404, detail=f"Food with id {item_data.food_id} not found")
        
        if not food.is_available:
            raise HTTPException(status_code=400, detail=f"Food {food.name} is not available")
        
        if food.stock_quantity < item_data.quantity:
            raise HTTPException(status_code=400, detail=f"Insufficient stock for {food.name}")
        
        item_subtotal = food.price * item_data.quantity
        subtotal += item_subtotal
        
        order_items_data.append({
            "food": food,
            "quantity": item_data.quantity,
            "unit_price": food.price,
            "subtotal": item_subtotal
        })
    
    total_amount = subtotal + order_data.delivery_fee
    
    # Tạo đơn hàng
    db_order = Order(
        buyer_id=current_buyer.user_id,
        seller_id=order_data.seller_id,
        order_number=generate_order_number(),
        status=OrderStatus.PENDING,
        subtotal=subtotal,
        delivery_fee=order_data.delivery_fee,
        total_amount=total_amount,
        delivery_address=order_data.delivery_address,
        delivery_phone=order_data.delivery_phone,
        delivery_notes=order_data.delivery_notes
    )
    db.add(db_order)
    db.flush()
    
    # Tạo order items
    for item_data in order_items_data:
        db_order_item = OrderItem(
            order_id=db_order.id,
            food_id=item_data["food"].id,
            quantity=item_data["quantity"],
            unit_price=item_data["unit_price"],
            subtotal=item_data["subtotal"]
        )
        db.add(db_order_item)
        
        # Cập nhật stock
        item_data["food"].stock_quantity -= item_data["quantity"]
    
    # Cập nhật thống kê buyer
    current_buyer.total_orders += 1
    current_buyer.total_spent += int(total_amount)
    
    db.commit()
    db.refresh(db_order)
    
    # Send order confirmation email (async, don't wait)
    user = db.query(User).filter(User.id == current_buyer.user_id).first()
    if user:
        await EmailService.send_order_confirmation_email(
            email=user.email,
            order_number=db_order.order_number,
            total_amount=total_amount,
            user_name=user.full_name
        )
    
    return db_order


@router.get("", response_model=PaginatedResponse[OrderResponse])
async def get_orders(
    current_user: User = Depends(get_current_active_user),
    status: Optional[str] = Query(None, description="Filter by order status"),
    order_number: Optional[str] = Query(None, description="Search by order number"),
    min_amount: Optional[float] = Query(None, description="Minimum total amount"),
    max_amount: Optional[float] = Query(None, description="Maximum total amount"),
    start_date: Optional[str] = Query(None, description="Start date (YYYY-MM-DD)"),
    end_date: Optional[str] = Query(None, description="End date (YYYY-MM-DD)"),
    sort_by: Optional[str] = Query("created_at", description="Sort by field"),
    sort_order: Optional[str] = Query("desc", description="Sort order (asc, desc)"),
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    page: Optional[int] = Query(None, ge=1),
    db: Session = Depends(get_db)
):
    """Lấy danh sách đơn hàng với pagination, search và filter"""
    # Tính skip từ page nếu có
    if page is not None:
        skip = (page - 1) * limit
    
    query = db.query(Order)
    
    # Buyer chỉ xem đơn hàng của mình
    if current_user.role == UserRole.BUYER:
        query = query.filter(Order.buyer_id == current_user.id)
    # Seller chỉ xem đơn hàng của cửa hàng mình
    elif current_user.role == UserRole.SELLER:
        from models.user import Seller
        seller = db.query(Seller).filter(Seller.user_id == current_user.id).first()
        if seller:
            query = query.filter(Order.seller_id == seller.id)
    
    # Filters
    if status:
        try:
            order_status = OrderStatus(status)
            query = query.filter(Order.status == order_status)
        except ValueError:
            raise HTTPException(status_code=400, detail=f"Invalid status: {status}")
    
    if order_number:
        query = query.filter(Order.order_number.ilike(f"%{order_number}%"))
    
    if min_amount is not None:
        query = query.filter(Order.total_amount >= min_amount)
    if max_amount is not None:
        query = query.filter(Order.total_amount <= max_amount)
    
    if start_date:
        try:
            start_dt = datetime.strptime(start_date, "%Y-%m-%d")
            query = query.filter(Order.created_at >= start_dt)
        except ValueError:
            raise HTTPException(status_code=400, detail="Invalid start_date format. Use YYYY-MM-DD")
    
    if end_date:
        try:
            end_dt = datetime.strptime(end_date, "%Y-%m-%d")
            # Include the entire end date
            end_dt = end_dt.replace(hour=23, minute=59, second=59)
            query = query.filter(Order.created_at <= end_dt)
        except ValueError:
            raise HTTPException(status_code=400, detail="Invalid end_date format. Use YYYY-MM-DD")
    
    # Get total count
    total = query.count()
    
    # Sorting
    sort_field = getattr(Order, sort_by, Order.created_at)
    if sort_order.lower() == "desc":
        query = query.order_by(sort_field.desc())
    else:
        query = query.order_by(sort_field.asc())
    
    # Pagination
    orders = query.offset(skip).limit(limit).all()
    
    return PaginatedResponse.create(orders, total, skip, limit)


@router.get("/{order_id}", response_model=OrderResponse)
async def get_order(
    order_id: int,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Lấy thông tin chi tiết đơn hàng"""
    order = db.query(Order).filter(Order.id == order_id).first()
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")
    
    # Kiểm tra quyền truy cập
    if current_user.role == UserRole.BUYER and order.buyer_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized to access this order")
    elif current_user.role == UserRole.SELLER:
        from models.user import Seller
        seller = db.query(Seller).filter(Seller.user_id == current_user.id).first()
        if seller and order.seller_id != seller.id:
            raise HTTPException(status_code=403, detail="Not authorized to access this order")
    
    return order


@router.patch("/{order_id}/status", response_model=OrderResponse)
async def update_order_status(
    order_id: int,
    status_data: OrderStatusUpdate,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Cập nhật trạng thái đơn hàng"""
    order = db.query(Order).filter(Order.id == order_id).first()
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")
    
    # Kiểm tra quyền
    if current_user.role == UserRole.SELLER:
        from models.user import Seller
        seller = db.query(Seller).filter(Seller.user_id == current_user.id).first()
        if seller and order.seller_id != seller.id:
            raise HTTPException(status_code=403, detail="Not authorized")
    elif current_user.role == UserRole.BUYER:
        if order.buyer_id != current_user.id:
            raise HTTPException(status_code=403, detail="Not authorized")
        # Buyer chỉ có thể hủy đơn hàng
        if status_data.status != OrderStatus.CANCELLED:
            raise HTTPException(status_code=403, detail="Buyers can only cancel orders")
    
    order.status = status_data.status
    if status_data.status == OrderStatus.DELIVERED:
        order.delivered_at = datetime.now(timezone.utc)
    
    db.commit()
    db.refresh(order)
    
    # Send order status update email (async, don't wait)
    user = db.query(User).filter(User.id == order.buyer_id).first()
    if user:
        await EmailService.send_order_status_update_email(
            email=user.email,
            order_number=order.order_number,
            status=status_data.status.value,
            user_name=user.full_name
        )
    
    return order

