-- ============================================================
-- 04_schema_customer.sql
-- Tạo bảng: Khach_hang
-- Phải chạy SAU 02_schema_core.sql
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Khach_hang (Customer)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Khach_hang' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Khach_hang (
        Khach_hang_id       INT            NOT NULL IDENTITY(1,1),
        Ten_khach_hang      NVARCHAR(255)  NOT NULL,
        SDT                 NVARCHAR(20)   NULL,
        Email               NVARCHAR(255)  NULL,
        Trang_thai          INT            NOT NULL DEFAULT 1,
        Ngay_tao            DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat       DATETIME2      NULL,
        Dia_chi_khach_hang  NVARCHAR(MAX)  NULL,
        Mat_khau            NVARCHAR(255)  NULL,
        Tai_khoan_id        INT            NULL,
        CONSTRAINT PK_KhachHang         PRIMARY KEY (Khach_hang_id),
        CONSTRAINT FK_KhachHang_TaiKhoan FOREIGN KEY (Tai_khoan_id) REFERENCES dbo.TaiKhoan (Tai_khoan_id)
    );
    PRINT N'Bảng Khach_hang đã được tạo.';
END
ELSE
    PRINT N'Bảng Khach_hang đã tồn tại.';
GO
