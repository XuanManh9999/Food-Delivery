from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routers import auth, registration, food, order, payment, seller
from database import engine, Base
from config import settings

# Tạo database tables
Base.metadata.create_all(bind=engine)

# Khởi tạo FastAPI app
app = FastAPI(
    title="Food Delivery API",
    description="API cho ứng dụng giao đồ ăn với FastAPI và MySQL",
    version="1.0.0"
)

# Cấu hình CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Trong production nên chỉ định domain cụ thể
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Đăng ký routers
app.include_router(auth.router, prefix="/api")
app.include_router(registration.router, prefix="/api")
app.include_router(food.router, prefix="/api")
app.include_router(order.router, prefix="/api")
app.include_router(payment.router, prefix="/api")
app.include_router(seller.router, prefix="/api")


@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "message": "Food Delivery API",
        "version": "1.0.0",
        "docs": "/docs"
    }


@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy"}

