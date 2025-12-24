from sqlalchemy import Column, Integer, String, Boolean, DateTime, Enum as SQLEnum, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
import enum
from database import Base


class UserRole(str, enum.Enum):
    SELLER = "seller"
    BUYER = "buyer"
    DRIVER = "driver"


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, index=True, nullable=False)
    username = Column(String(100), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    full_name = Column(String(255), nullable=False)
    phone_number = Column(String(20), nullable=False)
    role = Column(SQLEnum(UserRole), nullable=False)
    is_active = Column(Boolean, default=True)
    is_verified = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    seller_profile = relationship("Seller", back_populates="user", uselist=False, cascade="all, delete-orphan")
    buyer_profile = relationship("Buyer", back_populates="user", uselist=False, cascade="all, delete-orphan")
    driver_profile = relationship("Driver", back_populates="user", uselist=False, cascade="all, delete-orphan")
    orders = relationship("Order", back_populates="buyer_user", cascade="all, delete-orphan")


class Seller(Base):
    __tablename__ = "sellers"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True, nullable=False)
    store_name = Column(String(255), nullable=False)
    store_address = Column(String(500), nullable=False)
    store_phone = Column(String(20))
    store_description = Column(String(1000))
    license_number = Column(String(100))
    rating = Column(Integer, default=0)
    total_orders = Column(Integer, default=0)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    user = relationship("User", back_populates="seller_profile", foreign_keys=[user_id])
    foods = relationship("Food", back_populates="seller", cascade="all, delete-orphan")
    orders = relationship("Order", back_populates="seller", cascade="all, delete-orphan")


class Buyer(Base):
    __tablename__ = "buyers"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True, nullable=False)
    address = Column(String(500))
    default_payment_method = Column(String(50))
    total_orders = Column(Integer, default=0)
    total_spent = Column(Integer, default=0)  # Tổng tiền đã chi
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    user = relationship("User", back_populates="buyer_profile", foreign_keys=[user_id])


class Driver(Base):
    __tablename__ = "drivers"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True, nullable=False)
    license_number = Column(String(100), nullable=False)
    vehicle_type = Column(String(50))  # xe máy, xe đạp, etc.
    vehicle_number = Column(String(50))
    is_available = Column(Boolean, default=True)
    current_location_lat = Column(String(50))
    current_location_lng = Column(String(50))
    rating = Column(Integer, default=0)
    total_deliveries = Column(Integer, default=0)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    user = relationship("User", back_populates="driver_profile", foreign_keys=[user_id])
    orders = relationship("Order", back_populates="driver", cascade="all, delete-orphan")

