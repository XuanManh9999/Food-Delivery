# Hướng dẫn Fix Lỗi Emulator - Không đủ dung lượng

## Vấn đề
Emulator báo lỗi: "Not enough space to create userdata partition. Available: 3373.88 MB"

## Giải pháp

### Giải pháp 1: Tạo AVD mới với cấu hình nhẹ hơn (Khuyến nghị)

1. **Mở Device Manager:**
   - Trong Android Studio: Tools → Device Manager
   - Hoặc nhấn biểu tượng Device Manager ở thanh công cụ

2. **Xóa AVD cũ (nếu cần):**
   - Click vào dropdown bên cạnh "Pixel 6"
   - Chọn "Delete" để xóa AVD cũ

3. **Tạo AVD mới:**
   - Click "Create Device"
   - Chọn device profile nhẹ hơn:
     - **Pixel 4** (thay vì Pixel 6)
     - Hoặc **Pixel 3a** (nhẹ nhất)
   - Click "Next"

4. **Chọn System Image:**
   - Chọn API level thấp hơn để tiết kiệm dung lượng:
     - **API 30 (Android 11)** - khoảng 1.5GB
     - Hoặc **API 29 (Android 10)** - khoảng 1.2GB
     - Tránh API 33, 34 (quá nặng, >3GB)
   - Click "Download" nếu chưa có
   - Click "Next"

5. **Cấu hình AVD:**
   - **AVD Name:** Pixel_4_API_30 (hoặc tên khác)
   - **Graphics:** Automatic (hoặc Software - GLES 2.0 nếu máy yếu)
   - **RAM:** 2048 MB (giảm từ 4096 nếu có)
   - **VM heap:** 512 MB
   - **Internal Storage:** 2048 MB (giảm từ mặc định)
   - **SD Card:** Bỏ chọn hoặc 512 MB
   - Click "Finish"

### Giải pháp 2: Giải phóng dung lượng ổ đĩa C:

1. **Xóa các file không cần thiết:**
   ```powershell
   # Xóa temp files
   Remove-Item -Path "$env:TEMP\*" -Recurse -Force -ErrorAction SilentlyContinue
   
   # Xóa Windows temp
   Remove-Item -Path "C:\Windows\Temp\*" -Recurse -Force -ErrorAction SilentlyContinue
   ```

2. **Xóa các AVD cũ không dùng:**
   - Mở: `C:\Users\Admin\.android\avd\`
   - Xóa các thư mục AVD không dùng

3. **Xóa Gradle cache (nếu cần):**
   - Xóa: `C:\Users\Admin\.gradle\caches\`
   - Hoặc chỉ xóa: `C:\Users\Admin\.gradle\caches\modules-2\`

4. **Dọn dẹp Android Studio:**
   - File → Invalidate Caches / Restart
   - Tools → SDK Manager → SDK Tools → Uncheck unused tools

### Giải pháp 3: Di chuyển AVD sang ổ đĩa khác

1. **Tạo biến môi trường ANDROID_AVD_HOME:**
   - Mở System Properties → Environment Variables
   - Thêm biến mới:
     - **Name:** `ANDROID_AVD_HOME`
     - **Value:** `D:\Android\AVD` (hoặc ổ đĩa khác có nhiều dung lượng)
   - Click OK và restart Android Studio

2. **Hoặc sử dụng symbolic link:**
   ```powershell
   # Di chuyển thư mục avd sang ổ D
   Move-Item "C:\Users\Admin\.android\avd" "D:\Android\avd"
   
   # Tạo symbolic link
   New-Item -ItemType SymbolicLink -Path "C:\Users\Admin\.android\avd" -Target "D:\Android\avd"
   ```

### Giải pháp 4: Sử dụng thiết bị thật (Nhanh nhất)

1. **Bật USB Debugging trên điện thoại:**
   - Settings → About Phone → Tap "Build Number" 7 lần
   - Settings → Developer Options → Enable "USB Debugging"

2. **Kết nối điện thoại:**
   - Cắm USB vào máy tính
   - Chấp nhận "Allow USB Debugging" trên điện thoại

3. **Chạy app:**
   - Chọn thiết bị trong Device Manager
   - Click Run

## Khuyến nghị

**Cách nhanh nhất:** Tạo AVD mới với Pixel 4 + API 30 (Android 11)
- Nhẹ hơn Pixel 6 + API 34
- Chỉ cần ~2GB dung lượng
- Chạy mượt hơn trên máy yếu

## Kiểm tra dung lượng còn lại

```powershell
# Xem dung lượng ổ C
Get-PSDrive C | Select-Object Used,Free

# Xem dung lượng thư mục .android
Get-ChildItem "C:\Users\Admin\.android" -Recurse | Measure-Object -Property Length -Sum
```

