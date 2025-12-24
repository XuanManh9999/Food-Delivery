from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime, timezone
import uuid
from database import get_db
from auth import get_current_active_user
from models.user import User, UserRole
from models.order import Order
from models.payment import Payment, PaymentMethod, PaymentStatus
from schemas.payment import PaymentCreate, PaymentResponse, PaymentStatusUpdate
from schemas.common import PaginatedResponse

router = APIRouter(prefix="/payments", tags=["Payment"])


def generate_payment_number() -> str:
    """Tạo mã thanh toán duy nhất"""
    return f"PAY-{datetime.now().strftime('%Y%m%d')}-{uuid.uuid4().hex[:8].upper()}"


@router.post("", response_model=PaymentResponse, status_code=status.HTTP_201_CREATED)
async def create_payment(
    payment_data: PaymentCreate,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Tạo thanh toán cho đơn hàng"""
    # Kiểm tra đơn hàng tồn tại
    order = db.query(Order).filter(Order.id == payment_data.order_id).first()
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")
    
    # Kiểm tra quyền (chỉ buyer của đơn hàng mới được thanh toán)
    if current_user.role != UserRole.BUYER or order.buyer_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized to create payment for this order")
    
    # Kiểm tra đơn hàng đã có thanh toán chưa
    existing_payment = db.query(Payment).filter(Payment.order_id == payment_data.order_id).first()
    if existing_payment:
        raise HTTPException(status_code=400, detail="Payment already exists for this order")
    
    # Tạo thanh toán
    db_payment = Payment(
        order_id=payment_data.order_id,
        payment_number=generate_payment_number(),
        payment_method=payment_data.payment_method,
        amount=order.total_amount,
        status=PaymentStatus.PENDING,
        transaction_id=payment_data.transaction_id,
        payment_notes=payment_data.payment_notes
    )
    db.add(db_payment)
    db.commit()
    db.refresh(db_payment)
    return db_payment


@router.get("", response_model=PaginatedResponse[PaymentResponse])
async def get_payments(
    current_user: User = Depends(get_current_active_user),
    order_id: Optional[int] = Query(None, description="Filter by order ID"),
    payment_method: Optional[str] = Query(None, description="Filter by payment method"),
    status: Optional[str] = Query(None, description="Filter by payment status"),
    payment_number: Optional[str] = Query(None, description="Search by payment number"),
    min_amount: Optional[float] = Query(None, description="Minimum amount"),
    max_amount: Optional[float] = Query(None, description="Maximum amount"),
    start_date: Optional[str] = Query(None, description="Start date (YYYY-MM-DD)"),
    end_date: Optional[str] = Query(None, description="End date (YYYY-MM-DD)"),
    sort_by: Optional[str] = Query("created_at", description="Sort by field"),
    sort_order: Optional[str] = Query("desc", description="Sort order (asc, desc)"),
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    page: Optional[int] = Query(None, ge=1),
    db: Session = Depends(get_db)
):
    """Lấy danh sách thanh toán với pagination, search và filter"""
    # Tính skip từ page nếu có
    if page is not None:
        skip = (page - 1) * limit
    
    query = db.query(Payment)
    
    if order_id:
        query = query.filter(Payment.order_id == order_id)
    
    # Buyer chỉ xem thanh toán của đơn hàng mình
    if current_user.role == UserRole.BUYER:
        orders = db.query(Order.id).filter(Order.buyer_id == current_user.id).subquery()
        query = query.filter(Payment.order_id.in_(db.query(orders.c.id)))
    
    # Filters
    if payment_method:
        try:
            method = PaymentMethod(payment_method)
            query = query.filter(Payment.payment_method == method)
        except ValueError:
            raise HTTPException(status_code=400, detail=f"Invalid payment method: {payment_method}")
    
    if status:
        try:
            payment_status = PaymentStatus(status)
            query = query.filter(Payment.status == payment_status)
        except ValueError:
            raise HTTPException(status_code=400, detail=f"Invalid status: {status}")
    
    if payment_number:
        query = query.filter(Payment.payment_number.ilike(f"%{payment_number}%"))
    
    if min_amount is not None:
        query = query.filter(Payment.amount >= min_amount)
    if max_amount is not None:
        query = query.filter(Payment.amount <= max_amount)
    
    if start_date:
        try:
            start_dt = datetime.strptime(start_date, "%Y-%m-%d")
            query = query.filter(Payment.created_at >= start_dt)
        except ValueError:
            raise HTTPException(status_code=400, detail="Invalid start_date format. Use YYYY-MM-DD")
    
    if end_date:
        try:
            end_dt = datetime.strptime(end_date, "%Y-%m-%d")
            end_dt = end_dt.replace(hour=23, minute=59, second=59)
            query = query.filter(Payment.created_at <= end_dt)
        except ValueError:
            raise HTTPException(status_code=400, detail="Invalid end_date format. Use YYYY-MM-DD")
    
    # Get total count
    total = query.count()
    
    # Sorting
    sort_field = getattr(Payment, sort_by, Payment.created_at)
    if sort_order.lower() == "desc":
        query = query.order_by(sort_field.desc())
    else:
        query = query.order_by(sort_field.asc())
    
    # Pagination
    payments = query.offset(skip).limit(limit).all()
    
    return PaginatedResponse.create(payments, total, skip, limit)


@router.get("/{payment_id}", response_model=PaymentResponse)
async def get_payment(
    payment_id: int,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Lấy thông tin chi tiết thanh toán"""
    payment = db.query(Payment).filter(Payment.id == payment_id).first()
    if not payment:
        raise HTTPException(status_code=404, detail="Payment not found")
    
    # Kiểm tra quyền
    if current_user.role == UserRole.BUYER:
        order = db.query(Order).filter(Order.id == payment.order_id).first()
        if order and order.buyer_id != current_user.id:
            raise HTTPException(status_code=403, detail="Not authorized to access this payment")
    
    return payment


@router.patch("/{payment_id}/status", response_model=PaymentResponse)
async def update_payment_status(
    payment_id: int,
    status_data: PaymentStatusUpdate,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Cập nhật trạng thái thanh toán"""
    payment = db.query(Payment).filter(Payment.id == payment_id).first()
    if not payment:
        raise HTTPException(status_code=404, detail="Payment not found")
    
    # Cập nhật trạng thái
    payment.status = status_data.status
    if status_data.transaction_id:
        payment.transaction_id = status_data.transaction_id
    
    # Nếu thanh toán thành công, cập nhật trạng thái đơn hàng
    if status_data.status == PaymentStatus.COMPLETED:
        payment.paid_at = datetime.now(timezone.utc)
        order = db.query(Order).filter(Order.id == payment.order_id).first()
        if order and order.status.value == "pending":
            from models.order import OrderStatus
            order.status = OrderStatus.CONFIRMED
    
    db.commit()
    db.refresh(payment)
    return payment

