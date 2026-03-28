-- ============================================================
-- 02_schema_core.sql
-- Tạo bảng lõi: ChucVu, TaiKhoan, NhanVien
-- Phải chạy SAU 01_create_database.sql
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- ChucVu (Position)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'ChucVu' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.ChucVu (
        Chuc_vu_id   INT            NOT NULL IDENTITY(1,1),
        Ten_chuc_vu  NVARCHAR(255)  NOT NULL,
        CONSTRAINT PK_ChucVu PRIMARY KEY (Chuc_vu_id)
    );
    PRINT N'Bảng ChucVu đã được tạo.';
END
ELSE
    PRINT N'Bảng ChucVu đã tồn tại.';
GO

-- -------------------------------------------------------
-- TaiKhoan (Account)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'TaiKhoan' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.TaiKhoan (
        Tai_khoan_id    INT            NOT NULL IDENTITY(1,1),
        User_name       NVARCHAR(255)  NOT NULL,
        Pass_word       NVARCHAR(255)  NOT NULL,
        Trang_thai      INT            NOT NULL DEFAULT 1,
        Role_code       NVARCHAR(50)   NOT NULL DEFAULT 'STAFF',
        Email           NVARCHAR(255)  NULL,
        Ho_ten          NVARCHAR(255)  NULL,
        So_dien_thoai   NVARCHAR(50)   NULL,
        Ngay_tao        DATETIME2      NULL,
        Ngay_cap_nhat   DATETIME2      NULL,
        CONSTRAINT PK_TaiKhoan        PRIMARY KEY (Tai_khoan_id),
        CONSTRAINT UQ_TaiKhoan_Username UNIQUE (User_name)
    );
    PRINT N'Bảng TaiKhoan đã được tạo.';
END
ELSE
    PRINT N'Bảng TaiKhoan đã tồn tại.';
GO

-- -------------------------------------------------------
-- NhanVien (Staff)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'NhanVien' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.NhanVien (
        Nhan_vien_id    INT            NOT NULL IDENTITY(1,1),
        Ho_ten          NVARCHAR(255)  NOT NULL,
        Gioi_tinh       NVARCHAR(50)   NULL,
        SDT             NVARCHAR(50)   NULL,
        Email           NVARCHAR(255)  NULL,
        Dia_chi         NVARCHAR(MAX)  NULL,
        Ngay_sinh       DATE           NULL,
        Chuc_vu_id      INT            NOT NULL,
        Tai_khoan_id    INT            NULL,
        Trang_thai      INT            NOT NULL DEFAULT 1,
        Ngay_tao        DATETIME2      NULL,
        Ngay_cap_nhat   DATETIME2      NULL,
        CONSTRAINT PK_NhanVien         PRIMARY KEY (Nhan_vien_id),
        CONSTRAINT FK_NhanVien_ChucVu  FOREIGN KEY (Chuc_vu_id)   REFERENCES dbo.ChucVu (Chuc_vu_id),
        CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (Tai_khoan_id) REFERENCES dbo.TaiKhoan (Tai_khoan_id)
    );
    PRINT N'Bảng NhanVien đã được tạo.';
END
ELSE
    PRINT N'Bảng NhanVien đã tồn tại.';
GO
