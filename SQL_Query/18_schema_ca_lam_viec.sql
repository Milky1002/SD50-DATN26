-- ============================================================
-- 18_schema_ca_lam_viec.sql
-- Tạo bảng: Ca_lam_viec (Staff Shift / Attendance)
-- Phải chạy SAU 02_schema_core.sql (NhanVien)
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Ca_lam_viec (Staff Work Shift)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Ca_lam_viec' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Ca_lam_viec (
        Ca_lam_viec_id      INT            NOT NULL IDENTITY(1,1),
        Nhan_vien_id        INT            NOT NULL,
        Ho_ten_nhan_vien    NVARCHAR(255)  NULL,
        Thoi_gian_bat_dau   DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Thoi_gian_ket_thuc  DATETIME2      NULL,
        Trang_thai          INT            NOT NULL DEFAULT 1,   -- 1=đang làm, 2=đã kết thúc
        Tong_hoa_don        INT            NOT NULL DEFAULT 0,
        Tong_san_pham       INT            NOT NULL DEFAULT 0,
        Tong_tien           DECIMAL(18,2)  NOT NULL DEFAULT 0,
        Ghi_chu             NVARCHAR(MAX)  NULL,
        Ngay_tao            DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat       DATETIME2      NULL,
        CONSTRAINT PK_CaLamViec        PRIMARY KEY (Ca_lam_viec_id),
        CONSTRAINT FK_CaLamViec_NV     FOREIGN KEY (Nhan_vien_id) REFERENCES dbo.NhanVien (Nhan_vien_id)
    );
    PRINT N'Bảng Ca_lam_viec đã được tạo.';
END
ELSE
    PRINT N'Bảng Ca_lam_viec đã tồn tại.';
GO
