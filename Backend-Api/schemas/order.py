from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
from models.order import OrderStatus


class OrderItemCreate(BaseModel):
    food_id: int
    quantity: int


class OrderItemResponse(BaseModel):
    id: int
    food_id: int
    quantity: int
    unit_price: float
    subtotal: float

    class Config:
        from_attributes = True


class OrderCreate(BaseModel):
    seller_id: int
    items: List[OrderItemCreate]
    delivery_address: str
    delivery_phone: str
    delivery_notes: Optional[str] = None
    delivery_fee: float = 0.0


class OrderResponse(BaseModel):
    id: int
    buyer_id: int
    seller_id: int
    driver_id: Optional[int]
    order_number: str
    status: OrderStatus
    subtotal: float
    delivery_fee: float
    total_amount: float
    delivery_address: str
    delivery_phone: str
    delivery_notes: Optional[str]
    created_at: datetime
    updated_at: Optional[datetime]
    delivered_at: Optional[datetime]
    items: List[OrderItemResponse]

    class Config:
        from_attributes = True


class OrderStatusUpdate(BaseModel):
    status: OrderStatus

