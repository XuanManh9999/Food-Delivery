from pydantic import BaseModel
from typing import Optional
from datetime import datetime
from models.payment import PaymentMethod, PaymentStatus


class PaymentCreate(BaseModel):
    order_id: int
    payment_method: PaymentMethod
    transaction_id: Optional[str] = None
    payment_notes: Optional[str] = None


class PaymentResponse(BaseModel):
    id: int
    order_id: int
    payment_number: str
    payment_method: PaymentMethod
    amount: float
    status: PaymentStatus
    transaction_id: Optional[str]
    payment_notes: Optional[str]
    paid_at: Optional[datetime]
    created_at: datetime
    updated_at: Optional[datetime]

    class Config:
        from_attributes = True


class PaymentStatusUpdate(BaseModel):
    status: PaymentStatus
    transaction_id: Optional[str] = None

