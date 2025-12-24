from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    # Database settings
    DB_HOST: str = "localhost"
    DB_PORT: int = 3306
    DB_USER: str = "root"
    DB_PASSWORD: str = ""
    DB_NAME: str = "food_delivery_db"
    
    # JWT settings
    SECRET_KEY: str = "your-secret-key-here-change-in-production"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    
    # Server settings
    HOST: str = "0.0.0.0"
    PORT: int = 8000
    FRONTEND_URL: str = "http://localhost:3000"  # URL của frontend app
    
    # Email settings
    SMTP_HOST: str = "smtp.gmail.com"
    SMTP_PORT: int = 587
    SMTP_USER: str = ""
    SMTP_PASSWORD: str = ""
    SMTP_FROM_EMAIL: str = "noreply@fooddelivery.com"
    SMTP_FROM_NAME: str = "Food Delivery"
    SMTP_USE_TLS: bool = True
    
    # Email verification
    EMAIL_VERIFICATION_EXPIRE_HOURS: int = 24
    
    # Password reset
    PASSWORD_RESET_EXPIRE_HOURS: int = 1
    
    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()

