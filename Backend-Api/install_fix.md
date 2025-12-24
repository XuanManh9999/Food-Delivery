# Fix Lỗi Cài Đặt Dependencies

Nếu gặp lỗi khi cài đặt `cryptography` (cần Rust compiler), làm theo các bước sau:

## Giải Pháp 1: Upgrade pip và cài từ pre-built wheels (Khuyến nghị)

```bash
# Upgrade pip, wheel, và setuptools
python -m pip install --upgrade pip wheel setuptools

# Cài đặt dependencies
pip install -r requirements.txt
```

## Giải Pháp 2: Cài đặt từng package một

```bash
# Cài các package không cần Rust trước
pip install fastapi uvicorn sqlalchemy pymysql python-dotenv pydantic pydantic-settings alembic aiosmtplib jinja2 email-validator

# Cài cryptography từ pre-built wheel
pip install cryptography --only-binary :all:

# Cài các package còn lại
pip install python-jose[cryptography] passlib[bcrypt]
```

## Giải Pháp 3: Cài Rust Toolchain (Nếu cần compile)

### Windows:

1. Tải Rust từ: https://rustup.rs/
2. Chạy installer
3. Restart terminal
4. Chạy lại: `pip install -r requirements.txt`

### Hoặc dùng Chocolatey:

```powershell
choco install rust
```

## Giải Pháp 4: Sử dụng Conda (Alternative)

Nếu vẫn gặp vấn đề, có thể dùng Conda:

```bash
# Cài Conda từ https://docs.conda.io/en/latest/miniconda.html

# Tạo environment
conda create -n fooddelivery python=3.11
conda activate fooddelivery

# Cài dependencies
pip install -r requirements.txt
```

## Kiểm Tra Sau Khi Cài

```bash
python -c "import cryptography; print('Cryptography OK')"
python -c "import fastapi; print('FastAPI OK')"
python -c "import sqlalchemy; print('SQLAlchemy OK')"
```

Nếu tất cả đều OK, bạn có thể chạy server:

```bash
python run.py
```
