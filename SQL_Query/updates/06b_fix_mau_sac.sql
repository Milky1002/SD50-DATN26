-- =============================================
-- Script 06b: Thêm màu sắc (bảng trống)
-- =============================================
USE [sd50];
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Đỏ')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Đỏ', '#FF0000', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Xanh dương')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Xanh dương', '#0000FF', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Đen')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Đen', '#000000', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Trắng')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Trắng', '#FFFFFF', 1);
IF NOT EXISTS (SELECT 1 FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Vàng')
    INSERT INTO [dbo].[Mau_sac] ([Ten_mau], [Ma_mau_hex], [Trang_thai]) VALUES (N'Vàng', '#FFD700', 1);
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

PRINT N'✓ Đã thêm 10 màu sắc';
GO
