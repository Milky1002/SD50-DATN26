USE [sd50];
GO

PRINT N'[13] Chuẩn hóa liên kết Khach_hang ↔ TaiKhoan...';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Khach_hang') AND name = 'Tai_khoan_id')
BEGIN
    ALTER TABLE dbo.Khach_hang ADD Tai_khoan_id INT NULL;
    PRINT N'  + Thêm cột Khach_hang.Tai_khoan_id';
END
GO

-- Đồng bộ account theo email trước
UPDATE kh
SET kh.Tai_khoan_id = tk.Tai_khoan_id,
    kh.Ngay_cap_nhat = GETDATE()
FROM dbo.Khach_hang kh
INNER JOIN dbo.TaiKhoan tk ON tk.Email = kh.Email
WHERE kh.Tai_khoan_id IS NULL
  AND kh.Email IS NOT NULL;
GO

-- Đồng bộ account theo số điện thoại nếu email chưa khớp
UPDATE kh
SET kh.Tai_khoan_id = tk.Tai_khoan_id,
    kh.Ngay_cap_nhat = GETDATE()
FROM dbo.Khach_hang kh
INNER JOIN dbo.TaiKhoan tk ON tk.So_dien_thoai = kh.SDT
WHERE kh.Tai_khoan_id IS NULL
  AND kh.SDT IS NOT NULL;
GO

-- Backfill dữ liệu tài khoản từ hồ sơ khách hàng khi account còn thiếu
UPDATE tk
SET tk.Ho_ten = ISNULL(tk.Ho_ten, kh.Ten_khach_hang),
    tk.So_dien_thoai = ISNULL(tk.So_dien_thoai, kh.SDT),
    tk.Email = ISNULL(tk.Email, kh.Email),
    tk.Ngay_cap_nhat = GETDATE()
FROM dbo.TaiKhoan tk
INNER JOIN dbo.Khach_hang kh ON kh.Tai_khoan_id = tk.Tai_khoan_id;
GO

-- Cảnh báo các hồ sơ mâu thuẫn một account gắn nhiều khách hàng
IF EXISTS (
    SELECT Tai_khoan_id
    FROM dbo.Khach_hang
    WHERE Tai_khoan_id IS NOT NULL
    GROUP BY Tai_khoan_id
    HAVING COUNT(*) > 1
)
BEGIN
    PRINT N'  ! Cảnh báo: tồn tại TaiKhoan đang liên kết với nhiều Khach_hang. Cần rà soát dữ liệu thủ công.';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_KhachHang_TaiKhoan_Unified')
BEGIN
    ALTER TABLE dbo.Khach_hang WITH NOCHECK
    ADD CONSTRAINT FK_KhachHang_TaiKhoan_Unified FOREIGN KEY (Tai_khoan_id)
    REFERENCES dbo.TaiKhoan(Tai_khoan_id);
    PRINT N'  + Tạo FK_KhachHang_TaiKhoan_Unified';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID('dbo.Khach_hang') AND name = 'IX_KhachHang_TaiKhoanId')
BEGIN
    CREATE INDEX IX_KhachHang_TaiKhoanId ON dbo.Khach_hang(Tai_khoan_id) WHERE Tai_khoan_id IS NOT NULL;
    PRINT N'  + Tạo index IX_KhachHang_TaiKhoanId';
END
GO

PRINT N'[13] ✓ Hoàn tất chuẩn hóa liên kết khách hàng - tài khoản.';
GO
