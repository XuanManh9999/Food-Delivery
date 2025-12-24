from pydantic import BaseModel, EmailStr
from typing import Optional
from datetime import datetime
from models.user import UserRole


class UserBase(BaseModel):
    email: EmailStr
    username: str
    full_name: str
    phone_number: str


class UserCreate(UserBase):
    password: str
    role: UserRole


class UserResponse(UserBase):
    id: int
    role: UserRole
    is_active: bool
    is_verified: bool
    created_at: datetime

    class Config:
        from_attributes = True


class SellerCreate(BaseModel):
    email: EmailStr
    username: str
    password: str
    full_name: str
    phone_number: str
    store_name: str
    store_address: str
    store_phone: Optional[str] = None
    store_description: Optional[str] = None
    license_number: Optional[str] = None


class SellerResponse(BaseModel):
    id: int
    user_id: int
    store_name: str
    store_address: str
    store_phone: Optional[str]
    store_description: Optional[str]
    license_number: Optional[str]
    rating: int
    total_orders: int
    created_at: datetime
    user: UserResponse

    class Config:
        from_attributes = True


class BuyerCreate(BaseModel):
    email: EmailStr
    username: str
    password: str
    full_name: str
    phone_number: str
    address: Optional[str] = None


class BuyerResponse(BaseModel):
    id: int
    user_id: int
    address: Optional[str]
    default_payment_method: Optional[str]
    total_orders: int
    total_spent: int
    created_at: datetime
    user: UserResponse

    class Config:
        from_attributes = True


class DriverCreate(BaseModel):
    email: EmailStr
    username: str
    password: str
    full_name: str
    phone_number: str
    license_number: str
    vehicle_type: Optional[str] = None
    vehicle_number: Optional[str] = None


class DriverResponse(BaseModel):
    id: int
    user_id: int
    license_number: str
    vehicle_type: Optional[str]
    vehicle_number: Optional[str]
    is_available: bool
    rating: int
    total_deliveries: int
    created_at: datetime
    user: UserResponse

    class Config:
        from_attributes = True

