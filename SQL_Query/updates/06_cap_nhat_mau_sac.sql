-- =============================================
-- Script 06: Cập nhật Màu sắc
-- Sửa text tiếng Việt có dấu
-- =============================================
USE [sd50];
GO

UPDATE [dbo].[Mau_sac] SET [Ten_mau] = N'Đỏ' WHERE [Ten_mau] = N'Do';
UPDATE [dbo].[Mau_sac] SET [Ten_mau] = N'Xanh dương' WHERE [Ten_mau] = N'Xanh duong';
UPDATE [dbo].[Mau_sac] SET [Ten_mau] = N'Đen' WHERE [Ten_mau] = N'Den';
UPDATE [dbo].[Mau_sac] SET [Ten_mau] = N'Trắng' WHERE [Ten_mau] = N'Trang';
UPDATE [dbo].[Mau_sac] SET [Ten_mau] = N'Vàng' WHERE [Ten_mau] = N'Vang';

-- Thêm thêm màu sắc mẫu
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Xanh lá')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Xanh lá', '#00FF00', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Cam')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Cam', '#FF8C00', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Tím')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Tím', '#800080', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Hồng')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Hồng', '#FF69B4', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Bạc')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Bạc', '#C0C0C0', 1);

PRINT N'✓ Đã cập nhật bảng Mau_sac (tiếng Việt có dấu + thêm màu mới)';
GO
