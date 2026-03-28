-- ============================================================
-- 09_schema_cart.sql
-- Tạo bảng: Gio_hang, Gio_hang_chi_tiet
-- Phải chạy SAU 04_schema_customer.sql, 03_schema_product.sql
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Gio_hang (Shopping Cart)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Gio_hang' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Gio_hang (
        Gio_hang_id     INT            NOT NULL IDENTITY(1,1),
        Khach_hang_id   INT            NULL,
        Session_id      NVARCHAR(100)  NULL,
        Ngay_tao        DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat   DATETIME2      NULL,
        CONSTRAINT PK_GioHang           PRIMARY KEY (Gio_hang_id),
        CONSTRAINT FK_GioHang_KhachHang FOREIGN KEY (Khach_hang_id) REFERENCES dbo.Khach_hang (Khach_hang_id)
    );
    PRINT N'Bảng Gio_hang đã được tạo.';
END
ELSE
    PRINT N'Bảng Gio_hang đã tồn tại.';
GO

-- -------------------------------------------------------
-- Gio_hang_chi_tiet (Cart Item)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Gio_hang_chi_tiet' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Gio_hang_chi_tiet (
        Gio_hang_chi_tiet_id  INT            NOT NULL IDENTITY(1,1),
        Gio_hang_id           INT            NOT NULL,
        San_pham_id           INT            NOT NULL,
        So_luong              INT            NOT NULL DEFAULT 1,
        Gia_tai_thoi_diem     DECIMAL(18,2)  NULL,
        CONSTRAINT PK_GioHangChiTiet        PRIMARY KEY (Gio_hang_chi_tiet_id),
        CONSTRAINT FK_GHCT_GioHang          FOREIGN KEY (Gio_hang_id)  REFERENCES dbo.Gio_hang (Gio_hang_id),
        CONSTRAINT FK_GHCT_SanPham          FOREIGN KEY (San_pham_id)  REFERENCES dbo.SanPham (San_pham_id)
    );
    PRINT N'Bảng Gio_hang_chi_tiet đã được tạo.';
END
ELSE
    PRINT N'Bảng Gio_hang_chi_tiet đã tồn tại.';
GO
