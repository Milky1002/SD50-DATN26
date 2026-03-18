-- =============================================
-- Script 05: Cập nhật Danh mục sản phẩm
-- Sửa text tiếng Việt có dấu
-- =============================================
USE [sd50];
GO

-- Cập nhật tên danh mục sang tiếng Việt có dấu
UPDATE [dbo].[Danh_muc_san_pham] SET [Ten_danh_muc] = N'Vợt cầu lông' WHERE [Ten_danh_muc] = N'Vot cau long';
UPDATE [dbo].[Danh_muc_san_pham] SET [Ten_danh_muc] = N'Giày cầu lông' WHERE [Ten_danh_muc] = N'Giay cau long';
UPDATE [dbo].[Danh_muc_san_pham] SET [Ten_danh_muc] = N'Phụ kiện' WHERE [Ten_danh_muc] = N'Phu kien';
UPDATE [dbo].[Danh_muc_san_pham] SET [Ten_danh_muc] = N'Quần áo' WHERE [Ten_danh_muc] = N'Quan ao';
UPDATE [dbo].[Danh_muc_san_pham] SET [Ten_danh_muc] = N'Ba lô - Túi vợt' WHERE [Ten_danh_muc] = N'Ba lo - Tui vot';

PRINT N'✓ Đã cập nhật bảng Danh_muc_san_pham (tiếng Việt có dấu)';
GO
