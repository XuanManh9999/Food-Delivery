from pydantic import BaseModel
from typing import List, Generic, TypeVar, Optional

T = TypeVar('T')


class PaginationParams(BaseModel):
    """Parameters cho pagination"""
    skip: int = 0
    limit: int = 20
    page: Optional[int] = None
    
    def __init__(self, **data):
        super().__init__(**data)
        # Nếu có page thì tính skip
        if self.page is not None and self.page > 0:
            self.skip = (self.page - 1) * self.limit


class PaginatedResponse(BaseModel, Generic[T]):
    """Response chuẩn cho pagination"""
    items: List[T]
    total: int
    page: int
    page_size: int
    total_pages: int
    
    @classmethod
    def create(cls, items: List[T], total: int, skip: int, limit: int):
        page = (skip // limit) + 1 if limit > 0 else 1
        total_pages = (total + limit - 1) // limit if limit > 0 else 1
        return cls(
            items=items,
            total=total,
            page=page,
            page_size=limit,
            total_pages=total_pages
        )


class SearchFilterParams(BaseModel):
    """Parameters chung cho search và filter"""
    search: Optional[str] = None
    skip: int = 0
    limit: int = 20
    page: Optional[int] = None
    sort_by: Optional[str] = None
    sort_order: Optional[str] = "asc"  # asc or desc

