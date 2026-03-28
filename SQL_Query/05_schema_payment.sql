-- ============================================================
-- 05_schema_payment.sql
-- Tạo bảng: HinhThucThanhToan
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- HinhThucThanhToan (Payment Method)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'HinhThucThanhToan' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.HinhThucThanhToan (
        Hinh_thuc_thanh_toan_id  INT            NOT NULL IDENTITY(1,1),
        Ten_hinh_thuc            NVARCHAR(255)  NOT NULL,
        Mo_ta                    NVARCHAR(MAX)  NULL,
        Ngay_tao                 DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat            DATETIME2      NULL,
        CONSTRAINT PK_HinhThucThanhToan PRIMARY KEY (Hinh_thuc_thanh_toan_id)
    );
    PRINT N'Bảng HinhThucThanhToan đã được tạo.';
END
ELSE
    PRINT N'Bảng HinhThucThanhToan đã tồn tại.';
GO
