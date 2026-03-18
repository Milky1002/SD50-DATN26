-- =============================================
-- CHẠY TẤT CẢ SCRIPT CẬP NHẬT
-- File này gọi lần lượt từng script nhỏ
-- Chạy trên database sd50 đã có sẵn
-- =============================================
-- Tài khoản đăng nhập: admin / admin@123
-- Nhân viên mẫu:       nhanvien01 / admin@123
--                       nhanvien02 / admin@123
-- =============================================

USE [sd50];
GO

PRINT N'';
PRINT N'========================================';
PRINT N'  BẮT ĐẦU CẬP NHẬT DỮ LIỆU SD50';
PRINT N'========================================';
PRINT N'';

-- Script 01: Chức vụ
:r updates\01_cap_nhat_chuc_vu.sql

-- Script 02: Tài khoản
:r updates\02_cap_nhat_tai_khoan.sql

-- Script 03: Nhân viên
:r updates\03_cap_nhat_nhan_vien.sql

-- Script 04: Hình thức thanh toán
:r updates\04_cap_nhat_hinh_thuc_thanh_toan.sql

-- Script 05: Danh mục sản phẩm
:r updates\05_cap_nhat_danh_muc.sql

-- Script 06: Màu sắc
:r updates\06_cap_nhat_mau_sac.sql

-- Script 07: Sản phẩm
:r updates\07_cap_nhat_san_pham.sql

-- Script 08: Khuyến mãi
:r updates\08_cap_nhat_khuyen_mai.sql

-- Script 09: Khách hàng
:r updates\09_them_khach_hang.sql

PRINT N'';
PRINT N'========================================';
PRINT N'  ✓ HOÀN TẤT CẬP NHẬT DỮ LIỆU!';
PRINT N'========================================';
PRINT N'  Tài khoản: admin / admin@123';
PRINT N'  Nhân viên: nhanvien01 / admin@123';
PRINT N'  Nhân viên: nhanvien02 / admin@123';
PRINT N'========================================';
GO
