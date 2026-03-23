USE [sd50];
GO

-- =============================================
-- Thêm cột mật khẩu cho khách hàng (customer auth)
-- =============================================
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Khach_hang') AND name = 'Mat_khau')
BEGIN
    ALTER TABLE dbo.Khach_hang ADD Mat_khau NVARCHAR(255) NULL;
END
GO

-- =============================================
-- Bảng Giỏ hàng
-- =============================================
IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID('dbo.Gio_hang') AND type = 'U')
BEGIN
    CREATE TABLE dbo.Gio_hang (
        Gio_hang_id       INT IDENTITY(1,1) PRIMARY KEY,
        Khach_hang_id     INT NULL,
        Session_id        NVARCHAR(100) NULL,
        Ngay_tao          DATETIME2 NOT NULL DEFAULT GETDATE(),
        Ngay_cap_nhat     DATETIME2 NULL,
        CONSTRAINT FK_GioHang_KhachHang FOREIGN KEY (Khach_hang_id)
            REFERENCES dbo.Khach_hang(Khach_hang_id)
    );
END
GO

-- =============================================
-- Bảng Giỏ hàng chi tiết
-- =============================================
IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID('dbo.Gio_hang_chi_tiet') AND type = 'U')
BEGIN
    CREATE TABLE dbo.Gio_hang_chi_tiet (
        Gio_hang_chi_tiet_id  INT IDENTITY(1,1) PRIMARY KEY,
        Gio_hang_id           INT NOT NULL,
        San_pham_id           INT NOT NULL,
        So_luong              INT NOT NULL DEFAULT 1,
        Gia_tai_thoi_diem     DECIMAL(18,2) NULL,
        CONSTRAINT FK_GioHangCT_GioHang FOREIGN KEY (Gio_hang_id)
            REFERENCES dbo.Gio_hang(Gio_hang_id) ON DELETE CASCADE,
        CONSTRAINT FK_GioHangCT_SanPham FOREIGN KEY (San_pham_id)
            REFERENCES dbo.SanPham(San_pham_id)
    );
END
GO

PRINT N'=== Shop tables created successfully ===';
GO
