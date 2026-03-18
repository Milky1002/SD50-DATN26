-- =============================================
-- Script 01: Cập nhật bảng ChucVu (Chức vụ)
-- Sửa text tiếng Việt có dấu
-- =============================================
USE [sd50];
GO

-- Cập nhật chức vụ Quản lý
UPDATE [dbo].[ChucVu]
SET [Ten_chuc_vu] = N'Quản lý',
    [Mo_ta_chuc_vu] = N'Nhân viên quản lý hệ thống'
WHERE [Chuc_vu_id] = 1;

-- Thêm chức vụ Nhân viên nếu chưa có
IF NOT EXISTS (SELECT 1 FROM [dbo].[ChucVu] WHERE [Chuc_vu_id] = 2)
BEGIN
    SET IDENTITY_INSERT [dbo].[ChucVu] ON;
    INSERT INTO [dbo].[ChucVu] ([Chuc_vu_id], [Ten_chuc_vu], [Mo_ta_chuc_vu])
    VALUES (2, N'Nhân viên', N'Nhân viên bán hàng');
    SET IDENTITY_INSERT [dbo].[ChucVu] OFF;
END
ELSE
BEGIN
    UPDATE [dbo].[ChucVu]
    SET [Ten_chuc_vu] = N'Nhân viên',
        [Mo_ta_chuc_vu] = N'Nhân viên bán hàng'
    WHERE [Chuc_vu_id] = 2;
END;

PRINT N'✓ Đã cập nhật bảng ChucVu thành công';
GO
