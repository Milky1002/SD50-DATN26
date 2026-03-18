# SD50-DATN26 — Hệ Thống Quản Lý Cửa Hàng Cầu Lông

Hệ thống quản lý cửa hàng bán lẻ cầu lông, hỗ trợ bán hàng tại quầy (POS), quản lý sản phẩm, nhập/xuất kho, khuyến mãi, hoá đơn và khách hàng.

---

## Yêu Cầu Hệ Thống

- Java 17+
- Maven 3.6+
- SQL Server 2019+
- `sqlcmd` (có sẵn khi cài SQL Server, hoặc cài riêng từ [mssql-tools](https://docs.microsoft.com/en-us/sql/tools/sqlcmd-utility))

---

## Hướng Dẫn Cài Đặt

### 1. Clone dự án

```bash
git clone https://github.com/Milky1002/SD50-DATN26.git
cd SD50-DATN26
```

### 2. Tạo database và bảng

Chạy file `SetupDatabaseSQL.sql` để tạo database `sd50`, tất cả bảng và dữ liệu ban đầu:

```bash
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -i SQL_Query/SetupDatabaseSQL.sql
```

### 3. Chạy script cập nhật dữ liệu mẫu

```bash
cd SQL_Query
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -i run_all_updates.sql
cd ..
```

> **Lưu ý:** File `run_all_updates.sql` dùng lệnh `:r updates\...` (đường dẫn tương đối), nên bạn **phải `cd SQL_Query`** trước khi chạy.

Nếu muốn chạy từng file riêng lẻ, chạy theo thứ tự số trong thư mục `updates/`:

```bash
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/01_cap_nhat_chuc_vu.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/02_cap_nhat_tai_khoan.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/03_cap_nhat_nhan_vien.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/04_cap_nhat_hinh_thuc_thanh_toan.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/05_cap_nhat_danh_muc.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/06_cap_nhat_mau_sac.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/07_cap_nhat_san_pham.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/08_cap_nhat_khuyen_mai.sql
sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -d sd50 -i SQL_Query/updates/09_them_khach_hang.sql
```

### 4. Cấu hình kết nối database

Mở file `src/main/resources/application.properties` và chỉnh sửa nếu thông tin kết nối khác mặc định:

```properties
spring.datasource.url=jdbc:sqlserver://127.0.0.1:1433;databaseName=sd50;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123
server.port=8888
```

### 5. Build và chạy ứng dụng

```bash
mvn clean install
mvn spring-boot:run
```

### 6. Truy cập hệ thống

Mở trình duyệt tại: **http://localhost:8888/login**

---

## Tài Khoản Mặc Định

| Tài khoản | Mật khẩu | Vai trò |
|------------|-----------|---------|
| `admin` | `admin@123` | Quản lý (toàn quyền) |
| `nhanvien01` | `admin@123` | Nhân viên |
| `nhanvien02` | `admin@123` | Nhân viên |

> **Quan trọng:** Đổi mật khẩu ngay sau lần đăng nhập đầu tiên! Bấm vào tên người dùng góc trên phải → "Đổi mật khẩu".

---

## Cấu Trúc Thư Mục SQL

```
SQL_Query/
  SetupDatabaseSQL.sql                    # Tạo database, tất cả bảng, dữ liệu ban đầu
  run_all_updates.sql                     # Chạy tất cả script cập nhật (gọi tuần tự các file trong updates/)
  updates/
    01_cap_nhat_chuc_vu.sql               # Chức vụ: Quản lý, Nhân viên
    02_cap_nhat_tai_khoan.sql             # Tài khoản: admin, nhanvien01, nhanvien02
    03_cap_nhat_nhan_vien.sql             # Nhân viên mẫu
    04_cap_nhat_hinh_thuc_thanh_toan.sql  # Hình thức thanh toán
    05_cap_nhat_danh_muc.sql              # Danh mục sản phẩm
    05b_fix_danh_muc.sql                  # Sửa danh mục cho DB cũ
    06_cap_nhat_mau_sac.sql               # Màu sắc
    06b_fix_mau_sac.sql                   # Sửa màu sắc cho DB cũ
    07_cap_nhat_san_pham.sql              # Sản phẩm
    07b_fix_san_pham.sql                  # Sửa sản phẩm cho DB cũ
    08_cap_nhat_khuyen_mai.sql            # Chương trình khuyến mãi
    09_them_khach_hang.sql                # Khách hàng mẫu
```

---

## Cấu Trúc Dự Án

```
SD50-DATN26/
  src/main/
    java/com/example/sd50datn/
      Config/
        AuthInterceptor.java              # Chặn request chưa đăng nhập → redirect /login
        AdminInterceptor.java             # Chặn request không đủ quyền (chỉ "Quản lý")
        WebMvcConfig.java                 # Đăng ký interceptor
      Controller/
        LoginController.java              # Đăng nhập, đăng xuất, đổi mật khẩu
        DashboardController.java          # Trang tổng quan
        SanPhamController.java            # Quản lý sản phẩm
        DanhMucSanPhamController.java     # Quản lý danh mục
        MauSacController.java             # Quản lý màu sắc
        BanHangController.java            # Bán hàng tại quầy (POS)
        XuatKhoController.java            # Quản lý xuất kho
        InvoiceController.java            # Quản lý hoá đơn
        OrderController.java              # Quản lý đơn hàng
        StaffController.java              # Quản lý nhân viên
        KhachHangController.java          # Quản lý khách hàng
        KhuyenMaiViewController.java      # Quản lý khuyến mãi
        ChuongTrinhKhuyenMaiController.java  # API khuyến mãi
      Service/
        AuthService.java                  # Xác thực, BCrypt, đổi mật khẩu
        StaffService.java                 # Quản lý nhân viên (hash mật khẩu)
        ...
      entity/                             # JPA entity classes
      repository/                         # JPA repositories
      dto/                                # Data Transfer Objects
    resources/
      templates/
        layout.html                       # Layout chính (sidebar, topbar, menu người dùng)
        login.html                        # Trang đăng nhập
        ...
      static/
        css/app.css                       # Giao diện menu, modal, toast
        css/login.css                     # Giao diện trang đăng nhập
        js/app.js                         # Dropdown, modal, đổi mật khẩu, toast
      application.properties              # Cấu hình database, port
  SQL_Query/                              # Script SQL (xem mục trên)
  pom.xml                                 # Cấu hình Maven
```

---

## Các Trang Chính

| URL | Chức năng | Quyền truy cập |
|-----|-----------|-----------------|
| `/login` | Đăng nhập | Công khai |
| `/logout` | Đăng xuất | Đã đăng nhập |
| `/dashboard` | Tổng quan | Đã đăng nhập |
| `/san-pham` | Quản lý sản phẩm (CRUD, tìm kiếm, lọc, Excel, barcode) | Đã đăng nhập |
| `/danh-muc` | Quản lý danh mục sản phẩm | Đã đăng nhập |
| `/mau-sac` | Quản lý màu sắc | Đã đăng nhập |
| `/ban-hang` | Bán hàng tại quầy (POS) | Đã đăng nhập |
| `/xuat-kho` | Quản lý xuất kho | Đã đăng nhập |
| `/invoices` | Quản lý hoá đơn | Đã đăng nhập |
| `/orders` | Quản lý đơn hàng | Đã đăng nhập |
| `/khuyen-mai` | Chương trình khuyến mãi | Đã đăng nhập |
| `/khach-hang` | Quản lý khách hàng | Đã đăng nhập |
| `/nhan-vien` | Quản lý nhân viên | Chỉ "Quản lý" |

---

## Xác Thực & Phân Quyền

Hệ thống sử dụng xác thực dựa trên session:

- **AuthInterceptor** — chặn tất cả request (trừ `/login`, `/css/**`, `/js/**`...). Nếu chưa đăng nhập sẽ redirect về `/login`.
- **AdminInterceptor** — chặn các URL `/nhan-vien/**`. Chỉ cho phép vai trò "Quản lý" truy cập.
- **BCrypt** — mật khẩu được hash bằng BCrypt. Nếu database có mật khẩu plaintext cũ, hệ thống tự động chuyển đổi sang BCrypt khi đăng nhập thành công.

**Đổi mật khẩu:** Bấm vào tên người dùng góc trên phải → "Đổi mật khẩu", hoặc gọi API:

```
POST /api/change-password
Content-Type: application/x-www-form-urlencoded

oldPassword=mậtkhẩucũ&newPassword=mậtkhẩumới
```

---

## Xác Nhận Sau Khi Cài Đặt

Sau khi chạy xong script SQL, có thể kiểm tra dữ liệu bằng truy vấn sau:

```sql
USE [sd50];

SELECT 'ChucVu' AS Bảng, COUNT(*) AS SốLượng FROM dbo.ChucVu
UNION ALL SELECT N'TaiKhoan', COUNT(*) FROM dbo.TaiKhoan
UNION ALL SELECT N'NhanVien', COUNT(*) FROM dbo.NhanVien
UNION ALL SELECT N'DanhMuc', COUNT(*) FROM dbo.Danh_muc_san_pham
UNION ALL SELECT N'MauSac', COUNT(*) FROM dbo.MauSac
UNION ALL SELECT N'SanPham', COUNT(*) FROM dbo.SanPham
UNION ALL SELECT N'KhuyenMai', COUNT(*) FROM dbo.Chuong_trinh_khuyen_mai
UNION ALL SELECT N'KhachHang', COUNT(*) FROM dbo.KhachHang
UNION ALL SELECT N'HTTT', COUNT(*) FROM dbo.Hinh_thuc_thanh_toan;
```

Kết quả mong đợi:

| Bảng | Số lượng |
|------|----------|
| ChucVu | 2 |
| TaiKhoan | 3 |
| NhanVien | 3 |
| DanhMuc | 5 |
| MauSac | 10 |
| SanPham | 12 |
| KhuyenMai | 7 |
| KhachHang | 9 |
| HTTT | 4 |

---

## Lưu Ý Quan Trọng

- **Đổi mật khẩu ngay** sau lần đăng nhập đầu tiên. Mật khẩu mặc định trong SQL seed là plaintext, hệ thống sẽ tự hash bằng BCrypt khi đăng nhập.
- **Backup database** trước khi chạy script cập nhật trên môi trường production:
  ```sql
  BACKUP DATABASE [sd50] TO DISK = N'C:\Backup\sd50_backup.bak' WITH FORMAT;
  ```
- Các file `05b`, `06b`, `07b` trong `updates/` là bản sửa bổ sung cho database cũ — chỉ cần chạy nếu gặp lỗi khi chạy file chính tương ứng.
- Hệ thống xác thực hiện tại là application-level (interceptor), chưa dùng Spring Security. Nên cân nhắc nâng cấp lên Spring Security cho môi trường production.

---

## Xử Lý Lỗi Thường Gặp

| Lỗi | Cách xử lý |
|-----|------------|
| Không kết nối được database | Kiểm tra SQL Server đang chạy, đúng port 1433, đúng username/password |
| Port 8888 đã được sử dụng | Đổi `server.port` trong `application.properties` hoặc tắt process đang dùng port |
| "Table not found" | Chạy `SetupDatabaseSQL.sql` trước, sau đó chạy `run_all_updates.sql` |
| Lỗi khi chạy `run_all_updates.sql` | Phải `cd SQL_Query` trước khi chạy (do file dùng đường dẫn tương đối) |
| Đăng nhập không được | Kiểm tra đã chạy `02_cap_nhat_tai_khoan.sql`. Tài khoản: `admin` / `admin@123` |
| Truy cập `/nhan-vien` bị redirect | Chỉ tài khoản có chức vụ "Quản lý" mới truy cập được trang này |
