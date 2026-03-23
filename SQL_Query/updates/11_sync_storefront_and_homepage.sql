USE [sd50];
GO

-- ============================================================
-- 11. Đồng bộ storefront / auth / cart / homepage / upload ảnh
-- Idempotent: an toàn khi chạy nhiều lần trên môi trường production
-- ============================================================

PRINT N'=== Bắt đầu đồng bộ storefront và homepage ===';
GO

-- ------------------------------------------------------------
-- 1) Khach_hang: thêm mật khẩu cho customer auth
-- ------------------------------------------------------------
IF COL_LENGTH('dbo.Khach_hang', 'Mat_khau') IS NULL
BEGIN
    ALTER TABLE dbo.Khach_hang
    ADD Mat_khau NVARCHAR(255) NULL;

    PRINT N'✓ Đã thêm cột dbo.Khach_hang.Mat_khau';
END
ELSE
BEGIN
    PRINT N'- Cột dbo.Khach_hang.Mat_khau đã tồn tại';
END
GO

-- ------------------------------------------------------------
-- 2) Bảng ảnh: bổ sung metadata phục vụ upload file nội bộ
-- ------------------------------------------------------------
IF COL_LENGTH('dbo.Anh', 'Ten_file_goc') IS NULL
BEGIN
    ALTER TABLE dbo.Anh
    ADD Ten_file_goc NVARCHAR(255) NULL;
    PRINT N'✓ Đã thêm cột dbo.Anh.Ten_file_goc';
END
GO

IF COL_LENGTH('dbo.Anh', 'Loai_nguon') IS NULL
BEGIN
    ALTER TABLE dbo.Anh
    ADD Loai_nguon NVARCHAR(30) NOT NULL CONSTRAINT DF_Anh_Loai_nguon DEFAULT N'url';
    PRINT N'✓ Đã thêm cột dbo.Anh.Loai_nguon';
END
GO

IF COL_LENGTH('dbo.Anh', 'Kich_thuoc_byte') IS NULL
BEGIN
    ALTER TABLE dbo.Anh
    ADD Kich_thuoc_byte BIGINT NULL;
    PRINT N'✓ Đã thêm cột dbo.Anh.Kich_thuoc_byte';
END
GO

IF COL_LENGTH('dbo.Anh', 'Mime_type') IS NULL
BEGIN
    ALTER TABLE dbo.Anh
    ADD Mime_type NVARCHAR(100) NULL;
    PRINT N'✓ Đã thêm cột dbo.Anh.Mime_type';
END
GO

-- ------------------------------------------------------------
-- 3) Giỏ hàng storefront (đúng tên bảng/entity hiện tại)
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.Gio_hang', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Gio_hang (
        Gio_hang_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        Khach_hang_id INT NULL,
        Session_id    NVARCHAR(100) NULL,
        Ngay_tao      DATETIME2 NOT NULL CONSTRAINT DF_Gio_hang_Ngay_tao DEFAULT GETDATE(),
        Ngay_cap_nhat DATETIME2 NULL,
        CONSTRAINT FK_Gio_hang_Khach_hang FOREIGN KEY (Khach_hang_id)
            REFERENCES dbo.Khach_hang(Khach_hang_id)
    );

    PRINT N'✓ Đã tạo bảng dbo.Gio_hang';
END
ELSE
BEGIN
    PRINT N'- Bảng dbo.Gio_hang đã tồn tại';
END
GO

IF COL_LENGTH('dbo.Gio_hang', 'Session_id') IS NULL
BEGIN
    ALTER TABLE dbo.Gio_hang ADD Session_id NVARCHAR(100) NULL;
    PRINT N'✓ Đã thêm cột dbo.Gio_hang.Session_id';
END
GO

IF COL_LENGTH('dbo.Gio_hang', 'Ngay_cap_nhat') IS NULL
BEGIN
    ALTER TABLE dbo.Gio_hang ADD Ngay_cap_nhat DATETIME2 NULL;
    PRINT N'✓ Đã thêm cột dbo.Gio_hang.Ngay_cap_nhat';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Gio_hang_Session_id'
      AND object_id = OBJECT_ID('dbo.Gio_hang')
)
BEGIN
    CREATE NONCLUSTERED INDEX IX_Gio_hang_Session_id
        ON dbo.Gio_hang(Session_id)
        WHERE Session_id IS NOT NULL;
    PRINT N'✓ Đã tạo index IX_Gio_hang_Session_id';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Gio_hang_Khach_hang_id'
      AND object_id = OBJECT_ID('dbo.Gio_hang')
)
BEGIN
    CREATE NONCLUSTERED INDEX IX_Gio_hang_Khach_hang_id
        ON dbo.Gio_hang(Khach_hang_id)
        WHERE Khach_hang_id IS NOT NULL;
    PRINT N'✓ Đã tạo index IX_Gio_hang_Khach_hang_id';
END
GO

IF OBJECT_ID('dbo.Gio_hang_chi_tiet', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Gio_hang_chi_tiet (
        Gio_hang_chi_tiet_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        Gio_hang_id          INT NOT NULL,
        San_pham_id          INT NOT NULL,
        So_luong             INT NOT NULL CONSTRAINT DF_Gio_hang_CT_So_luong DEFAULT 1,
        Gia_tai_thoi_diem    DECIMAL(18,2) NULL,
        CONSTRAINT FK_Gio_hang_CT_Gio_hang FOREIGN KEY (Gio_hang_id)
            REFERENCES dbo.Gio_hang(Gio_hang_id) ON DELETE CASCADE,
        CONSTRAINT FK_Gio_hang_CT_San_pham FOREIGN KEY (San_pham_id)
            REFERENCES dbo.SanPham(San_pham_id),
        CONSTRAINT CK_Gio_hang_CT_So_luong CHECK (So_luong > 0)
    );

    PRINT N'✓ Đã tạo bảng dbo.Gio_hang_chi_tiet';
END
ELSE
BEGIN
    PRINT N'- Bảng dbo.Gio_hang_chi_tiet đã tồn tại';
END
GO

IF COL_LENGTH('dbo.Gio_hang_chi_tiet', 'Gia_tai_thoi_diem') IS NULL
BEGIN
    ALTER TABLE dbo.Gio_hang_chi_tiet ADD Gia_tai_thoi_diem DECIMAL(18,2) NULL;
    PRINT N'✓ Đã thêm cột dbo.Gio_hang_chi_tiet.Gia_tai_thoi_diem';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Gio_hang_chi_tiet_Gio_hang_id'
      AND object_id = OBJECT_ID('dbo.Gio_hang_chi_tiet')
)
BEGIN
    CREATE NONCLUSTERED INDEX IX_Gio_hang_chi_tiet_Gio_hang_id
        ON dbo.Gio_hang_chi_tiet(Gio_hang_id);
    PRINT N'✓ Đã tạo index IX_Gio_hang_chi_tiet_Gio_hang_id';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'UX_Gio_hang_chi_tiet_Gio_hang_San_pham'
      AND object_id = OBJECT_ID('dbo.Gio_hang_chi_tiet')
)
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UX_Gio_hang_chi_tiet_Gio_hang_San_pham
        ON dbo.Gio_hang_chi_tiet(Gio_hang_id, San_pham_id);
    PRINT N'✓ Đã tạo unique index UX_Gio_hang_chi_tiet_Gio_hang_San_pham';
END
GO

-- ------------------------------------------------------------
-- 4) Homepage configuration: danh mục nổi bật + sản phẩm HOT
-- ------------------------------------------------------------
IF OBJECT_ID('dbo.Trang_chu_danh_muc_noi_bat', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Trang_chu_danh_muc_noi_bat (
        Trang_chu_danh_muc_noi_bat_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        Danh_muc_san_pham_id          INT NOT NULL,
        Thu_tu                        INT NOT NULL CONSTRAINT DF_TC_DMNB_Thu_tu DEFAULT 0,
        So_luong_hien_thi             INT NOT NULL CONSTRAINT DF_TC_DMNB_So_luong DEFAULT 20,
        Trang_thai                    INT NOT NULL CONSTRAINT DF_TC_DMNB_Trang_thai DEFAULT 1,
        Ngay_tao                      DATETIME2 NOT NULL CONSTRAINT DF_TC_DMNB_Ngay_tao DEFAULT GETDATE(),
        Ngay_cap_nhat                 DATETIME2 NULL,
        CONSTRAINT FK_TC_DMNB_Danh_muc FOREIGN KEY (Danh_muc_san_pham_id)
            REFERENCES dbo.Danh_muc_san_pham(Danh_muc_san_pham_id),
        CONSTRAINT CK_TC_DMNB_So_luong CHECK (So_luong_hien_thi BETWEEN 1 AND 50)
    );

    PRINT N'✓ Đã tạo bảng dbo.Trang_chu_danh_muc_noi_bat';
END
ELSE
BEGIN
    PRINT N'- Bảng dbo.Trang_chu_danh_muc_noi_bat đã tồn tại';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'UX_TC_DMNB_Danh_muc'
      AND object_id = OBJECT_ID('dbo.Trang_chu_danh_muc_noi_bat')
)
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UX_TC_DMNB_Danh_muc
        ON dbo.Trang_chu_danh_muc_noi_bat(Danh_muc_san_pham_id);
    PRINT N'✓ Đã tạo unique index UX_TC_DMNB_Danh_muc';
END
GO

IF OBJECT_ID('dbo.Trang_chu_san_pham_hot', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Trang_chu_san_pham_hot (
        Trang_chu_san_pham_hot_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        San_pham_id               INT NOT NULL,
        Thu_tu                    INT NOT NULL CONSTRAINT DF_TC_SPH_Thu_tu DEFAULT 0,
        Trang_thai                INT NOT NULL CONSTRAINT DF_TC_SPH_Trang_thai DEFAULT 1,
        Ngay_tao                  DATETIME2 NOT NULL CONSTRAINT DF_TC_SPH_Ngay_tao DEFAULT GETDATE(),
        Ngay_cap_nhat             DATETIME2 NULL,
        CONSTRAINT FK_TC_SPH_San_pham FOREIGN KEY (San_pham_id)
            REFERENCES dbo.SanPham(San_pham_id)
    );

    PRINT N'✓ Đã tạo bảng dbo.Trang_chu_san_pham_hot';
END
ELSE
BEGIN
    PRINT N'- Bảng dbo.Trang_chu_san_pham_hot đã tồn tại';
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'UX_TC_SPH_San_pham'
      AND object_id = OBJECT_ID('dbo.Trang_chu_san_pham_hot')
)
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UX_TC_SPH_San_pham
        ON dbo.Trang_chu_san_pham_hot(San_pham_id);
    PRINT N'✓ Đã tạo unique index UX_TC_SPH_San_pham';
END
GO

PRINT N'=== Hoàn tất đồng bộ storefront và homepage ===';
GO
