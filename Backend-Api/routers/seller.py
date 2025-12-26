from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from database import get_db
from models.user import Seller, User, UserRole
from schemas.user import SellerResponse
from pydantic import BaseModel

router = APIRouter(prefix="/sellers", tags=["Seller"])


class SellerListResponse(BaseModel):
    id: int
    user_id: int
    store_name: str
    store_address: str
    store_phone: Optional[str]
    store_description: Optional[str]
    rating: float
    total_orders: int
    user: dict  # Basic user info
    
    class Config:
        from_attributes = True


@router.get("", response_model=List[SellerListResponse])
async def get_sellers(
    skip: int = Query(0, ge=0, description="Number of records to skip"),
    limit: int = Query(20, ge=1, le=100, description="Number of records to return"),
    search: Optional[str] = Query(None, description="Search by store name"),
    min_rating: Optional[float] = Query(None, description="Minimum rating"),
    db: Session = Depends(get_db)
):
    """Lấy danh sách sellers/restaurants"""
    query = db.query(Seller).join(User).filter(User.is_active == True)
    
    # Search filter
    if search:
        search_term = f"%{search}%"
        query = query.filter(Seller.store_name.ilike(search_term))
    
    # Rating filter
    if min_rating is not None:
        query = query.filter(Seller.rating >= min_rating)
    
    # Get total count
    total = query.count()
    
    # Order by rating and total_orders
    sellers = query.order_by(Seller.rating.desc(), Seller.total_orders.desc()).offset(skip).limit(limit).all()
    
    # Format response
    result = []
    for seller in sellers:
        user = db.query(User).filter(User.id == seller.user_id).first()
        result.append({
            "id": seller.id,
            "user_id": seller.user_id,
            "store_name": seller.store_name,
            "store_address": seller.store_address,
            "store_phone": seller.store_phone,
            "store_description": seller.store_description,
            "rating": float(seller.rating) if seller.rating is not None else 0.0,
            "total_orders": seller.total_orders,
            "user": {
                "id": user.id,
                "full_name": user.full_name,
                "phone_number": user.phone_number
            } if user else {}
        })
    
    return result


@router.get("/{seller_id}", response_model=SellerListResponse)
async def get_seller(seller_id: int, db: Session = Depends(get_db)):
    """Lấy thông tin chi tiết seller"""
    seller = db.query(Seller).filter(Seller.id == seller_id).first()
    if not seller:
        raise HTTPException(status_code=404, detail="Seller not found")
    
    user = db.query(User).filter(User.id == seller.user_id).first()
    return {
        "id": seller.id,
        "user_id": seller.user_id,
        "store_name": seller.store_name,
        "store_address": seller.store_address,
        "store_phone": seller.store_phone,
        "store_description": seller.store_description,
        "rating": float(seller.rating) if seller.rating is not None else 0.0,
        "total_orders": seller.total_orders,
        "user": {
            "id": user.id,
            "full_name": user.full_name,
            "phone_number": user.phone_number
        } if user else {}
    }

