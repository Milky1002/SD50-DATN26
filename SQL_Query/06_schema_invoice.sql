-- ============================================================
-- 06_schema_invoice.sql
-- Tạo bảng: HoaDon, HoaDonChiTiet, ThanhToan
-- Phải chạy SAU 02, 03, 04, 05
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- HoaDon (Invoice)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'HoaDon' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.HoaDon (
        Hoa_don_id              INT            NOT NULL IDENTITY(1,1),
        Nhan_vien_id            INT            NOT NULL,
        Voucher_id              INT            NULL,
        Khach_hang_id           INT            NULL,
        Hinh_thuc_thanh_toan_id INT            NULL,
        Dia_chi_id              INT            NULL,
        Ten_khach_hang          NVARCHAR(255)  NOT NULL,
        Sdt_khach_hang          NVARCHAR(50)   NULL,
        Email_khach_hang        NVARCHAR(255)  NULL,
        Ngay_tao                DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_nhan_hang          DATETIME2      NULL,
        Tong_tien_sau_khi_giam  DECIMAL(18,2)  NOT NULL DEFAULT 0,
        Trang_thai              INT            NOT NULL DEFAULT 0,
        Loai_hoa_don            NVARCHAR(50)   NULL,
        Ghi_chu                 NVARCHAR(MAX)  NULL,
        Dia_chi_khach_hang      NVARCHAR(MAX)  NULL,
        Thong_tin_voucher       NVARCHAR(MAX)  NULL,
        CONSTRAINT PK_HoaDon             PRIMARY KEY (Hoa_don_id),
        CONSTRAINT FK_HoaDon_NhanVien    FOREIGN KEY (Nhan_vien_id)            REFERENCES dbo.NhanVien (Nhan_vien_id),
        CONSTRAINT FK_HoaDon_KhachHang   FOREIGN KEY (Khach_hang_id)           REFERENCES dbo.Khach_hang (Khach_hang_id),
        CONSTRAINT FK_HoaDon_HTTT        FOREIGN KEY (Hinh_thuc_thanh_toan_id) REFERENCES dbo.HinhThucThanhToan (Hinh_thuc_thanh_toan_id)
    );
    PRINT N'Bảng HoaDon đã được tạo.';
END
ELSE
    PRINT N'Bảng HoaDon đã tồn tại.';
GO

-- -------------------------------------------------------
-- HoaDonChiTiet (Invoice Detail)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'HoaDonChiTiet' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.HoaDonChiTiet (
        Hoa_don_chi_tiet_id  INT            NOT NULL IDENTITY(1,1),
        Hoa_don_id           INT            NOT NULL,
        San_pham_id          INT            NOT NULL,
        So_luong_san_pham    INT            NOT NULL,
        Gia                  DECIMAL(18,2)  NOT NULL,
        CONSTRAINT PK_HoaDonChiTiet       PRIMARY KEY (Hoa_don_chi_tiet_id),
        CONSTRAINT FK_HDCT_HoaDon         FOREIGN KEY (Hoa_don_id)  REFERENCES dbo.HoaDon  (Hoa_don_id),
        CONSTRAINT FK_HDCT_SanPham        FOREIGN KEY (San_pham_id) REFERENCES dbo.SanPham (San_pham_id)
    );
    PRINT N'Bảng HoaDonChiTiet đã được tạo.';
END
ELSE
    PRINT N'Bảng HoaDonChiTiet đã tồn tại.';
GO

-- -------------------------------------------------------
-- ThanhToan (Payment Transaction)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'ThanhToan' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.ThanhToan (
        Thanh_toan_id           INT            NOT NULL IDENTITY(1,1),
        Hinh_thuc_thanh_toan_id INT            NOT NULL,
        Hoa_don_id              INT            NOT NULL,
        So_tien                 DECIMAL(18,2)  NOT NULL,
        Paid_at                 DATETIME2      NULL,
        Ma_giao_dich            NVARCHAR(255)  NULL,
        Trang_thai              INT            NOT NULL DEFAULT 0,
        CONSTRAINT PK_ThanhToan         PRIMARY KEY (Thanh_toan_id),
        CONSTRAINT FK_TT_HinhThuc       FOREIGN KEY (Hinh_thuc_thanh_toan_id) REFERENCES dbo.HinhThucThanhToan (Hinh_thuc_thanh_toan_id),
        CONSTRAINT FK_TT_HoaDon         FOREIGN KEY (Hoa_don_id)              REFERENCES dbo.HoaDon (Hoa_don_id)
    );
    PRINT N'Bảng ThanhToan đã được tạo.';
END
ELSE
    PRINT N'Bảng ThanhToan đã tồn tại.';
GO
