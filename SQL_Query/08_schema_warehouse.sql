-- ============================================================
-- 08_schema_warehouse.sql
-- Tạo bảng: NhaCungCap, PhieuNhap, PhieuNhapChiTiet, PhieuXuat, PhieuXuatChiTiet
-- Phải chạy SAU 02_schema_core.sql, 03_schema_product.sql
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- NhaCungCap (Supplier)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'NhaCungCap' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.NhaCungCap (
        Nha_cung_cap_id  INT            NOT NULL IDENTITY(1,1),
        Ten_nha_cung_cap NVARCHAR(255)  NOT NULL,
        Nguoi_lien_he    NVARCHAR(255)  NULL,
        SDT              NVARCHAR(50)   NULL,
        Email            NVARCHAR(255)  NULL,
        Dia_chi          NVARCHAR(MAX)  NULL,
        Trang_thai       INT            NOT NULL DEFAULT 1,
        Ngay_tao         DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat    DATETIME2      NULL,
        CONSTRAINT PK_NhaCungCap PRIMARY KEY (Nha_cung_cap_id)
    );
    PRINT N'Bảng NhaCungCap đã được tạo.';
END
ELSE
    PRINT N'Bảng NhaCungCap đã tồn tại.';
GO

-- -------------------------------------------------------
-- PhieuNhap (Import Receipt)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'PhieuNhap' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.PhieuNhap (
        Phieu_nhap_id    INT            NOT NULL IDENTITY(1,1),
        Ma_phieu_nhap    NVARCHAR(50)   NOT NULL,
        Nha_cung_cap_id  INT            NOT NULL,
        Nhan_vien_id     INT            NOT NULL,
        Ngay_nhap        DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Tong_tien        DECIMAL(18,2)  NOT NULL DEFAULT 0,
        Trang_thai       INT            NOT NULL DEFAULT 1,
        Ghi_chu          NVARCHAR(MAX)  NULL,
        Ngay_tao         DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat    DATETIME2      NULL,
        CONSTRAINT PK_PhieuNhap             PRIMARY KEY (Phieu_nhap_id),
        CONSTRAINT UQ_PhieuNhap_Ma          UNIQUE (Ma_phieu_nhap),
        CONSTRAINT FK_PhieuNhap_NCC         FOREIGN KEY (Nha_cung_cap_id) REFERENCES dbo.NhaCungCap (Nha_cung_cap_id),
        CONSTRAINT FK_PhieuNhap_NV          FOREIGN KEY (Nhan_vien_id)    REFERENCES dbo.NhanVien (Nhan_vien_id)
    );
    PRINT N'Bảng PhieuNhap đã được tạo.';
END
ELSE
    PRINT N'Bảng PhieuNhap đã tồn tại.';
GO

-- -------------------------------------------------------
-- PhieuNhapChiTiet (Import Receipt Detail)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'PhieuNhapChiTiet' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.PhieuNhapChiTiet (
        Phieu_nhap_chi_tiet_id  INT            NOT NULL IDENTITY(1,1),
        Phieu_nhap_id           INT            NOT NULL,
        San_pham_id             INT            NOT NULL,
        So_luong_nhap           INT            NOT NULL,
        Don_gia_nhap            DECIMAL(18,2)  NOT NULL,
        Ghi_chu                 NVARCHAR(MAX)  NULL,
        CONSTRAINT PK_PhieuNhapChiTiet      PRIMARY KEY (Phieu_nhap_chi_tiet_id),
        CONSTRAINT FK_PNCT_PhieuNhap        FOREIGN KEY (Phieu_nhap_id) REFERENCES dbo.PhieuNhap (Phieu_nhap_id),
        CONSTRAINT FK_PNCT_SanPham          FOREIGN KEY (San_pham_id)   REFERENCES dbo.SanPham (San_pham_id)
    );
    PRINT N'Bảng PhieuNhapChiTiet đã được tạo.';
END
ELSE
    PRINT N'Bảng PhieuNhapChiTiet đã tồn tại.';
GO

-- -------------------------------------------------------
-- PhieuXuat (Export Receipt)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'PhieuXuat' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.PhieuXuat (
        Phieu_xuat_id   INT            NOT NULL IDENTITY(1,1),
        Ma_phieu_xuat   NVARCHAR(50)   NOT NULL,
        Nhan_vien_id    INT            NOT NULL,
        Ngay_xuat       DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Tong_tien       DECIMAL(18,2)  NOT NULL DEFAULT 0,
        Trang_thai      INT            NOT NULL DEFAULT 0,
        Ly_do           NVARCHAR(MAX)  NULL,
        Ghi_chu         NVARCHAR(MAX)  NULL,
        Ngay_tao        DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat   DATETIME2      NULL,
        CONSTRAINT PK_PhieuXuat             PRIMARY KEY (Phieu_xuat_id),
        CONSTRAINT UQ_PhieuXuat_Ma          UNIQUE (Ma_phieu_xuat),
        CONSTRAINT FK_PhieuXuat_NV          FOREIGN KEY (Nhan_vien_id) REFERENCES dbo.NhanVien (Nhan_vien_id)
    );
    PRINT N'Bảng PhieuXuat đã được tạo.';
END
ELSE
    PRINT N'Bảng PhieuXuat đã tồn tại.';
GO

-- -------------------------------------------------------
-- PhieuXuatChiTiet (Export Receipt Detail)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'PhieuXuatChiTiet' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.PhieuXuatChiTiet (
        Phieu_xuat_chi_tiet_id  INT            NOT NULL IDENTITY(1,1),
        Phieu_xuat_id           INT            NOT NULL,
        San_pham_id             INT            NOT NULL,
        So_luong_xuat           INT            NOT NULL,
        Don_gia                 DECIMAL(18,2)  NOT NULL,
        Ghi_chu                 NVARCHAR(MAX)  NULL,
        CONSTRAINT PK_PhieuXuatChiTiet      PRIMARY KEY (Phieu_xuat_chi_tiet_id),
        CONSTRAINT FK_PXCT_PhieuXuat        FOREIGN KEY (Phieu_xuat_id) REFERENCES dbo.PhieuXuat (Phieu_xuat_id),
        CONSTRAINT FK_PXCT_SanPham          FOREIGN KEY (San_pham_id)   REFERENCES dbo.SanPham (San_pham_id)
    );
    PRINT N'Bảng PhieuXuatChiTiet đã được tạo.';
END
ELSE
    PRINT N'Bảng PhieuXuatChiTiet đã tồn tại.';
GO
