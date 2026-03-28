-- ============================================================
-- 03_schema_product.sql
-- Tạo bảng: Danh_muc_san_pham, Mau_sac, Anh, SanPham
-- Phải chạy SAU 02_schema_core.sql
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Danh_muc_san_pham (Product Category)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Danh_muc_san_pham' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Danh_muc_san_pham (
        Danh_muc_san_pham_id  INT            NOT NULL IDENTITY(1,1),
        Ten_danh_muc          NVARCHAR(255)  NOT NULL,
        Trang_thai            INT            NOT NULL DEFAULT 1,
        Ngay_tao              DATE           NULL,
        Ngay_cap_nhat         DATE           NULL,
        CONSTRAINT PK_DanhMucSanPham PRIMARY KEY (Danh_muc_san_pham_id)
    );
    PRINT N'Bảng Danh_muc_san_pham đã được tạo.';
END
ELSE
    PRINT N'Bảng Danh_muc_san_pham đã tồn tại.';
GO

-- -------------------------------------------------------
-- Mau_sac (Color)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Mau_sac' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Mau_sac (
        Mau_sac_id      INT            NOT NULL IDENTITY(1,1),
        Ten_mau         NVARCHAR(255)  NOT NULL,
        Ma_mau_hex      NVARCHAR(50)   NOT NULL DEFAULT '#000000',
        Trang_thai      INT            NOT NULL DEFAULT 1,
        Ngay_tao        DATETIME2      NULL,
        Ngay_cap_nhat   DATETIME2      NULL,
        CONSTRAINT PK_MauSac PRIMARY KEY (Mau_sac_id)
    );
    PRINT N'Bảng Mau_sac đã được tạo.';
END
ELSE
    PRINT N'Bảng Mau_sac đã tồn tại.';
GO

-- -------------------------------------------------------
-- Anh (Image/Photo)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Anh' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Anh (
        Anh_id          INT            NOT NULL IDENTITY(1,1),
        Anh_url         NVARCHAR(MAX)  NOT NULL,
        Ten_file_goc    NVARCHAR(255)  NULL,
        Loai_nguon      NVARCHAR(100)  NULL,
        Kich_thuoc_byte BIGINT         NULL,
        Mime_type       NVARCHAR(100)  NULL,
        Mo_ta           NVARCHAR(MAX)  NULL,
        Thu_tu          INT            NOT NULL DEFAULT 0,
        Trang_thai      INT            NOT NULL DEFAULT 1,
        Ngay_tao        DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT PK_Anh PRIMARY KEY (Anh_id)
    );
    PRINT N'Bảng Anh đã được tạo.';
END
ELSE
    PRINT N'Bảng Anh đã tồn tại.';
GO

-- -------------------------------------------------------
-- SanPham (Product)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'SanPham' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.SanPham (
        San_pham_id             INT              NOT NULL IDENTITY(1,1),
        Danh_muc_san_pham_id    INT              NOT NULL,
        Mau_sac_id              INT              NULL,
        Anh_id                  INT              NULL,
        Ten_san_pham            NVARCHAR(255)    NOT NULL,
        Ma_san_pham             NVARCHAR(50)     NOT NULL,
        Sku                     NVARCHAR(50)     NOT NULL,
        Gia_nhap                DECIMAL(18,2)    NOT NULL,
        Gia_ban                 DECIMAL(18,2)    NOT NULL,
        So_luong_ton            INT              NOT NULL DEFAULT 0,
        Don_vi_tinh             NVARCHAR(50)     NULL,
        Barcode                 NVARCHAR(100)    NULL,
        Mo_ta                   NVARCHAR(MAX)    NULL,
        Trang_thai              INT              NOT NULL DEFAULT 1,
        Ngay_tao                DATETIME2        NOT NULL DEFAULT SYSDATETIME(),
        Ngay_sua                DATETIME2        NULL,
        CONSTRAINT PK_SanPham               PRIMARY KEY (San_pham_id),
        CONSTRAINT UQ_SanPham_Ma            UNIQUE (Ma_san_pham),
        CONSTRAINT UQ_SanPham_Sku           UNIQUE (Sku),
        CONSTRAINT FK_SanPham_DanhMuc       FOREIGN KEY (Danh_muc_san_pham_id) REFERENCES dbo.Danh_muc_san_pham (Danh_muc_san_pham_id),
        CONSTRAINT FK_SanPham_MauSac        FOREIGN KEY (Mau_sac_id)           REFERENCES dbo.Mau_sac (Mau_sac_id),
        CONSTRAINT FK_SanPham_Anh           FOREIGN KEY (Anh_id)               REFERENCES dbo.Anh (Anh_id)
    );
    PRINT N'Bảng SanPham đã được tạo.';
END
ELSE
    PRINT N'Bảng SanPham đã tồn tại.';
GO
