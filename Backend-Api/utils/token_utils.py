import secrets
from datetime import datetime, timedelta, timezone
from config import settings


def generate_reset_token() -> str:
    """Tạo token ngẫu nhiên cho password reset"""
    return secrets.token_urlsafe(32)


def generate_verification_token() -> str:
    """Tạo token ngẫu nhiên cho email verification"""
    return secrets.token_urlsafe(32)


def get_password_reset_expiry() -> datetime:
    """Lấy thời gian hết hạn cho password reset token"""
    return datetime.now(timezone.utc) + timedelta(hours=settings.PASSWORD_RESET_EXPIRE_HOURS)


def get_email_verification_expiry() -> datetime:
    """Lấy thời gian hết hạn cho email verification token"""
    return datetime.now(timezone.utc) + timedelta(hours=settings.EMAIL_VERIFICATION_EXPIRE_HOURS)

