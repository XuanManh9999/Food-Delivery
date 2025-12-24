from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from sqlalchemy import or_, func
from typing import List, Optional
from database import get_db
from auth import get_current_active_user
from models.user import User, UserRole, Seller
from models.food import Food, FoodCategory
from schemas.food import FoodCreate, FoodUpdate, FoodResponse, FoodCategoryCreate, FoodCategoryResponse
from schemas.common import PaginatedResponse, PaginationParams

router = APIRouter(prefix="/foods", tags=["Food"])


def get_current_seller(current_user: User = Depends(get_current_active_user), db: Session = Depends(get_db)):
    """Kiểm tra user là seller"""
    if current_user.role != UserRole.SELLER:
        raise HTTPException(status_code=403, detail="Only sellers can access this endpoint")
    seller = db.query(Seller).filter(Seller.user_id == current_user.id).first()
    if not seller:
        raise HTTPException(status_code=404, detail="Seller profile not found")
    return seller


@router.post("/categories", response_model=FoodCategoryResponse, status_code=status.HTTP_201_CREATED)
async def create_category(category_data: FoodCategoryCreate, db: Session = Depends(get_db)):
    """Tạo danh mục đồ ăn"""
    db_category = FoodCategory(**category_data.dict())
    db.add(db_category)
    db.commit()
    db.refresh(db_category)
    return db_category


@router.get("/categories", response_model=List[FoodCategoryResponse])
async def get_categories(db: Session = Depends(get_db)):
    """Lấy danh sách danh mục đồ ăn"""
    categories = db.query(FoodCategory).all()
    return categories


@router.post("", response_model=FoodResponse, status_code=status.HTTP_201_CREATED)
async def create_food(
    food_data: FoodCreate,
    current_seller: Seller = Depends(get_current_seller),
    db: Session = Depends(get_db)
):
    """Đăng đồ ăn mới (chỉ Seller)"""
    db_food = Food(
        seller_id=current_seller.id,
        **food_data.dict()
    )
    db.add(db_food)
    db.commit()
    db.refresh(db_food)
    return db_food


@router.get("", response_model=PaginatedResponse[FoodResponse])
async def get_foods(
    seller_id: Optional[int] = Query(None, description="Filter by seller ID"),
    category_id: Optional[int] = Query(None, description="Filter by category ID"),
    is_available: Optional[bool] = Query(None, description="Filter by availability"),
    search: Optional[str] = Query(None, description="Search by name or description"),
    min_price: Optional[float] = Query(None, description="Minimum price"),
    max_price: Optional[float] = Query(None, description="Maximum price"),
    min_rating: Optional[float] = Query(None, description="Minimum rating"),
    sort_by: Optional[str] = Query("created_at", description="Sort by field (name, price, rating, created_at)"),
    sort_order: Optional[str] = Query("desc", description="Sort order (asc, desc)"),
    skip: int = Query(0, ge=0, description="Number of records to skip"),
    limit: int = Query(20, ge=1, le=100, description="Number of records to return"),
    page: Optional[int] = Query(None, ge=1, description="Page number (alternative to skip)"),
    db: Session = Depends(get_db)
):
    """Lấy danh sách đồ ăn với pagination, search và filter"""
    # Tính skip từ page nếu có
    if page is not None:
        skip = (page - 1) * limit
    
    # Base query
    query = db.query(Food)
    
    # Filters
    if seller_id:
        query = query.filter(Food.seller_id == seller_id)
    if category_id:
        query = query.filter(Food.category_id == category_id)
    if is_available is not None:
        query = query.filter(Food.is_available == is_available)
    if min_price is not None:
        query = query.filter(Food.price >= min_price)
    if max_price is not None:
        query = query.filter(Food.price <= max_price)
    if min_rating is not None:
        query = query.filter(Food.rating >= min_rating)
    
    # Search
    if search:
        search_term = f"%{search}%"
        query = query.filter(
            or_(
                Food.name.ilike(search_term),
                Food.description.ilike(search_term)
            )
        )
    
    # Get total count before pagination
    total = query.count()
    
    # Sorting
    sort_field = getattr(Food, sort_by, Food.created_at)
    if sort_order.lower() == "desc":
        query = query.order_by(sort_field.desc())
    else:
        query = query.order_by(sort_field.asc())
    
    # Pagination
    foods = query.offset(skip).limit(limit).all()
    
    return PaginatedResponse.create(foods, total, skip, limit)


@router.get("/{food_id}", response_model=FoodResponse)
async def get_food(food_id: int, db: Session = Depends(get_db)):
    """Lấy thông tin chi tiết đồ ăn"""
    food = db.query(Food).filter(Food.id == food_id).first()
    if not food:
        raise HTTPException(status_code=404, detail="Food not found")
    return food


@router.put("/{food_id}", response_model=FoodResponse)
async def update_food(
    food_id: int,
    food_data: FoodUpdate,
    current_seller: Seller = Depends(get_current_seller),
    db: Session = Depends(get_db)
):
    """Cập nhật thông tin đồ ăn (chỉ Seller sở hữu)"""
    food = db.query(Food).filter(Food.id == food_id, Food.seller_id == current_seller.id).first()
    if not food:
        raise HTTPException(status_code=404, detail="Food not found")
    
    update_data = food_data.dict(exclude_unset=True)
    for field, value in update_data.items():
        setattr(food, field, value)
    
    db.commit()
    db.refresh(food)
    return food


@router.delete("/{food_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_food(
    food_id: int,
    current_seller: Seller = Depends(get_current_seller),
    db: Session = Depends(get_db)
):
    """Xóa đồ ăn (chỉ Seller sở hữu)"""
    food = db.query(Food).filter(Food.id == food_id, Food.seller_id == current_seller.id).first()
    if not food:
        raise HTTPException(status_code=404, detail="Food not found")
    
    db.delete(food)
    db.commit()
    return None

