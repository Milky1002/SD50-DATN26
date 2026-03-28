-- ============================================================
-- 07_schema_promotion.sql
-- Tạo bảng: Chuong_trinh_khuyen_mai, chi_tiet, lich_su
-- Phải chạy SAU 03_schema_product.sql, 06_schema_invoice.sql
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Chuong_trinh_khuyen_mai (Promotion Program)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Chuong_trinh_khuyen_mai' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Chuong_trinh_khuyen_mai (
        Chuong_trinh_khuyen_mai_id   INT            NOT NULL IDENTITY(1,1),
        Ma_chuong_trinh              NVARCHAR(50)   NOT NULL,
        Ten_chuong_trinh             NVARCHAR(255)  NOT NULL,
        Mo_ta                        NVARCHAR(MAX)  NULL,
        -- 1=Giảm hóa đơn, 2=Giảm sản phẩm, 3=Tặng hàng, 4=Đồng giá
        Loai_khuyen_mai              INT            NOT NULL,
        -- 1=Phần trăm, 2=Tiền mặt
        Loai_giam                    INT            NOT NULL,
        Gia_tri_giam                 DECIMAL(18,2)  NOT NULL,
        Giam_toi_da                  DECIMAL(18,2)  NULL,
        Don_hang_toi_thieu           DECIMAL(18,2)  NULL,
        Ngay_bat_dau                 DATETIME2      NOT NULL,
        Ngay_ket_thuc                DATETIME2      NOT NULL,
        Gio_bat_dau                  TIME           NULL,
        Gio_ket_thuc                 TIME           NULL,
        Ap_dung_cung_nhieu_ctkm      BIT            NOT NULL DEFAULT 0,
        Tu_dong_ap_dung              BIT            NOT NULL DEFAULT 0,
        Tong_lien_hoa_don_ap_dung    NVARCHAR(MAX)  NULL,
        Ngay_trong_tuan              NVARCHAR(50)   NULL,
        Ngay_trong_thang             NVARCHAR(MAX)  NULL,
        -- 1=Tất cả KH, 2=Theo nhóm, 3=Cụ thể
        Khach_hang_ap_dung          INT            NULL,
        Kenh_ban_ap_dung             NVARCHAR(MAX)  NULL,
        -- 0=Dừng, 1=Hoạt động, 2=Sắp diễn ra, 3=Đã kết thúc
        Trang_thai                   INT            NOT NULL DEFAULT 1,
        Ngay_tao                     DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        Ngay_cap_nhat                DATETIME2      NULL,
        CONSTRAINT PK_ChuongTrinhKhuyenMai      PRIMARY KEY (Chuong_trinh_khuyen_mai_id),
        CONSTRAINT UQ_ChuongTrinh_Ma            UNIQUE (Ma_chuong_trinh)
    );
    PRINT N'Bảng Chuong_trinh_khuyen_mai đã được tạo.';
END
ELSE
    PRINT N'Bảng Chuong_trinh_khuyen_mai đã tồn tại.';
GO

-- -------------------------------------------------------
-- Chuong_trinh_khuyen_mai_chi_tiet (Promotion Detail)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Chuong_trinh_khuyen_mai_chi_tiet' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Chuong_trinh_khuyen_mai_chi_tiet (
        Chuong_trinh_khuyen_mai_chi_tiet_id  INT            NOT NULL IDENTITY(1,1),
        Chuong_trinh_khuyen_mai_id           INT            NOT NULL,
        San_pham_id                          INT            NULL,
        Danh_muc_san_pham_id                 INT            NULL,
        So_luong_toi_thieu                   INT            NULL,
        So_luong_toi_da                      INT            NULL,
        Gia_tri_giam                         DECIMAL(18,2)  NULL,
        Trang_thai                           INT            NOT NULL DEFAULT 1,
        CONSTRAINT PK_CTKMChiTiet        PRIMARY KEY (Chuong_trinh_khuyen_mai_chi_tiet_id),
        CONSTRAINT FK_CTKMChiTiet_CTKM   FOREIGN KEY (Chuong_trinh_khuyen_mai_id) REFERENCES dbo.Chuong_trinh_khuyen_mai (Chuong_trinh_khuyen_mai_id),
        CONSTRAINT FK_CTKMChiTiet_SP     FOREIGN KEY (San_pham_id)                REFERENCES dbo.SanPham (San_pham_id),
        CONSTRAINT FK_CTKMChiTiet_DM     FOREIGN KEY (Danh_muc_san_pham_id)       REFERENCES dbo.Danh_muc_san_pham (Danh_muc_san_pham_id)
    );
    PRINT N'Bảng Chuong_trinh_khuyen_mai_chi_tiet đã được tạo.';
END
ELSE
    PRINT N'Bảng Chuong_trinh_khuyen_mai_chi_tiet đã tồn tại.';
GO

-- -------------------------------------------------------
-- Lich_su_ap_dung_khuyen_mai (Promotion Apply History)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Lich_su_ap_dung_khuyen_mai' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Lich_su_ap_dung_khuyen_mai (
        Lich_su_id                   INT            NOT NULL IDENTITY(1,1),
        Chuong_trinh_khuyen_mai_id   INT            NOT NULL,
        Hoa_don_id                   INT            NOT NULL,
        Gia_tri_giam                 DECIMAL(18,2)  NOT NULL,
        Ngay_ap_dung                 DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT PK_LichSuApDungKM       PRIMARY KEY (Lich_su_id),
        CONSTRAINT FK_LichSuKM_CTKM        FOREIGN KEY (Chuong_trinh_khuyen_mai_id) REFERENCES dbo.Chuong_trinh_khuyen_mai (Chuong_trinh_khuyen_mai_id),
        CONSTRAINT FK_LichSuKM_HoaDon      FOREIGN KEY (Hoa_don_id)                 REFERENCES dbo.HoaDon (Hoa_don_id)
    );
    PRINT N'Bảng Lich_su_ap_dung_khuyen_mai đã được tạo.';
END
ELSE
    PRINT N'Bảng Lich_su_ap_dung_khuyen_mai đã tồn tại.';
GO
