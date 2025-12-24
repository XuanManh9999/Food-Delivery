from pydantic import BaseModel
from typing import Optional
from datetime import datetime


class FoodCategoryCreate(BaseModel):
    name: str
    description: Optional[str] = None


class FoodCategoryResponse(BaseModel):
    id: int
    name: str
    description: Optional[str]
    created_at: datetime

    class Config:
        from_attributes = True


class FoodCreate(BaseModel):
    name: str
    description: Optional[str] = None
    price: float
    image_url: Optional[str] = None
    category_id: Optional[int] = None
    stock_quantity: int = 0


class FoodUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    price: Optional[float] = None
    image_url: Optional[str] = None
    category_id: Optional[int] = None
    is_available: Optional[bool] = None
    stock_quantity: Optional[int] = None


class FoodResponse(BaseModel):
    id: int
    seller_id: int
    category_id: Optional[int]
    name: str
    description: Optional[str]
    price: float
    image_url: Optional[str]
    is_available: bool
    stock_quantity: int
    rating: float
    total_orders: int
    created_at: datetime
    updated_at: Optional[datetime]

    class Config:
        from_attributes = True

