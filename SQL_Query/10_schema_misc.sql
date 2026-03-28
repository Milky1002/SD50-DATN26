-- ============================================================
-- 10_schema_misc.sql
-- Tạo bảng: Lich_su_hoat_dong_nhan_vien,
--           Trang_chu_danh_muc_noi_bat, Trang_chu_san_pham_hot
-- Phải chạy SAU 02, 03 schema
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Lich_su_hoat_dong_nhan_vien (Staff Activity Log)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Lich_su_hoat_dong_nhan_vien' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Lich_su_hoat_dong_nhan_vien (
        Id              INT            NOT NULL IDENTITY(1,1),
        Nhan_vien_id    INT            NOT NULL,
        Ho_ten_nhan_vien NVARCHAR(255) NULL,
        -- SALE_OFFLINE | KH_TAO | KH_SUA
        Hanh_dong       NVARCHAR(50)   NOT NULL,
        -- HOA_DON | KHACH_HANG
        Doi_tuong       NVARCHAR(50)   NULL,
        Doi_tuong_id    INT            NULL,
        Mo_ta           NVARCHAR(MAX)  NULL,
        Gia_tri         DECIMAL(18,2)  NULL,
        Thoi_gian       DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT PK_LichSuHoatDong PRIMARY KEY (Id)
    );
    PRINT N'Bảng Lich_su_hoat_dong_nhan_vien đã được tạo.';
END
ELSE
    PRINT N'Bảng Lich_su_hoat_dong_nhan_vien đã tồn tại.';
GO

-- -------------------------------------------------------
-- Trang_chu_danh_muc_noi_bat (Homepage Featured Category)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Trang_chu_danh_muc_noi_bat' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Trang_chu_danh_muc_noi_bat (
        Trang_chu_danh_muc_noi_bat_id  INT        NOT NULL IDENTITY(1,1),
        Danh_muc_san_pham_id           INT        NOT NULL,
        Thu_tu                         INT        NOT NULL DEFAULT 0,
        So_luong_hien_thi              INT        NOT NULL DEFAULT 20,
        Trang_thai                     INT        NOT NULL DEFAULT 1,
        Ngay_tao                       DATETIME2  NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat                  DATETIME2  NULL,
        CONSTRAINT PK_TrangChuDanhMuc        PRIMARY KEY (Trang_chu_danh_muc_noi_bat_id),
        CONSTRAINT FK_TrangChuDM_DanhMuc     FOREIGN KEY (Danh_muc_san_pham_id) REFERENCES dbo.Danh_muc_san_pham (Danh_muc_san_pham_id)
    );
    PRINT N'Bảng Trang_chu_danh_muc_noi_bat đã được tạo.';
END
ELSE
    PRINT N'Bảng Trang_chu_danh_muc_noi_bat đã tồn tại.';
GO

-- -------------------------------------------------------
-- Trang_chu_san_pham_hot (Homepage Hot Products)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Trang_chu_san_pham_hot' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Trang_chu_san_pham_hot (
        Trang_chu_san_pham_hot_id  INT        NOT NULL IDENTITY(1,1),
        San_pham_id                INT        NOT NULL,
        Thu_tu                     INT        NOT NULL DEFAULT 0,
        Trang_thai                 INT        NOT NULL DEFAULT 1,
        Ngay_tao                   DATETIME2  NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat              DATETIME2  NULL,
        CONSTRAINT PK_TrangChuSPHot          PRIMARY KEY (Trang_chu_san_pham_hot_id),
        CONSTRAINT FK_TrangChuSPHot_SP       FOREIGN KEY (San_pham_id) REFERENCES dbo.SanPham (San_pham_id)
    );
    PRINT N'Bảng Trang_chu_san_pham_hot đã được tạo.';
END
ELSE
    PRINT N'Bảng Trang_chu_san_pham_hot đã tồn tại.';
GO
