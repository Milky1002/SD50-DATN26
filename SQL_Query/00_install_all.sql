-- ============================================================
-- 00_install_all.sql  —  Master installer
-- Cửa hàng Cầu Lông SD50-DATN26
--
-- CÁCH CHẠY (từ thư mục SQL_Query):
--   sqlcmd -S 127.0.0.1,1433 -U sa -P 123 -i 00_install_all.sql
--
-- Hoặc mở file này trong SSMS với chế độ SQLCMD Mode (Query > SQLCMD Mode)
-- rồi nhấn Execute.
--
-- Thứ tự chạy:
--   01  Tạo database sd50
--   02  Schema lõi (ChucVu, TaiKhoan, NhanVien)
--   03  Schema sản phẩm (DanhMuc, MauSac, Anh, SanPham)
--   04  Schema khách hàng (Khach_hang)
--   05  Schema thanh toán (HinhThucThanhToan)
--   06  Schema hóa đơn (HoaDon, HoaDonChiTiet, ThanhToan)
--   07  Schema khuyến mãi
--   08  Schema kho hàng (NhaCungCap, PhieuNhap, PhieuXuat)
--   09  Schema giỏ hàng
--   10  Schema misc (LichSu, TrangChu)
--   11  Seed lõi (ChucVu x5, TaiKhoan x22, NhanVien x20)
--   12  Seed sản phẩm (DanhMuc x10, MauSac x10, Anh x20, SanPham x20)
--   13  Seed khách hàng (x20)
--   14  Seed hóa đơn (HinhThuc x5, HoaDon x20, HDCT x20, TT x18)
--   15  Seed khuyến mãi (x10 + chi tiết + lịch sử)
--   16  Seed kho hàng (NCC x10, PhieuNhap x10, PhieuXuat x5)
--   17  Seed misc (GioHang, TrangChu, LichSu x20)
-- ============================================================

PRINT N'============================================================';
PRINT N'Bắt đầu cài đặt database sd50...';
PRINT N'============================================================';
GO

-- 01 — Tạo database
:r 01_create_database.sql

PRINT N'[01] Tạo database: DONE';
GO

-- 02 — Schema core
:r 02_schema_core.sql

PRINT N'[02] Schema core: DONE';
GO

-- 03 — Schema product
:r 03_schema_product.sql

PRINT N'[03] Schema sản phẩm: DONE';
GO

-- 04 — Schema customer
:r 04_schema_customer.sql

PRINT N'[04] Schema khách hàng: DONE';
GO

-- 05 — Schema payment
:r 05_schema_payment.sql

PRINT N'[05] Schema thanh toán: DONE';
GO

-- 06 — Schema invoice
:r 06_schema_invoice.sql

PRINT N'[06] Schema hóa đơn: DONE';
GO

-- 07 — Schema promotion
:r 07_schema_promotion.sql

PRINT N'[07] Schema khuyến mãi: DONE';
GO

-- 08 — Schema warehouse
:r 08_schema_warehouse.sql

PRINT N'[08] Schema kho hàng: DONE';
GO

-- 09 — Schema cart
:r 09_schema_cart.sql

PRINT N'[09] Schema giỏ hàng: DONE';
GO

-- 10 — Schema misc
:r 10_schema_misc.sql

PRINT N'[10] Schema misc: DONE';
GO

-- 11 — Seed core
:r 11_seed_core.sql

PRINT N'[11] Seed core data: DONE';
GO

-- 12 — Seed product
:r 12_seed_product.sql

PRINT N'[12] Seed sản phẩm: DONE';
GO

-- 13 — Seed customer
:r 13_seed_customer.sql

PRINT N'[13] Seed khách hàng: DONE';
GO

-- 14 — Seed invoice
:r 14_seed_invoice.sql

PRINT N'[14] Seed hóa đơn: DONE';
GO

-- 15 — Seed promotion
:r 15_seed_promotion.sql

PRINT N'[15] Seed khuyến mãi: DONE';
GO

-- 16 — Seed warehouse
:r 16_seed_warehouse.sql

PRINT N'[16] Seed kho hàng: DONE';
GO

-- 17 — Seed misc
:r 17_seed_misc.sql

PRINT N'[17] Seed misc: DONE';
GO

PRINT N'============================================================';
PRINT N'Cài đặt hoàn thành!';
PRINT N'';
PRINT N'Tài khoản mặc định:';
PRINT N'  admin / admin@123         (ADMIN)';
PRINT N'  nhanvien01 / admin@123    (STAFF)';
PRINT N'  nhanvien02 / admin@123    (STAFF)';
PRINT N'';
PRINT N'Truy cập: http://localhost:8888/login';
PRINT N'============================================================';
GO
