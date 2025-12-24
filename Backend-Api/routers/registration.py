from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from database import get_db
from auth import get_password_hash
from models.user import User, Seller, Buyer, Driver, UserRole
from models.email_token import EmailVerificationToken
from schemas.user import SellerCreate, SellerResponse, BuyerCreate, BuyerResponse, DriverCreate, DriverResponse
from services.email_service import EmailService
from utils.token_utils import generate_verification_token, get_email_verification_expiry

router = APIRouter(prefix="/register", tags=["Registration"])


@router.post("/seller", response_model=SellerResponse, status_code=status.HTTP_201_CREATED)
async def register_seller(seller_data: SellerCreate, db: Session = Depends(get_db)):
    """Đăng ký tài khoản Seller"""
    # Kiểm tra email đã tồn tại
    if db.query(User).filter(User.email == seller_data.email).first():
        raise HTTPException(status_code=400, detail="Email already registered")
    
    # Kiểm tra username đã tồn tại
    if db.query(User).filter(User.username == seller_data.username).first():
        raise HTTPException(status_code=400, detail="Username already taken")
    
    # Tạo user
    db_user = User(
        email=seller_data.email,
        username=seller_data.username,
        hashed_password=get_password_hash(seller_data.password),
        full_name=seller_data.full_name,
        phone_number=seller_data.phone_number,
        role=UserRole.SELLER
    )
    db.add(db_user)
    db.flush()  # Để lấy user.id
    
    # Tạo seller profile
    db_seller = Seller(
        user_id=db_user.id,
        store_name=seller_data.store_name,
        store_address=seller_data.store_address,
        store_phone=seller_data.store_phone,
        store_description=seller_data.store_description,
        license_number=seller_data.license_number
    )
    db.add(db_seller)
    
    # Create email verification token
    verification_token = generate_verification_token()
    expires_at = get_email_verification_expiry()
    db_verification_token = EmailVerificationToken(
        user_id=db_user.id,
        token=verification_token,
        expires_at=expires_at
    )
    db.add(db_verification_token)
    
    db.commit()
    db.refresh(db_seller)
    db.refresh(db_user)
    
    # Send verification email (async, don't wait for it)
    await EmailService.send_verification_email(
        email=db_user.email,
        verification_token=verification_token,
        user_name=db_user.full_name
    )
    
    db_seller.user = db_user
    return db_seller


@router.post("/buyer", response_model=BuyerResponse, status_code=status.HTTP_201_CREATED)
async def register_buyer(buyer_data: BuyerCreate, db: Session = Depends(get_db)):
    """Đăng ký tài khoản Buyer"""
    # Kiểm tra email đã tồn tại
    if db.query(User).filter(User.email == buyer_data.email).first():
        raise HTTPException(status_code=400, detail="Email already registered")
    
    # Kiểm tra username đã tồn tại
    if db.query(User).filter(User.username == buyer_data.username).first():
        raise HTTPException(status_code=400, detail="Username already taken")
    
    # Tạo user
    db_user = User(
        email=buyer_data.email,
        username=buyer_data.username,
        hashed_password=get_password_hash(buyer_data.password),
        full_name=buyer_data.full_name,
        phone_number=buyer_data.phone_number,
        role=UserRole.BUYER
    )
    db.add(db_user)
    db.flush()
    
    # Tạo buyer profile
    db_buyer = Buyer(
        user_id=db_user.id,
        address=buyer_data.address
    )
    db.add(db_buyer)
    
    # Create email verification token
    verification_token = generate_verification_token()
    expires_at = get_email_verification_expiry()
    db_verification_token = EmailVerificationToken(
        user_id=db_user.id,
        token=verification_token,
        expires_at=expires_at
    )
    db.add(db_verification_token)
    
    db.commit()
    db.refresh(db_buyer)
    db.refresh(db_user)
    
    # Send verification email (async, don't wait for it)
    await EmailService.send_verification_email(
        email=db_user.email,
        verification_token=verification_token,
        user_name=db_user.full_name
    )
    
    db_buyer.user = db_user
    return db_buyer


@router.post("/driver", response_model=DriverResponse, status_code=status.HTTP_201_CREATED)
async def register_driver(driver_data: DriverCreate, db: Session = Depends(get_db)):
    """Đăng ký tài khoản Driver"""
    # Kiểm tra email đã tồn tại
    if db.query(User).filter(User.email == driver_data.email).first():
        raise HTTPException(status_code=400, detail="Email already registered")
    
    # Kiểm tra username đã tồn tại
    if db.query(User).filter(User.username == driver_data.username).first():
        raise HTTPException(status_code=400, detail="Username already taken")
    
    # Tạo user
    db_user = User(
        email=driver_data.email,
        username=driver_data.username,
        hashed_password=get_password_hash(driver_data.password),
        full_name=driver_data.full_name,
        phone_number=driver_data.phone_number,
        role=UserRole.DRIVER
    )
    db.add(db_user)
    db.flush()
    
    # Tạo driver profile
    db_driver = Driver(
        user_id=db_user.id,
        license_number=driver_data.license_number,
        vehicle_type=driver_data.vehicle_type,
        vehicle_number=driver_data.vehicle_number
    )
    db.add(db_driver)
    
    # Create email verification token
    verification_token = generate_verification_token()
    expires_at = get_email_verification_expiry()
    db_verification_token = EmailVerificationToken(
        user_id=db_user.id,
        token=verification_token,
        expires_at=expires_at
    )
    db.add(db_verification_token)
    
    db.commit()
    db.refresh(db_driver)
    db.refresh(db_user)
    
    # Send verification email (async, don't wait for it)
    await EmailService.send_verification_email(
        email=db_user.email,
        verification_token=verification_token,
        user_name=db_user.full_name
    )
    
    db_driver.user = db_user
    return db_driver

