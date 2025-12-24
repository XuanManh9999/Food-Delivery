# Email Setup Guide

Hướng dẫn cấu hình email cho ứng dụng Food Delivery.

## Cấu hình Email

### 1. Gmail (Khuyến nghị cho development)

1. **Bật 2-Step Verification:**
   - Vào https://myaccount.google.com/security
   - Bật 2-Step Verification

2. **Tạo App Password:**
   - Vào https://myaccount.google.com/apppasswords
   - Chọn "Mail" và "Other (Custom name)"
   - Nhập tên: "Food Delivery App"
   - Copy App Password (16 ký tự)

3. **Cập nhật .env:**
```env
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-16-char-app-password
SMTP_FROM_EMAIL=your-email@gmail.com
SMTP_FROM_NAME=Food Delivery
SMTP_USE_TLS=True
```

### 2. SendGrid (Khuyến nghị cho production)

1. **Đăng ký tài khoản:**
   - Vào https://sendgrid.com
   - Tạo tài khoản miễn phí (100 emails/ngày)

2. **Tạo API Key:**
   - Settings > API Keys
   - Create API Key
   - Copy API Key

3. **Cập nhật .env:**
```env
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USER=apikey
SMTP_PASSWORD=your-sendgrid-api-key
SMTP_FROM_EMAIL=noreply@yourdomain.com
SMTP_FROM_NAME=Food Delivery
SMTP_USE_TLS=True
```

### 3. Mailgun

1. **Đăng ký tài khoản:**
   - Vào https://www.mailgun.com
   - Tạo tài khoản

2. **Lấy SMTP credentials:**
   - Domain > SMTP credentials

3. **Cập nhật .env:**
```env
SMTP_HOST=smtp.mailgun.org
SMTP_PORT=587
SMTP_USER=postmaster@your-domain.mailgun.org
SMTP_PASSWORD=your-mailgun-password
SMTP_FROM_EMAIL=noreply@yourdomain.com
SMTP_FROM_NAME=Food Delivery
SMTP_USE_TLS=True
```

## Các tính năng Email

### 1. Email Verification
- Tự động gửi khi đăng ký tài khoản
- Token hết hạn sau 24 giờ
- Endpoint: `POST /api/auth/verify-email`

### 2. Password Reset
- Gửi khi yêu cầu reset mật khẩu
- Token hết hạn sau 1 giờ
- Endpoint: `POST /api/auth/forgot-password`

### 3. Order Confirmation
- Tự động gửi khi tạo đơn hàng thành công
- Chứa thông tin đơn hàng và tổng tiền

### 4. Order Status Update
- Tự động gửi khi trạng thái đơn hàng thay đổi
- Thông báo các trạng thái: confirmed, preparing, ready, delivering, delivered, cancelled

## Testing Email

### Test trong development:

1. **Sử dụng Mailtrap (Khuyến nghị):**
   - Đăng ký tại https://mailtrap.io
   - Lấy SMTP credentials
   - Cập nhật .env với Mailtrap credentials
   - Xem emails trong Mailtrap inbox

2. **Sử dụng Gmail:**
   - Cấu hình như hướng dẫn trên
   - Kiểm tra inbox/spam folder

## Troubleshooting

### Email không gửi được:

1. **Kiểm tra credentials:**
   - Đảm bảo SMTP_USER và SMTP_PASSWORD đúng
   - Với Gmail: phải dùng App Password, không phải mật khẩu thường

2. **Kiểm tra firewall:**
   - Đảm bảo port 587 không bị chặn

3. **Kiểm tra logs:**
   - Xem logs trong console để biết lỗi cụ thể

4. **Test connection:**
   ```python
   import aiosmtplib
   
   async def test_smtp():
       await aiosmtplib.send(
           message,
           hostname="smtp.gmail.com",
           port=587,
           username="your-email@gmail.com",
           password="your-app-password",
           use_tls=True,
       )
   ```

## Security Best Practices

1. **Không commit .env file:**
   - Đã có trong .gitignore
   - Luôn sử dụng .env.example làm template

2. **Sử dụng App Passwords:**
   - Không dùng mật khẩu chính của email
   - Tạo App Password riêng cho ứng dụng

3. **Rate Limiting:**
   - Giới hạn số lượng email gửi trong một khoảng thời gian
   - Tránh spam và abuse

4. **Email Validation:**
   - Validate email format trước khi gửi
   - Kiểm tra email domain tồn tại (optional)

## Production Checklist

- [ ] Cấu hình SMTP production (SendGrid/Mailgun)
- [ ] Verify domain email
- [ ] Setup SPF và DKIM records
- [ ] Test tất cả email templates
- [ ] Setup email monitoring và alerts
- [ ] Configure rate limiting
- [ ] Setup bounce và complaint handling
- [ ] Test email deliverability

