import aiosmtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from jinja2 import Template
from typing import Optional
from config import settings
import logging

logger = logging.getLogger(__name__)


class EmailService:
    """Service để gửi email"""
    
    @staticmethod
    async def send_email(
        to_email: str,
        subject: str,
        html_content: str,
        text_content: Optional[str] = None
    ) -> bool:
        """
        Gửi email
        
        Args:
            to_email: Email người nhận
            subject: Tiêu đề email
            html_content: Nội dung HTML
            text_content: Nội dung text (optional)
        
        Returns:
            True nếu gửi thành công, False nếu thất bại
        """
        if not settings.SMTP_USER or not settings.SMTP_PASSWORD:
            logger.warning("SMTP credentials not configured. Email will not be sent.")
            return False
        
        try:
            message = MIMEMultipart("alternative")
            message["Subject"] = subject
            message["From"] = f"{settings.SMTP_FROM_NAME} <{settings.SMTP_FROM_EMAIL}>"
            message["To"] = to_email
            
            # Add text content if provided
            if text_content:
                text_part = MIMEText(text_content, "plain")
                message.attach(text_part)
            
            # Add HTML content
            html_part = MIMEText(html_content, "html")
            message.attach(html_part)
            
            # Send email
            await aiosmtplib.send(
                message,
                hostname=settings.SMTP_HOST,
                port=settings.SMTP_PORT,
                username=settings.SMTP_USER,
                password=settings.SMTP_PASSWORD,
                use_tls=settings.SMTP_USE_TLS,
            )
            
            logger.info(f"Email sent successfully to {to_email}")
            return True
            
        except Exception as e:
            logger.error(f"Failed to send email to {to_email}: {str(e)}")
            return False
    
    @staticmethod
    async def send_password_reset_email(email: str, reset_token: str, user_name: str) -> bool:
        """Gửi email reset mật khẩu"""
        reset_link = f"{settings.FRONTEND_URL}/reset-password?token={reset_token}"
        
        html_template = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Reset Password</title>
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Food Delivery</h1>
            </div>
            <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #333; margin-top: 0;">Reset Your Password</h2>
                <p>Hello {{ user_name }},</p>
                <p>We received a request to reset your password. Click the button below to create a new password:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="{{ reset_link }}" style="background: #4A90E2; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Reset Password</a>
                </div>
                <p>Or copy and paste this link into your browser:</p>
                <p style="word-break: break-all; color: #666; background: #fff; padding: 10px; border-radius: 5px;">{{ reset_link }}</p>
                <p style="color: #999; font-size: 12px; margin-top: 30px;">
                    This link will expire in 1 hour. If you didn't request a password reset, please ignore this email.
                </p>
            </div>
        </body>
        </html>
        """
        
        template = Template(html_template)
        html_content = template.render(
            user_name=user_name,
            reset_link=reset_link
        )
        
        text_content = f"""
        Hello {user_name},
        
        We received a request to reset your password. Please click the link below:
        
        {reset_link}
        
        This link will expire in 1 hour. If you didn't request a password reset, please ignore this email.
        """
        
        return await EmailService.send_email(
            to_email=email,
            subject="Reset Your Password - Food Delivery",
            html_content=html_content,
            text_content=text_content
        )
    
    @staticmethod
    async def send_verification_email(email: str, verification_token: str, user_name: str) -> bool:
        """Gửi email xác thực tài khoản"""
        verification_link = f"{settings.FRONTEND_URL}/verify-email?token={verification_token}"
        
        html_template = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Verify Your Email</title>
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Food Delivery</h1>
            </div>
            <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #333; margin-top: 0;">Welcome to Food Delivery!</h2>
                <p>Hello {{ user_name }},</p>
                <p>Thank you for registering with Food Delivery. Please verify your email address by clicking the button below:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="{{ verification_link }}" style="background: #4A90E2; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Verify Email</a>
                </div>
                <p>Or copy and paste this link into your browser:</p>
                <p style="word-break: break-all; color: #666; background: #fff; padding: 10px; border-radius: 5px;">{{ verification_link }}</p>
                <p style="color: #999; font-size: 12px; margin-top: 30px;">
                    This link will expire in 24 hours. If you didn't create an account, please ignore this email.
                </p>
            </div>
        </body>
        </html>
        """
        
        template = Template(html_template)
        html_content = template.render(
            user_name=user_name,
            verification_link=verification_link
        )
        
        text_content = f"""
        Hello {user_name},
        
        Thank you for registering with Food Delivery. Please verify your email address by clicking the link below:
        
        {verification_link}
        
        This link will expire in 24 hours.
        """
        
        return await EmailService.send_email(
            to_email=email,
            subject="Verify Your Email - Food Delivery",
            html_content=html_content,
            text_content=text_content
        )
    
    @staticmethod
    async def send_order_confirmation_email(email: str, order_number: str, total_amount: float, user_name: str) -> bool:
        """Gửi email xác nhận đơn hàng"""
        html_template = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Order Confirmation</title>
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Food Delivery</h1>
            </div>
            <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #333; margin-top: 0;">Order Confirmed!</h2>
                <p>Hello {{ user_name }},</p>
                <p>Thank you for your order. We've received your order and are preparing it now.</p>
                <div style="background: white; padding: 20px; border-radius: 5px; margin: 20px 0;">
                    <p style="margin: 0;"><strong>Order Number:</strong> {{ order_number }}</p>
                    <p style="margin: 10px 0 0 0;"><strong>Total Amount:</strong> {{ total_amount }} VND</p>
                </div>
                <p>You can track your order status in your account dashboard.</p>
                <p style="color: #999; font-size: 12px; margin-top: 30px;">
                    If you have any questions, please contact our support team.
                </p>
            </div>
        </body>
        </html>
        """
        
        template = Template(html_template)
        html_content = template.render(
            user_name=user_name,
            order_number=order_number,
            total_amount=f"{total_amount:,.0f}"
        )
        
        text_content = f"""
        Hello {user_name},
        
        Thank you for your order. We've received your order and are preparing it now.
        
        Order Number: {order_number}
        Total Amount: {total_amount:,.0f} VND
        
        You can track your order status in your account dashboard.
        """
        
        return await EmailService.send_email(
            to_email=email,
            subject=f"Order Confirmation - {order_number}",
            html_content=html_content,
            text_content=text_content
        )
    
    @staticmethod
    async def send_order_status_update_email(email: str, order_number: str, status: str, user_name: str) -> bool:
        """Gửi email cập nhật trạng thái đơn hàng"""
        status_messages = {
            "confirmed": "Your order has been confirmed and is being prepared.",
            "preparing": "Your order is being prepared.",
            "ready": "Your order is ready for pickup.",
            "picked_up": "Your order has been picked up and is on the way.",
            "delivering": "Your order is out for delivery.",
            "delivered": "Your order has been delivered!",
            "cancelled": "Your order has been cancelled."
        }
        
        status_message = status_messages.get(status.lower(), "Your order status has been updated.")
        
        html_template = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Order Status Update</title>
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0;">Food Delivery</h1>
            </div>
            <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                <h2 style="color: #333; margin-top: 0;">Order Status Update</h2>
                <p>Hello {{ user_name }},</p>
                <p>{{ status_message }}</p>
                <div style="background: white; padding: 20px; border-radius: 5px; margin: 20px 0;">
                    <p style="margin: 0;"><strong>Order Number:</strong> {{ order_number }}</p>
                    <p style="margin: 10px 0 0 0;"><strong>Status:</strong> <span style="text-transform: capitalize;">{{ status }}</span></p>
                </div>
                <p>You can track your order status in your account dashboard.</p>
            </div>
        </body>
        </html>
        """
        
        template = Template(html_template)
        html_content = template.render(
            user_name=user_name,
            order_number=order_number,
            status=status,
            status_message=status_message
        )
        
        text_content = f"""
        Hello {user_name},
        
        {status_message}
        
        Order Number: {order_number}
        Status: {status}
        
        You can track your order status in your account dashboard.
        """
        
        return await EmailService.send_email(
            to_email=email,
            subject=f"Order Status Update - {order_number}",
            html_content=html_content,
            text_content=text_content
        )

