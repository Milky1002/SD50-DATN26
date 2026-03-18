-- =============================================
-- Script 02: Cập nhật tài khoản Admin
-- Đổi mật khẩu thành admin@123
-- =============================================
USE [sd50];
GO

-- Cập nhật hoặc tạo tài khoản admin với mật khẩu admin@123
IF EXISTS (SELECT 1 FROM [dbo].[TaiKhoan] WHERE [Tai_khoan_id] = 1)
BEGIN
    UPDATE [dbo].[TaiKhoan]
    SET [User_name] = N'admin',
        [Pass_word] = N'admin@123',
        [Trang_thai] = 1
    WHERE [Tai_khoan_id] = 1;
    PRINT N'✓ Đã cập nhật tài khoản admin (admin/admin@123)';
END
ELSE
BEGIN
    SET IDENTITY_INSERT [dbo].[TaiKhoan] ON;
    INSERT INTO [dbo].[TaiKhoan] ([Tai_khoan_id], [User_name], [Pass_word], [Trang_thai])
    VALUES (1, N'admin', N'admin@123', 1);
    SET IDENTITY_INSERT [dbo].[TaiKhoan] OFF;
    PRINT N'✓ Đã tạo tài khoản admin mới (admin/admin@123)';
END;

-- Thêm tài khoản nhân viên mẫu
IF NOT EXISTS (SELECT 1 FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien01')
BEGIN
    INSERT INTO [dbo].[TaiKhoan] ([User_name], [Pass_word], [Trang_thai])
    VALUES (N'nhanvien01', N'admin@123', 1);
    PRINT N'✓ Đã tạo tài khoản nhanvien01 (nhanvien01/admin@123)';
END;

IF NOT EXISTS (SELECT 1 FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien02')
BEGIN
    INSERT INTO [dbo].[TaiKhoan] ([User_name], [Pass_word], [Trang_thai])
    VALUES (N'nhanvien02', N'admin@123', 1);
    PRINT N'✓ Đã tạo tài khoản nhanvien02 (nhanvien02/admin@123)';
END;
GO
