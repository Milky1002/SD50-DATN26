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
    VALUES (1, N'Quản lý', N'Nhân viên quản lý hệ thống');
END;
IF NOT EXISTS (SELECT 1 FROM [dbo].[ChucVu] WHERE [Chuc_vu_id] = 2)
BEGIN
    INSERT INTO [dbo].[ChucVu] ([Chuc_vu_id], [Ten_chuc_vu], [Mo_ta_chuc_vu])
    VALUES (2, N'Nhân viên', N'Nhân viên bán hàng');
END;
SET IDENTITY_INSERT [dbo].[ChucVu] OFF;
GO

-- 7.1b Admin account (password "admin@123" — plaintext, will be auto-hashed on first login)
SET IDENTITY_INSERT [dbo].[TaiKhoan] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[TaiKhoan] WHERE [Tai_khoan_id] = 1)
BEGIN
    INSERT INTO [dbo].[TaiKhoan] ([Tai_khoan_id], [User_name], [Pass_word], [Trang_thai])
    VALUES (1, N'admin', N'admin@123', 1);
END;
SET IDENTITY_INSERT [dbo].[TaiKhoan] OFF;
GO

-- Tài khoản nhân viên mẫu
IF NOT EXISTS (SELECT 1 FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien01')
    INSERT INTO [dbo].[TaiKhoan] ([User_name], [Pass_word], [Trang_thai]) VALUES (N'nhanvien01', N'admin@123', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien02')
    INSERT INTO [dbo].[TaiKhoan] ([User_name], [Pass_word], [Trang_thai]) VALUES (N'nhanvien02', N'admin@123', 1);
GO

SET IDENTITY_INSERT [dbo].[NhanVien] ON;
IF NOT EXISTS (SELECT 1 FROM [dbo].[NhanVien] WHERE [Nhan_vien_id] = 1)
BEGIN
    INSERT INTO [dbo].[NhanVien] ([Nhan_vien_id], [Ho_ten], [Gioi_tinh], [SDT], [Email],
                                   [Dia_chi], [Ngay_sinh], [Chuc_vu_id], [Trang_thai], [Tai_khoan_id], [Ngay_tao])
    VALUES (1, N'Nguyễn Văn Admin', N'Nam', '0900000001', 'admin@shop.vn',
            N'Số 1, Phố Huế, Quận Hai Bà Trưng, Hà Nội', '1990-01-01', 1, 1, 1, GETDATE());
END;
SET IDENTITY_INSERT [dbo].[NhanVien] OFF;
GO

-- Nhân viên mẫu
DECLARE @tkNV1 INT = (SELECT [Tai_khoan_id] FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien01');
IF @tkNV1 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[NhanVien] WHERE [Tai_khoan_id] = @tkNV1)
    INSERT INTO [dbo].[NhanVien] ([Ho_ten], [Gioi_tinh], [SDT], [Email], [Dia_chi], [Ngay_sinh], [Chuc_vu_id], [Trang_thai], [Tai_khoan_id])
    VALUES (N'Trần Thị Hương', N'Nữ', N'0912345678', N'huong.tran@shop.vn', N'Số 15, Đường Lê Lợi, Quận 1, TP. Hồ Chí Minh', '1995-05-15', 2, 1, @tkNV1);

DECLARE @tkNV2 INT = (SELECT [Tai_khoan_id] FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien02');
IF @tkNV2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[NhanVien] WHERE [Tai_khoan_id] = @tkNV2)
    INSERT INTO [dbo].[NhanVien] ([Ho_ten], [Gioi_tinh], [SDT], [Email], [Dia_chi], [Ngay_sinh], [Chuc_vu_id], [Trang_thai], [Tai_khoan_id])
    VALUES (N'Lê Minh Đức', N'Nam', N'0987654321', N'duc.le@shop.vn', N'Số 42, Phố Bạch Mai, Quận Hai Bà Trưng, Hà Nội', '1998-08-20', 2, 1, @tkNV2);
GO

-- 7.2 Hình thức thanh toán
INSERT INTO [dbo].[HinhThucThanhToan] ([Ten_hinh_thuc], [Mo_ta])
VALUES (N'Tiền mặt', N'Thanh toán bằng tiền mặt'),
       (N'Chuyển khoản', N'Chuyển khoản ngân hàng'),
       (N'Thẻ tín dụng', N'Thanh toán bằng thẻ tín dụng'),
       (N'Ví điện tử', N'Thanh toán qua ví điện tử');
GO

-- 7.3 Danh mục sản phẩm
INSERT INTO [dbo].[Danh_muc_san_pham] ([Ten_danh_muc], [Trang_thai], [Ngay_tao])
VALUES (N'Vợt cầu lông', 1, CAST(GETDATE() AS DATE)),
       (N'Giày cầu lông', 1, CAST(GETDATE() AS DATE)),
       (N'Phụ kiện', 1, CAST(GETDATE() AS DATE)),
       (N'Quần áo', 1, CAST(GETDATE() AS DATE)),
       (N'Ba lô - Túi vợt', 1, CAST(GETDATE() AS DATE));
GO

-- 7.4 Màu sắc
INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai])
VALUES (N'Đỏ', '#FF0000', 1),
       (N'Xanh dương', '#0000FF', 1),
       (N'Đen', '#000000', 1),
       (N'Trắng', '#FFFFFF', 1),
       (N'Vàng', '#FFD700', 1),
       (N'Xanh lá', '#00FF00', 1),
       (N'Cam', '#FF8C00', 1),
       (N'Tím', '#800080', 1),
       (N'Hồng', '#FF69B4', 1),
       (N'Bạc', '#C0C0C0', 1);
GO

-- 7.5 Sản phẩm mẫu (12 sản phẩm)
INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
VALUES
(1, 1, N'Vợt Yonex Astrox 99 Pro', 'HH01', 'SKU-HH01', '8934567890123', 2500000, 3500000, 15, N'Cây', N'Vợt cao cấp cho người chơi chuyên nghiệp, thiết kế tấn công mạnh mẽ', 1),
(1, 3, N'Vợt Lining Axforce 80', 'HH02', 'SKU-HH02', '8934567890130', 1800000, 2800000, 20, N'Cây', N'Vợt công thủ toàn diện, phù hợp nhiều lối chơi', 1),
(2, 4, N'Giày Yonex Power Cushion 65Z', 'HH03', 'SKU-HH03', '8934567890147', 1200000, 1900000, 30, N'Đôi', N'Giày cầu lông cao cấp, đệm êm, bám sân tốt', 1),
(3, NULL, N'Cước cầu lông Yonex BG65', 'HH04', 'SKU-HH04', '8934567890154', 80000, 150000, 100, N'Cuộn', N'Cước bền, phổ biến, phù hợp người chơi phong trào', 1),
(4, 2, N'Áo cầu lông Yonex 2026', 'HH05', 'SKU-HH05', '8934567890161', 250000, 450000, 50, N'Cái', N'Áo thi đấu chính hãng, chất liệu thoáng mát', 1),
(1, 3, N'Vợt Yonex Nanoflare 700', 'HH06', 'SKU-HH06', '8934567890178', 2200000, 3200000, 12, N'Cây', N'Vợt siêu nhẹ, tốc độ cao, phù hợp lối chơi phòng thủ phản công', 1),
(1, 2, N'Vợt Victor Thruster K 9900', 'HH07', 'SKU-HH07', '8934567890185', 2800000, 3800000, 8, N'Cây', N'Vợt tấn công hàng đầu của Victor, cân bằng tốt', 1),
(2, 1, N'Giày Yonex Aerus Z', 'HH08', 'SKU-HH08', '8934567890192', 1500000, 2300000, 25, N'Đôi', N'Giày siêu nhẹ, đệm Power Cushion+, phù hợp chơi đơn', 1),
(3, NULL, N'Quấn cán Yonex Super Grap (3 cuộn)', 'HH09', 'SKU-HH09', '8934567890208', 50000, 95000, 200, N'Bộ', N'Quấn cán chống trượt, mềm mại, thấm mồ hôi tốt', 1),
(4, 4, N'Quần cầu lông Yonex 2026', 'HH10', 'SKU-HH10', '8934567890215', 200000, 380000, 60, N'Cái', N'Quần thể thao chính hãng, co giãn tốt, thoáng khí', 1),
(5, 3, N'Túi vợt Yonex BA82231W (6 vợt)', 'HH11', 'SKU-HH11', '8934567890222', 800000, 1350000, 15, N'Cái', N'Túi vợt cao cấp 6 ngăn, chống sốc, chống nước', 1),
(3, NULL, N'Cầu lông Yonex Mavis 350 (Hộp 6 quả)', 'HH12', 'SKU-HH12', '8934567890239', 60000, 120000, 150, N'Hộp', N'Cầu lông nhựa chất lượng cao, bay ổn định, bền bỉ', 1);
GO

-- 7.6 Chương trình khuyến mãi mẫu (7 chương trình)
INSERT INTO [dbo].[Chuong_trinh_khuyen_mai] (
    [Ma_chuong_trinh], [Ten_chuong_trinh], [Mo_ta],
    [Loai_khuyen_mai], [Loai_giam], [Gia_tri_giam], [Giam_toi_da], [Don_hang_toi_thieu],
    [Ngay_bat_dau], [Ngay_ket_thuc],
    [Ap_dung_cung_nhieu_ctkm], [Tu_dong_ap_dung],
    [Khach_hang_ap_dung], [Kenh_ban_ap_dung], [Ngay_trong_tuan],
    [Trang_thai], [Ngay_tao]
) VALUES
(N'SALE10', N'Giảm 10% hoá đơn trên 3 triệu', N'Giảm 10% cho hoá đơn có giá trị trên 3.000.000đ', 1, 1, 10, 500000, 3000000, '2026-03-01', '2026-12-31', 0, 1, 1, N'[Facebook, Zalo]', N'[2,3,4,5,6]', 1, GETDATE()),
(N'GIAM100K', N'Giảm 100K cho đơn hàng trên 1 triệu', N'Giảm 100.000đ cho mỗi đơn hàng có giá trị trên 1.000.000đ', 1, 2, 100000, NULL, 1000000, '2026-03-01', '2026-06-30', 1, 1, 1, N'[Facebook,Zalo,TikTok]', NULL, 1, GETDATE()),
(N'GIAM50K-SP', N'Giảm 50K cho sản phẩm vợt cầu lông', N'Giảm 50.000đ cho các sản phẩm vợt cầu lông', 2, 2, 50000, NULL, NULL, '2026-03-01', '2026-12-31', 1, 0, 1, N'[Facebook,Zalo]', NULL, 1, GETDATE()),
(N'TANG-QUA', N'Tặng quà khi mua trên 5 triệu', N'Tặng 1 áo thun khi mua hàng trên 5.000.000đ', 3, 2, 0, NULL, NULL, '2026-03-01', '2026-12-31', 1, 1, 1, NULL, NULL, 1, GETDATE()),
(N'DONGGIA99K', N'Đồng giá 99K cho sản phẩm sale', N'Tất cả sản phẩm sale chỉ 99.000đ', 4, 2, 99000, NULL, NULL, '2026-03-01', '2026-03-31', 0, 0, 1, NULL, NULL, 1, GETDATE()),
(N'OLD-PROMO', N'Chương trình đã kết thúc', N'Chương trình này đã kết thúc', 1, 1, 15, NULL, NULL, '2025-01-01', '2025-12-31', 0, 1, 1, NULL, NULL, 0, GETDATE()),
(N'VIP20', N'Giảm 20% cho khách VIP', N'Giảm 20% cho khách hàng VIP, tối đa 1 triệu', 1, 1, 20, 1000000, NULL, '2026-03-01', '2026-12-31', 0, 1, 3, NULL, NULL, 1, GETDATE());
GO

-- 7.7 Khách hàng mẫu
INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
VALUES
(N'Nguyễn Thị Lan', N'0901234567', N'lan.nguyen@gmail.com', 1, N'Số 10, Đường Nguyễn Trãi, Quận Thanh Xuân, Hà Nội'),
(N'Phạm Văn Hùng', N'0918765432', N'hung.pham@gmail.com', 1, N'Số 25, Phố Hàng Bông, Quận Hoàn Kiếm, Hà Nội'),
(N'Trương Thị Mai', N'0932456789', N'mai.truong@gmail.com', 1, N'Số 8, Đường Trần Phú, Quận Hải Châu, TP. Đà Nẵng'),
(N'Hoàng Đức Anh', N'0945678901', N'anh.hoang@gmail.com', 1, N'Số 30, Đường Lý Tự Trọng, Quận 1, TP. Hồ Chí Minh'),
(N'Vũ Thị Hồng Nhung', N'0956789012', N'nhung.vu@gmail.com', 1, N'Số 12, Phố Nguyễn Du, TP. Hải Phòng'),
(N'Đặng Quốc Tuấn', N'0967890123', N'tuan.dang@gmail.com', 1, N'Số 5, Đường Võ Nguyên Giáp, TP. Huế, Thừa Thiên Huế'),
(N'Bùi Thị Thanh Hà', N'0978901234', N'ha.bui@gmail.com', 1, N'Số 18, Phố Phan Chu Trinh, Quận Hoàn Kiếm, Hà Nội'),
(N'Lý Minh Quân', N'0989012345', N'quan.ly@gmail.com', 1, N'Số 22, Đường Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh');
GO

PRINT N'';
PRINT N'========================================';
PRINT N'  ✓ Thiết lập hoàn tất! Database sd50 sẵn sàng.';
PRINT N'  Tài khoản: admin / admin@123';
PRINT N'  Nhân viên: nhanvien01 / admin@123';
PRINT N'  Nhân viên: nhanvien02 / admin@123';
PRINT N'========================================';
GO
