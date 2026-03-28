-- ============================================================
-- 99_patch_missing_columns.sql
-- Thêm các cột bị thiếu vào DB đã tồn tại từ phiên bản cũ
-- Chạy file này một lần sau khi nâng cấp từ schema cũ
-- An toàn khi chạy nhiều lần (idempotent)
-- ============================================================

USE sd50;
GO

PRINT N'=== Patch: kiểm tra và thêm cột còn thiếu ===';
GO

-- -------------------------------------------------------
-- Bảng Anh
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Anh') AND name = 'Kich_thuoc_byte')
BEGIN
    ALTER TABLE dbo.Anh ADD Kich_thuoc_byte BIGINT NULL;
    PRINT N'[OK] Anh.Kich_thuoc_byte đã được thêm.';
END
ELSE PRINT N'[SKIP] Anh.Kich_thuoc_byte đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Anh') AND name = 'Ten_file_goc')
BEGIN
    ALTER TABLE dbo.Anh ADD Ten_file_goc NVARCHAR(255) NULL;
    PRINT N'[OK] Anh.Ten_file_goc đã được thêm.';
END
ELSE PRINT N'[SKIP] Anh.Ten_file_goc đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Anh') AND name = 'Loai_nguon')
BEGIN
    ALTER TABLE dbo.Anh ADD Loai_nguon NVARCHAR(100) NULL;
    PRINT N'[OK] Anh.Loai_nguon đã được thêm.';
END
ELSE PRINT N'[SKIP] Anh.Loai_nguon đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Anh') AND name = 'Mime_type')
BEGIN
    ALTER TABLE dbo.Anh ADD Mime_type NVARCHAR(100) NULL;
    PRINT N'[OK] Anh.Mime_type đã được thêm.';
END
ELSE PRINT N'[SKIP] Anh.Mime_type đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Anh') AND name = 'Thu_tu')
BEGIN
    ALTER TABLE dbo.Anh ADD Thu_tu INT NOT NULL DEFAULT 0;
    PRINT N'[OK] Anh.Thu_tu đã được thêm.';
END
ELSE PRINT N'[SKIP] Anh.Thu_tu đã tồn tại.';
GO

-- -------------------------------------------------------
-- Bảng Khach_hang
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Khach_hang') AND name = 'Mat_khau')
BEGIN
    ALTER TABLE dbo.Khach_hang ADD Mat_khau NVARCHAR(255) NULL;
    PRINT N'[OK] Khach_hang.Mat_khau đã được thêm.';
END
ELSE PRINT N'[SKIP] Khach_hang.Mat_khau đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Khach_hang') AND name = 'Tai_khoan_id')
BEGIN
    ALTER TABLE dbo.Khach_hang ADD Tai_khoan_id INT NULL;
    PRINT N'[OK] Khach_hang.Tai_khoan_id đã được thêm.';
END
ELSE PRINT N'[SKIP] Khach_hang.Tai_khoan_id đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Khach_hang') AND name = 'Dia_chi_khach_hang')
BEGIN
    ALTER TABLE dbo.Khach_hang ADD Dia_chi_khach_hang NVARCHAR(MAX) NULL;
    PRINT N'[OK] Khach_hang.Dia_chi_khach_hang đã được thêm.';
END
ELSE PRINT N'[SKIP] Khach_hang.Dia_chi_khach_hang đã tồn tại.';
GO

-- -------------------------------------------------------
-- Bảng HoaDon  – các cột được thêm ở update scripts cũ
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.HoaDon') AND name = 'Loai_hoa_don')
BEGIN
    ALTER TABLE dbo.HoaDon ADD Loai_hoa_don NVARCHAR(50) NULL;
    PRINT N'[OK] HoaDon.Loai_hoa_don đã được thêm.';
END
ELSE PRINT N'[SKIP] HoaDon.Loai_hoa_don đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.HoaDon') AND name = 'Dia_chi_khach_hang')
BEGIN
    ALTER TABLE dbo.HoaDon ADD Dia_chi_khach_hang NVARCHAR(MAX) NULL;
    PRINT N'[OK] HoaDon.Dia_chi_khach_hang đã được thêm.';
END
ELSE PRINT N'[SKIP] HoaDon.Dia_chi_khach_hang đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.HoaDon') AND name = 'Thong_tin_voucher')
BEGIN
    ALTER TABLE dbo.HoaDon ADD Thong_tin_voucher NVARCHAR(MAX) NULL;
    PRINT N'[OK] HoaDon.Thong_tin_voucher đã được thêm.';
END
ELSE PRINT N'[SKIP] HoaDon.Thong_tin_voucher đã tồn tại.';
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.HoaDon') AND name = 'Dia_chi_id')
BEGIN
    ALTER TABLE dbo.HoaDon ADD Dia_chi_id INT NULL;
    PRINT N'[OK] HoaDon.Dia_chi_id đã được thêm.';
END
ELSE PRINT N'[SKIP] HoaDon.Dia_chi_id đã tồn tại.';
GO

PRINT N'=== Patch hoàn thành ===';
GO
