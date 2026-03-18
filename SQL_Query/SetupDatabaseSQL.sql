-- ===========================================================================
-- SetupDatabaseSQL.sql
-- Consolidated setup script for SD50-DATN26
-- Run this ONCE on a fresh SQL Server to create all tables and seed data.
-- ===========================================================================

-- 1. Create database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'sd50')
BEGIN
    CREATE DATABASE [sd50];
END
GO

USE [sd50];
GO

-- ===========================================================================
-- SECTION 1: CORE TABLES
-- ===========================================================================

CREATE TABLE [dbo].[ChucVu] (
    [Chuc_vu_id] INT IDENTITY(1,1) NOT NULL,
    [Ten_chuc_vu] NVARCHAR(255) NOT NULL,
    [Mo_ta_chuc_vu] NVARCHAR(MAX) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_ChucVu] PRIMARY KEY CLUSTERED ([Chuc_vu_id] ASC)
);
GO

CREATE TABLE [dbo].[TaiKhoan] (
    [Tai_khoan_id] INT IDENTITY(1,1) NOT NULL,
    [User_name] NVARCHAR(255) NOT NULL,
    [Pass_word] NVARCHAR(255) NOT NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_TaiKhoan] PRIMARY KEY CLUSTERED ([Tai_khoan_id] ASC),
    CONSTRAINT [UQ_TaiKhoan_User_name] UNIQUE ([User_name])
);
GO

CREATE TABLE [dbo].[NhanVien] (
    [Nhan_vien_id] INT IDENTITY(1,1) NOT NULL,
    [Ho_ten] NVARCHAR(255) NOT NULL,
    [Gioi_tinh] NVARCHAR(50) NULL,
    [SDT] NVARCHAR(50) NULL,
    [Dia_chi] NVARCHAR(MAX) NULL,
    [Email] NVARCHAR(255) NULL,
    [Ngay_sinh] DATE NULL,
    [Chuc_vu_id] INT NOT NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Tai_khoan_id] INT NULL,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_NhanVien] PRIMARY KEY CLUSTERED ([Nhan_vien_id] ASC),
    CONSTRAINT [FK_NhanVien_ChucVu] FOREIGN KEY ([Chuc_vu_id]) REFERENCES [dbo].[ChucVu] ([Chuc_vu_id]),
    CONSTRAINT [FK_NhanVien_TaiKhoan] FOREIGN KEY ([Tai_khoan_id]) REFERENCES [dbo].[TaiKhoan] ([Tai_khoan_id]) ON DELETE SET NULL,
    CONSTRAINT [CK_NhanVien_Email] CHECK ([Email] IS NULL OR [Email] LIKE '%@%.%')
);
GO

CREATE TABLE [dbo].[Nhat_ky_he_thong] (
    [Log_id] INT IDENTITY(1,1) NOT NULL,
    [User_id] INT NULL,
    [Pass_word] NVARCHAR(255) NULL,
    [Trang_thai] INT NOT NULL,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_Nhat_ky_he_thong] PRIMARY KEY CLUSTERED ([Log_id] ASC)
);
GO

CREATE TABLE [dbo].[Khach_hang] (
    [Khach_hang_id] INT IDENTITY(1,1) NOT NULL,
    [Ten_khach_hang] NVARCHAR(255) NOT NULL,
    [SDT] NVARCHAR(50) NULL,
    [Email] NVARCHAR(255) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Tai_khoan_id] INT NULL,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    [Dia_chi_khach_hang] NVARCHAR(MAX) NULL,
    CONSTRAINT [PK_Khach_hang] PRIMARY KEY CLUSTERED ([Khach_hang_id] ASC),
    CONSTRAINT [FK_Khach_hang_TaiKhoan] FOREIGN KEY ([Tai_khoan_id]) REFERENCES [dbo].[TaiKhoan] ([Tai_khoan_id]) ON DELETE SET NULL,
    CONSTRAINT [CK_Khach_hang_Email] CHECK ([Email] IS NULL OR [Email] LIKE '%@%.%')
);
GO

CREATE TABLE [dbo].[DiaChiKhachHang] (
    [Dia_chi_id] INT IDENTITY(1,1) NOT NULL,
    [Khach_hang_id] INT NOT NULL,
    [Xa] NVARCHAR(255) NULL,
    [Huyen] NVARCHAR(255) NULL,
    [Thanh_pho] NVARCHAR(255) NULL,
    [So_nha] NVARCHAR(255) NULL,
    [Ghi_chu] NVARCHAR(MAX) NULL,
    CONSTRAINT [PK_DiaChiKhachHang] PRIMARY KEY CLUSTERED ([Dia_chi_id] ASC),
    CONSTRAINT [FK_DiaChiKhachHang_Khach_hang] FOREIGN KEY ([Khach_hang_id]) REFERENCES [dbo].[Khach_hang] ([Khach_hang_id]) ON DELETE CASCADE
);
GO

CREATE TABLE [dbo].[Voucher] (
    [Voucher_id] INT IDENTITY(1,1) NOT NULL,
    [Ten_vou_cher] NVARCHAR(255) NOT NULL,
    [Ma_voucher] NVARCHAR(50) NOT NULL,
    [Ngay_bat_dau] DATETIME2 NOT NULL,
    [Ngay_ket_thuc] DATETIME2 NOT NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [So_luong] INT NOT NULL DEFAULT 0,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_Voucher] PRIMARY KEY CLUSTERED ([Voucher_id] ASC),
    CONSTRAINT [UQ_Voucher_Ma_voucher] UNIQUE ([Ma_voucher]),
    CONSTRAINT [CK_Voucher_So_luong] CHECK ([So_luong] >= 0),
    CONSTRAINT [CK_Voucher_Ngay] CHECK ([Ngay_ket_thuc] >= [Ngay_bat_dau])
);
GO

CREATE TABLE [dbo].[HinhThucThanhToan] (
    [Hinh_thuc_thanh_toan_id] INT IDENTITY(1,1) NOT NULL,
    [Ten_hinh_thuc] NVARCHAR(255) NOT NULL,
    [Mo_ta] NVARCHAR(MAX) NULL,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_HinhThucThanhToan] PRIMARY KEY CLUSTERED ([Hinh_thuc_thanh_toan_id] ASC)
);
GO

-- Danh muc san pham (with Ngay_tao/Ngay_cap_nhat columns already included)
CREATE TABLE [dbo].[Danh_muc_san_pham] (
    [Danh_muc_san_pham_id] INT IDENTITY(1,1) NOT NULL,
    [Ten_danh_muc] NVARCHAR(255) NOT NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATE NULL,
    [Ngay_cap_nhat] DATE NULL,
    CONSTRAINT [PK_Danh_muc_san_pham] PRIMARY KEY CLUSTERED ([Danh_muc_san_pham_id] ASC),
    CONSTRAINT [UQ_Danh_muc_san_pham_Ten_danh_muc] UNIQUE ([Ten_danh_muc])
);
GO

CREATE TABLE [dbo].[Mau_sac] (
    [Mau_sac_id] INT IDENTITY(1,1) NOT NULL,
    [Ten_mau] NVARCHAR(255) NOT NULL,
    [Ma_mau_hex] NVARCHAR(50) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_Mau_sac] PRIMARY KEY CLUSTERED ([Mau_sac_id] ASC)
);
GO

CREATE TABLE [dbo].[Anh] (
    [Anh_id] INT IDENTITY(1,1) NOT NULL,
    [Anh_url] NVARCHAR(MAX) NOT NULL,
    [Mo_ta] NVARCHAR(MAX) NULL,
    [Thu_tu] INT NOT NULL DEFAULT 0,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT [PK_Anh] PRIMARY KEY CLUSTERED ([Anh_id] ASC)
);
GO

-- SanPham (with Barcode column already included)
CREATE TABLE [dbo].[SanPham] (
    [San_pham_id] INT IDENTITY(1,1) NOT NULL,
    [Danh_muc_san_pham_id] INT NOT NULL,
    [Mau_sac_id] INT NULL,
    [Anh_id] INT NULL,
    [Ten_san_pham] NVARCHAR(255) NOT NULL,
    [Ma_san_pham] NVARCHAR(50) NOT NULL,
    [Sku] NVARCHAR(50) NOT NULL,
    [Barcode] NVARCHAR(100) NULL,
    [Gia_nhap] DECIMAL(18, 2) NOT NULL,
    [Gia_ban] DECIMAL(18, 2) NOT NULL,
    [So_luong_ton] INT NOT NULL DEFAULT 0,
    [Don_vi_tinh] NVARCHAR(50) NULL,
    [Mo_ta] NVARCHAR(MAX) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_sua] DATETIME2 NULL,
    CONSTRAINT [PK_SanPham] PRIMARY KEY CLUSTERED ([San_pham_id] ASC),
    CONSTRAINT [FK_SanPham_Danh_muc_san_pham] FOREIGN KEY ([Danh_muc_san_pham_id]) REFERENCES [dbo].[Danh_muc_san_pham] ([Danh_muc_san_pham_id]),
    CONSTRAINT [FK_SanPham_Mau_sac] FOREIGN KEY ([Mau_sac_id]) REFERENCES [dbo].[Mau_sac] ([Mau_sac_id]) ON DELETE SET NULL,
    CONSTRAINT [FK_SanPham_Anh] FOREIGN KEY ([Anh_id]) REFERENCES [dbo].[Anh] ([Anh_id]) ON DELETE SET NULL,
    CONSTRAINT [UQ_SanPham_Ma_san_pham] UNIQUE ([Ma_san_pham]),
    CONSTRAINT [UQ_SanPham_Sku] UNIQUE ([Sku]),
    CONSTRAINT [CK_SanPham_Gia] CHECK ([Gia_ban] >= [Gia_nhap] AND [Gia_nhap] >= 0 AND [Gia_ban] >= 0),
    CONSTRAINT [CK_SanPham_So_luong_ton] CHECK ([So_luong_ton] >= 0)
);
GO

-- Unique index on Barcode (filtered, allowing NULLs)
CREATE UNIQUE NONCLUSTERED INDEX [UQ_SanPham_Barcode]
    ON [dbo].[SanPham] ([Barcode])
    WHERE [Barcode] IS NOT NULL;
GO

CREATE TABLE [dbo].[Thong_so_vot] (
    [Thong_so_id] INT IDENTITY(1,1) NOT NULL,
    [San_pham_id] INT NOT NULL,
    [Hang_can (theo tieu chuan U)] NVARCHAR(255) NULL,
    [Co_can_vot (theo tieu chuan G)] NVARCHAR(255) NULL,
    [Diem_can_bang] NVARCHAR(255) NULL,
    [Kieu_can_bang] NVARCHAR(255) NULL,
    [Do_cung_than_vot] NVARCHAR(255) NULL,
    [Muc_cang_day_toi_da] NVARCHAR(255) NULL,
    [Muc_cang_day_khuyen_nghi] NVARCHAR(255) NULL,
    [Chat_lieu] NVARCHAR(255) NULL,
    [Xuat_xu] NVARCHAR(255) NULL,
    [Loi_choi] NVARCHAR(255) NULL,
    [Trinh_do] NVARCHAR(255) NULL,
    CONSTRAINT [PK_Thong_so_vot] PRIMARY KEY CLUSTERED ([Thong_so_id] ASC),
    CONSTRAINT [FK_Thong_so_vot_SanPham] FOREIGN KEY ([San_pham_id]) REFERENCES [dbo].[SanPham] ([San_pham_id]) ON DELETE CASCADE
);
GO

-- ===========================================================================
-- SECTION 2: SHOPPING CART
-- ===========================================================================

CREATE TABLE [dbo].[GioHang] (
    [Gio_hang_id] INT IDENTITY(1,1) NOT NULL,
    [Khach_hang_id] INT NOT NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_GioHang] PRIMARY KEY CLUSTERED ([Gio_hang_id] ASC),
    CONSTRAINT [FK_GioHang_Khach_hang] FOREIGN KEY ([Khach_hang_id]) REFERENCES [dbo].[Khach_hang] ([Khach_hang_id]) ON DELETE CASCADE
);
GO

CREATE TABLE [dbo].[GioHangChiTiet] (
    [Gio_hang_chi_tiet_id] INT IDENTITY(1,1) NOT NULL,
    [Gio_hang_id] INT NOT NULL,
    [San_pham_id] INT NOT NULL,
    [San_pham_chi_tiet_id] INT NULL,
    [So_luong] INT NOT NULL DEFAULT 1,
    [Don_gia] DECIMAL(18, 2) NOT NULL,
    [Thanh_tien] DECIMAL(18, 2) NOT NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2 NULL,
    CONSTRAINT [PK_GioHangChiTiet] PRIMARY KEY CLUSTERED ([Gio_hang_chi_tiet_id] ASC),
    CONSTRAINT [FK_GioHangChiTiet_GioHang] FOREIGN KEY ([Gio_hang_id]) REFERENCES [dbo].[GioHang] ([Gio_hang_id]) ON DELETE CASCADE,
    CONSTRAINT [FK_GioHangChiTiet_SanPham] FOREIGN KEY ([San_pham_id]) REFERENCES [dbo].[SanPham] ([San_pham_id]),
    CONSTRAINT [CK_GioHangChiTiet_So_luong] CHECK ([So_luong] > 0),
    CONSTRAINT [CK_GioHangChiTiet_Don_gia] CHECK ([Don_gia] >= 0),
    CONSTRAINT [CK_GioHangChiTiet_Thanh_tien] CHECK ([Thanh_tien] >= 0)
);
GO

-- ===========================================================================
-- SECTION 3: INVOICES & PAYMENTS (HoaDon - with nullable FKs for POS)
-- ===========================================================================

CREATE TABLE [dbo].[HoaDon] (
    [Hoa_don_id] INT IDENTITY(1,1) NOT NULL,
    [Nhan_vien_id] INT NOT NULL,
    [Voucher_id] INT NULL,
    [Khach_hang_id] INT NULL,
    [Hinh_thuc_thanh_toan_id] INT NULL,
    [Dia_chi_id] INT NULL,
    [Ten_khach_hang] NVARCHAR(255) NOT NULL,
    [Sdt_khach_hang] NVARCHAR(50) NULL,
    [Email_khach_hang] NVARCHAR(255) NULL,
    [Ngay_tao] DATETIME2 NOT NULL DEFAULT GETDATE(),
    [Ngay_nhan_hang] DATETIME2 NULL,
    [Tong_tien_sau_khi_giam] DECIMAL(18, 2) NOT NULL,
    [Trang_thai] INT NOT NULL DEFAULT 0,
    [Loai_hoa_don] NVARCHAR(50) NULL,
    [Ghi_chu] NVARCHAR(MAX) NULL,
    [Dia_chi_khach_hang] NVARCHAR(MAX) NULL,
    [Thong_tin_voucher] NVARCHAR(MAX) NULL,
    CONSTRAINT [PK_HoaDon] PRIMARY KEY CLUSTERED ([Hoa_don_id] ASC),
    CONSTRAINT [FK_HoaDon_NhanVien] FOREIGN KEY ([Nhan_vien_id]) REFERENCES [dbo].[NhanVien] ([Nhan_vien_id]),
    CONSTRAINT [FK_HoaDon_Voucher] FOREIGN KEY ([Voucher_id]) REFERENCES [dbo].[Voucher] ([Voucher_id]) ON DELETE SET NULL,
    CONSTRAINT [FK_HoaDon_Khach_hang] FOREIGN KEY ([Khach_hang_id]) REFERENCES [dbo].[Khach_hang] ([Khach_hang_id]),
    CONSTRAINT [FK_HoaDon_HinhThucThanhToan] FOREIGN KEY ([Hinh_thuc_thanh_toan_id]) REFERENCES [dbo].[HinhThucThanhToan] ([Hinh_thuc_thanh_toan_id]),
    CONSTRAINT [FK_HoaDon_DiaChiKhachHang] FOREIGN KEY ([Dia_chi_id]) REFERENCES [dbo].[DiaChiKhachHang] ([Dia_chi_id]),
    CONSTRAINT [CK_HoaDon_Tong_tien] CHECK ([Tong_tien_sau_khi_giam] >= 0),
    CONSTRAINT [CK_HoaDon_Email] CHECK ([Email_khach_hang] IS NULL OR [Email_khach_hang] LIKE '%@%.%')
);
GO

CREATE TABLE [dbo].[HoaDonChiTiet] (
    [Hoa_don_chi_tiet_id] INT IDENTITY(1,1) NOT NULL,
    [San_pham_id] INT NOT NULL,
    [Hoa_don_id] INT NOT NULL,
    [So_luong_san_pham] INT NOT NULL,
    [Gia] DECIMAL(18, 2) NOT NULL,
    CONSTRAINT [PK_HoaDonChiTiet] PRIMARY KEY CLUSTERED ([Hoa_don_chi_tiet_id] ASC),
    CONSTRAINT [FK_HoaDonChiTiet_SanPham] FOREIGN KEY ([San_pham_id]) REFERENCES [dbo].[SanPham] ([San_pham_id]),
    CONSTRAINT [FK_HoaDonChiTiet_HoaDon] FOREIGN KEY ([Hoa_don_id]) REFERENCES [dbo].[HoaDon] ([Hoa_don_id]) ON DELETE CASCADE,
    CONSTRAINT [CK_HoaDonChiTiet_SoLuong] CHECK ([So_luong_san_pham] > 0),
    CONSTRAINT [CK_HoaDonChiTiet_Gia] CHECK ([Gia] >= 0)
);
GO

CREATE TABLE [dbo].[ThanhToan] (
    [Thanh_toan_id] INT IDENTITY(1,1) NOT NULL,
    [Hinh_thuc_thanh_toan_id] INT NOT NULL,
    [Hoa_don_id] INT NOT NULL,
    [So_tien] DECIMAL(18, 2) NOT NULL,
    [Paid_at] DATETIME2 NULL,
    [Ma_giao_dich] NVARCHAR(255) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 0,
    CONSTRAINT [PK_ThanhToan] PRIMARY KEY CLUSTERED ([Thanh_toan_id] ASC),
    CONSTRAINT [FK_ThanhToan_HinhThucThanhToan] FOREIGN KEY ([Hinh_thuc_thanh_toan_id]) REFERENCES [dbo].[HinhThucThanhToan] ([Hinh_thuc_thanh_toan_id]),
    CONSTRAINT [FK_ThanhToan_HoaDon] FOREIGN KEY ([Hoa_don_id]) REFERENCES [dbo].[HoaDon] ([Hoa_don_id]) ON DELETE CASCADE,
    CONSTRAINT [CK_ThanhToan_So_tien] CHECK ([So_tien] >= 0)
);
GO

-- ===========================================================================
-- SECTION 4: SUPPLIER & IMPORT (NhaCungCap, PhieuNhap)
-- ===========================================================================

CREATE TABLE [dbo].[NhaCungCap](
    [Nha_cung_cap_id] INT IDENTITY(1,1) PRIMARY KEY,
    [Ten_nha_cung_cap] NVARCHAR(255) NOT NULL,
    [Nguoi_lien_he] NVARCHAR(255) NULL,
    [SDT] NVARCHAR(50) NULL,
    [Email] NVARCHAR(255) NULL,
    [Dia_chi] NVARCHAR(MAX) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2(7) NULL
);
GO

CREATE TABLE [dbo].[PhieuNhap](
    [Phieu_nhap_id] INT IDENTITY(1,1) PRIMARY KEY,
    [Ma_phieu_nhap] NVARCHAR(50) NOT NULL UNIQUE,
    [Nha_cung_cap_id] INT NOT NULL,
    [Nhan_vien_id] INT NOT NULL,
    [Ngay_nhap] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
    [Tong_tien] DECIMAL(18,2) NOT NULL DEFAULT 0,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ghi_chu] NVARCHAR(MAX) NULL,
    [Ngay_tao] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2(7) NULL,
    CONSTRAINT [FK_PhieuNhap_NhaCungCap] FOREIGN KEY ([Nha_cung_cap_id]) REFERENCES [dbo].[NhaCungCap]([Nha_cung_cap_id]),
    CONSTRAINT [FK_PhieuNhap_NhanVien] FOREIGN KEY ([Nhan_vien_id]) REFERENCES [dbo].[NhanVien]([Nhan_vien_id])
);
GO

CREATE TABLE [dbo].[PhieuNhapChiTiet](
    [Phieu_nhap_chi_tiet_id] INT IDENTITY(1,1) PRIMARY KEY,
    [Phieu_nhap_id] INT NOT NULL,
    [San_pham_id] INT NOT NULL,
    [So_luong_nhap] INT NOT NULL CHECK ([So_luong_nhap] > 0),
    [Don_gia_nhap] DECIMAL(18,2) NOT NULL CHECK ([Don_gia_nhap] >= 0),
    [Thanh_tien] AS ([So_luong_nhap] * [Don_gia_nhap]) PERSISTED,
    [Ghi_chu] NVARCHAR(MAX) NULL,
    CONSTRAINT [FK_PhieuNhapChiTiet_PhieuNhap] FOREIGN KEY ([Phieu_nhap_id]) REFERENCES [dbo].[PhieuNhap]([Phieu_nhap_id]) ON DELETE CASCADE,
    CONSTRAINT [FK_PhieuNhapChiTiet_SanPham] FOREIGN KEY ([San_pham_id]) REFERENCES [dbo].[SanPham]([San_pham_id])
);
GO

-- ===========================================================================
-- SECTION 5: WAREHOUSE EXPORT (PhieuXuat)
-- ===========================================================================

CREATE TABLE [dbo].[PhieuXuat](
    [Phieu_xuat_id] INT IDENTITY(1,1) PRIMARY KEY,
    [Ma_phieu_xuat] NVARCHAR(50) NOT NULL UNIQUE,
    [Nhan_vien_id] INT NOT NULL,
    [Ngay_xuat] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
    [Tong_tien] DECIMAL(18,2) NOT NULL DEFAULT 0,
    [Trang_thai] INT NOT NULL DEFAULT 0,
    [Ly_do] NVARCHAR(MAX) NULL,
    [Ghi_chu] NVARCHAR(MAX) NULL,
    [Ngay_tao] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2(7) NULL,
    CONSTRAINT [FK_PhieuXuat_NhanVien] FOREIGN KEY ([Nhan_vien_id]) REFERENCES [dbo].[NhanVien]([Nhan_vien_id])
);
GO

CREATE TABLE [dbo].[PhieuXuatChiTiet](
    [Phieu_xuat_chi_tiet_id] INT IDENTITY(1,1) PRIMARY KEY,
    [Phieu_xuat_id] INT NOT NULL,
    [San_pham_id] INT NOT NULL,
    [So_luong_xuat] INT NOT NULL CHECK ([So_luong_xuat] > 0),
    [Don_gia] DECIMAL(18,2) NOT NULL CHECK ([Don_gia] >= 0),
    [Thanh_tien] AS ([So_luong_xuat] * [Don_gia]) PERSISTED,
    [Ghi_chu] NVARCHAR(MAX) NULL,
    CONSTRAINT [FK_PhieuXuatChiTiet_PhieuXuat] FOREIGN KEY ([Phieu_xuat_id]) REFERENCES [dbo].[PhieuXuat]([Phieu_xuat_id]) ON DELETE CASCADE,
    CONSTRAINT [FK_PhieuXuatChiTiet_SanPham] FOREIGN KEY ([San_pham_id]) REFERENCES [dbo].[SanPham]([San_pham_id])
);
GO

-- ===========================================================================
-- SECTION 6: PROMOTION SCHEMA
-- ===========================================================================

CREATE TABLE [dbo].[Chuong_trinh_khuyen_mai] (
    [Chuong_trinh_khuyen_mai_id] INT IDENTITY(1,1) NOT NULL,
    [Ma_chuong_trinh] NVARCHAR(50) NOT NULL,
    [Ten_chuong_trinh] NVARCHAR(255) NOT NULL,
    [Mo_ta] NVARCHAR(MAX) NULL,
    [Loai_khuyen_mai] INT NOT NULL,
    [Loai_giam] INT NOT NULL,
    [Gia_tri_giam] DECIMAL(18, 2) NOT NULL,
    [Giam_toi_da] DECIMAL(18, 2) NULL,
    [Don_hang_toi_thieu] DECIMAL(18, 2) NULL,
    [Ngay_bat_dau] DATETIME2(7) NOT NULL,
    [Ngay_ket_thuc] DATETIME2(7) NOT NULL,
    [Gio_bat_dau] TIME(7) NULL,
    [Gio_ket_thuc] TIME(7) NULL,
    [Ap_dung_cung_nhieu_ctkm] BIT NOT NULL DEFAULT 0,
    [Tu_dong_ap_dung] BIT NOT NULL DEFAULT 0,
    [Tong_lien_hoa_don_ap_dung] NVARCHAR(MAX) NULL,
    [Ngay_trong_tuan] NVARCHAR(50) NULL,
    [Ngay_trong_thang] NVARCHAR(MAX) NULL,
    [Khach_hang_ap_dung] INT NULL,
    [Kenh_ban_ap_dung] NVARCHAR(MAX) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    [Ngay_tao] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
    [Ngay_cap_nhat] DATETIME2(7) NULL,
    CONSTRAINT [PK_Chuong_trinh_khuyen_mai] PRIMARY KEY CLUSTERED ([Chuong_trinh_khuyen_mai_id] ASC),
    CONSTRAINT [UQ_CTKM_Ma_Chuong_Trinh] UNIQUE ([Ma_chuong_trinh]),
    CONSTRAINT [CK_CTKM_Gia_Tri_Giam] CHECK ([Gia_tri_giam] >= 0),
    CONSTRAINT [CK_CTKM_Ngay] CHECK ([Ngay_ket_thuc] >= [Ngay_bat_dau])
);
GO

CREATE TABLE [dbo].[Chuong_trinh_khuyen_mai_chi_tiet] (
    [Chuong_trinh_khuyen_mai_chi_tiet_id] INT IDENTITY(1,1) NOT NULL,
    [Chuong_trinh_khuyen_mai_id] INT NOT NULL,
    [San_pham_id] INT NULL,
    [Danh_muc_san_pham_id] INT NULL,
    [So_luong_toi_thieu] INT NULL,
    [So_luong_toi_da] INT NULL,
    [Gia_tri_giam] DECIMAL(18, 2) NULL,
    [Trang_thai] INT NOT NULL DEFAULT 1,
    CONSTRAINT [PK_CTKM_Chi_Tiet] PRIMARY KEY CLUSTERED ([Chuong_trinh_khuyen_mai_chi_tiet_id] ASC),
    CONSTRAINT [FK_CTKM_Chi_Tiet_CTKM] FOREIGN KEY ([Chuong_trinh_khuyen_mai_id]) REFERENCES [dbo].[Chuong_trinh_khuyen_mai] ([Chuong_trinh_khuyen_mai_id]) ON DELETE CASCADE,
    CONSTRAINT [FK_CTKM_Chi_Tiet_San_Pham] FOREIGN KEY ([San_pham_id]) REFERENCES [dbo].[SanPham] ([San_pham_id]) ON DELETE SET NULL,
    CONSTRAINT [FK_CTKM_Chi_Tiet_Danh_Muc] FOREIGN KEY ([Danh_muc_san_pham_id]) REFERENCES [dbo].[Danh_muc_san_pham] ([Danh_muc_san_pham_id]) ON DELETE SET NULL
);
GO

CREATE TABLE [dbo].[Lich_su_ap_dung_khuyen_mai] (
    [Lich_su_id] INT IDENTITY(1,1) NOT NULL,
    [Chuong_trinh_khuyen_mai_id] INT NOT NULL,
    [Hoa_don_id] INT NOT NULL,
    [Gia_tri_giam] DECIMAL(18, 2) NOT NULL,
    [Ngay_ap_dung] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
    CONSTRAINT [PK_Lich_su_ap_dung] PRIMARY KEY CLUSTERED ([Lich_su_id] ASC),
    CONSTRAINT [FK_Lich_Su_CTKM] FOREIGN KEY ([Chuong_trinh_khuyen_mai_id]) REFERENCES [dbo].[Chuong_trinh_khuyen_mai] ([Chuong_trinh_khuyen_mai_id]),
    CONSTRAINT [FK_Lich_Su_Hoa_Don] FOREIGN KEY ([Hoa_don_id]) REFERENCES [dbo].[HoaDon] ([Hoa_don_id]) ON DELETE CASCADE
);
GO

-- ===========================================================================
-- SECTION 7: SEED DATA
-- ===========================================================================

-- 7.1 Default ChucVu + NhanVien (required for POS and warehouse)
SET IDENTITY_INSERT [dbo].[ChucVu] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[ChucVu] WHERE [Chuc_vu_id] = 1)
BEGIN
    INSERT INTO [dbo].[ChucVu] ([Chuc_vu_id], [Ten_chuc_vu], [Mo_ta_chuc_vu])
    VALUES (1, N'Quan ly', N'Nhan vien quan ly he thong');
END;
SET IDENTITY_INSERT [dbo].[ChucVu] OFF;
GO

SET IDENTITY_INSERT [dbo].[NhanVien] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[NhanVien] WHERE [Nhan_vien_id] = 1)
BEGIN
    INSERT INTO [dbo].[NhanVien] ([Nhan_vien_id], [Ho_ten], [Gioi_tinh], [SDT], [Email],
                                   [Dia_chi], [Ngay_sinh], [Chuc_vu_id], [Trang_thai], [Ngay_tao])
    VALUES (1, N'Admin', N'Nam', '0900000001', 'admin@shop.vn',
            N'Ha Noi', '1990-01-01', 1, 1, GETDATE());
END;
SET IDENTITY_INSERT [dbo].[NhanVien] OFF;
GO

-- 7.2 Sample HinhThucThanhToan
INSERT INTO [dbo].[HinhThucThanhToan] ([Ten_hinh_thuc], [Mo_ta])
VALUES (N'Tien mat', N'Thanh toan bang tien mat'),
       (N'Chuyen khoan', N'Chuyen khoan ngan hang');
GO

-- 7.3 Sample Danh muc san pham
INSERT INTO [dbo].[Danh_muc_san_pham] ([Ten_danh_muc], [Trang_thai], [Ngay_tao])
VALUES (N'Vot cau long', 1, CAST(GETDATE() AS DATE)),
       (N'Giay cau long', 1, CAST(GETDATE() AS DATE)),
       (N'Phu kien', 1, CAST(GETDATE() AS DATE)),
       (N'Quan ao', 1, CAST(GETDATE() AS DATE)),
       (N'Ba lo - Tui vot', 1, CAST(GETDATE() AS DATE));
GO

-- 7.4 Sample Mau sac
INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai])
VALUES (N'Do', '#FF0000', 1),
       (N'Xanh duong', '#0000FF', 1),
       (N'Den', '#000000', 1),
       (N'Trang', '#FFFFFF', 1),
       (N'Vang', '#FFD700', 1);
GO

-- 7.5 Sample San pham (5 products for testing)
INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
VALUES
(1, 1, N'Vot Yonex Astrox 99 Pro', 'HH01', 'SKU-HH01', '8934567890123', 2500000, 3500000, 15, N'Cay', N'Vot cao cap cho nguoi choi chuyen nghiep', 1),
(1, 3, N'Vot Lining Axforce 80', 'HH02', 'SKU-HH02', '8934567890130', 1800000, 2800000, 20, N'Cay', N'Vot cong thu toan dien', 1),
(2, 4, N'Giay Yonex Power Cushion 65Z', 'HH03', 'SKU-HH03', '8934567890147', 1200000, 1900000, 30, N'Doi', N'Giay cau long cao cap', 1),
(3, NULL, N'Cuoc cau long Yonex BG65', 'HH04', 'SKU-HH04', '8934567890154', 80000, 150000, 100, N'Cuon', N'Cuoc ben, pho bien', 1),
(4, 2, N'Ao cau long Yonex 2026', 'HH05', 'SKU-HH05', '8934567890161', 250000, 450000, 50, N'Cai', N'Ao thi dau chinh hang', 1);
GO

-- 7.6 Sample Khuyen mai (7 programs)
INSERT INTO [dbo].[Chuong_trinh_khuyen_mai] (
    [Ma_chuong_trinh], [Ten_chuong_trinh], [Mo_ta],
    [Loai_khuyen_mai], [Loai_giam], [Gia_tri_giam], [Giam_toi_da], [Don_hang_toi_thieu],
    [Ngay_bat_dau], [Ngay_ket_thuc],
    [Ap_dung_cung_nhieu_ctkm], [Tu_dong_ap_dung],
    [Khach_hang_ap_dung], [Kenh_ban_ap_dung], [Ngay_trong_tuan],
    [Trang_thai], [Ngay_tao]
) VALUES
(N'SALE10', N'Giam 10% hoa don tren 3 trieu', N'Giam 10% cho hoa don tren 3.000.000d', 1, 1, 10, 500000, 3000000, '2026-03-01', '2026-12-31', 0, 1, 1, N'[Facebook, Zalo]', N'[2,3,4,5,6]', 1, GETDATE()),
(N'GIAM100K', N'Giam 100k cho don hang tren 1 trieu', N'Giam 100.000d cho moi don hang tren 1.000.000d', 1, 2, 100000, NULL, 1000000, '2026-03-01', '2026-06-30', 1, 1, 1, N'[Facebook,Zalo,TikTok]', NULL, 1, GETDATE()),
(N'GIAM50K-SP', N'Giam 50k cho san pham vot cau long', N'Giam 50.000d cho cac san pham vot cau long', 2, 2, 50000, NULL, NULL, '2026-03-01', '2026-12-31', 1, 0, 1, N'[Facebook,Zalo]', NULL, 1, GETDATE()),
(N'TANG-QUA', N'Tang qua khi mua tren 5 trieu', N'Tang 1 ao thun khi mua hang tren 5.000.000d', 3, 2, 0, NULL, NULL, '2026-03-01', '2026-12-31', 1, 1, 1, NULL, NULL, 1, GETDATE()),
(N'DONGGIA99K', N'Dong gia 99k cho san pham sale', N'Tat ca san pham sale chi 99.000d', 4, 2, 99000, NULL, NULL, '2026-03-01', '2026-03-31', 0, 0, 1, NULL, NULL, 1, GETDATE()),
(N'OLD-PROMO', N'Chuong trinh da ket thuc', N'Chuong trinh nay da ket thuc', 1, 1, 15, NULL, NULL, '2025-01-01', '2025-12-31', 0, 1, 1, NULL, NULL, 0, GETDATE()),
(N'VIP20', N'Giam 20% cho khach VIP', N'Giam 20% cho khach hang VIP, toi da 1 trieu', 1, 1, 20, 1000000, NULL, '2026-03-01', '2026-12-31', 0, 1, 3, NULL, NULL, 1, GETDATE());
GO

PRINT N'Setup complete! Database sd50 is ready.';
GO
