from datetime import timedelta, datetime, timezone
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from pydantic import EmailStr, BaseModel
from database import get_db
from auth import authenticate_user, create_access_token, get_current_active_user, get_password_hash
from config import settings
from schemas.auth import Token
from schemas.user import UserResponse
from models.user import User
from models.email_token import PasswordResetToken, EmailVerificationToken
from services.email_service import EmailService
from utils.token_utils import generate_reset_token, get_password_reset_expiry, generate_verification_token, get_email_verification_expiry

router = APIRouter(prefix="/auth", tags=["Authentication"])


class ForgotPasswordRequest(BaseModel):
    email: EmailStr


class ResetPasswordRequest(BaseModel):
    email: EmailStr
    new_password: str
    reset_token: str


class VerifyEmailRequest(BaseModel):
    token: str


@router.post("/login", response_model=Token)
async def login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    """Đăng nhập và nhận access token"""
    user = authenticate_user(db, form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    if not user.is_active:
        raise HTTPException(status_code=400, detail="Inactive user")
    
    access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user.username}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}


@router.get("/me", response_model=UserResponse)
async def read_users_me(current_user: User = Depends(get_current_active_user)):
    """Lấy thông tin user hiện tại"""
    return current_user


@router.post("/forgot-password")
async def forgot_password(request: ForgotPasswordRequest, db: Session = Depends(get_db)):
    """Gửi email reset mật khẩu"""
    user = db.query(User).filter(User.email == request.email).first()
    if not user:
        # Không tiết lộ email có tồn tại hay không (security best practice)
        return {
            "message": "If the email exists, a password reset link has been sent.",
            "status": "success"
        }
    
    # Invalidate old tokens for this user
    db.query(PasswordResetToken).filter(
        PasswordResetToken.user_id == user.id,
        PasswordResetToken.is_used == False
    ).update({"is_used": True})
    
    # Generate new reset token
    reset_token = generate_reset_token()
    expires_at = get_password_reset_expiry()
    
    # Save token to database
    db_reset_token = PasswordResetToken(
        user_id=user.id,
        token=reset_token,
        expires_at=expires_at
    )
    db.add(db_reset_token)
    db.commit()
    
    # Send email
    await EmailService.send_password_reset_email(
        email=user.email,
        reset_token=reset_token,
        user_name=user.full_name
    )
    
    return {
        "message": "If the email exists, a password reset link has been sent.",
        "status": "success"
    }


@router.post("/reset-password")
async def reset_password(request: ResetPasswordRequest, db: Session = Depends(get_db)):
    """Reset mật khẩu với token"""
    # Find reset token
    reset_token_obj = db.query(PasswordResetToken).filter(
        PasswordResetToken.token == request.reset_token,
        PasswordResetToken.is_used == False
    ).first()
    
    if not reset_token_obj:
        raise HTTPException(
            status_code=400,
            detail="Invalid or expired reset token"
        )
    
    # Check if token expired
    if reset_token_obj.expires_at < datetime.now(timezone.utc):
        reset_token_obj.is_used = True
        db.commit()
        raise HTTPException(
            status_code=400,
            detail="Reset token has expired. Please request a new one."
        )
    
    # Verify email matches
    user = db.query(User).filter(User.id == reset_token_obj.user_id).first()
    if not user or user.email != request.email:
        raise HTTPException(
            status_code=400,
            detail="Email does not match the reset token"
        )
    
    # Update password
    user.hashed_password = get_password_hash(request.new_password)
    
    # Mark token as used
    reset_token_obj.is_used = True
    
    db.commit()
    
    return {
        "message": "Password has been reset successfully",
        "status": "success"
    }


@router.post("/verify-email")
async def verify_email(request: VerifyEmailRequest, db: Session = Depends(get_db)):
    """Xác thực email với token"""
    verification_token_obj = db.query(EmailVerificationToken).filter(
        EmailVerificationToken.token == request.token,
        EmailVerificationToken.is_used == False
    ).first()
    
    if not verification_token_obj:
        raise HTTPException(
            status_code=400,
            detail="Invalid or expired verification token"
        )
    
    # Check if token expired
    if verification_token_obj.expires_at < datetime.now(timezone.utc):
        verification_token_obj.is_used = True
        db.commit()
        raise HTTPException(
            status_code=400,
            detail="Verification token has expired. Please request a new one."
        )
    
    # Verify user email
    user = db.query(User).filter(User.id == verification_token_obj.user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    # Mark email as verified
    user.is_verified = True
    verification_token_obj.is_used = True
    
    db.commit()
    
    return {
        "message": "Email verified successfully",
        "status": "success"
    }


@router.post("/resend-verification")
async def resend_verification_email(
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    """Gửi lại email xác thực"""
    if current_user.is_verified:
        raise HTTPException(
            status_code=400,
            detail="Email is already verified"
        )
    
    # Invalidate old tokens
    db.query(EmailVerificationToken).filter(
        EmailVerificationToken.user_id == current_user.id,
        EmailVerificationToken.is_used == False
    ).update({"is_used": True})
    
    # Generate new verification token
    verification_token = generate_verification_token()
    expires_at = get_email_verification_expiry()
    
    # Save token to database
    db_verification_token = EmailVerificationToken(
        user_id=current_user.id,
        token=verification_token,
        expires_at=expires_at
    )
    db.add(db_verification_token)
    db.commit()
    
    # Send email
    await EmailService.send_verification_email(
        email=current_user.email,
        verification_token=verification_token,
        user_name=current_user.full_name
    )
    
    return {
        "message": "Verification email has been sent",
        "status": "success"
    }
