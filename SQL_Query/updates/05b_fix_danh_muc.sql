-- =============================================
-- Script 05b: Sửa danh mục sản phẩm
-- Database hiện có 1 danh mục "Hàng Hóa 001"
-- Cần update + thêm mới
-- =============================================
USE [sd50];
GO

-- Cập nhật danh mục hiện có
UPDATE [dbo].[Danh_muc_san_pham]
SET [Ten_danh_muc] = N'Vợt cầu lông'
WHERE [Danh_muc_san_pham_id] = 1;

-- Thêm danh mục mới (tránh trùng tên bằng IF NOT EXISTS)
IF NOT EXISTS (SELECT 1 FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Giày cầu lông')
    INSERT INTO [dbo].[Danh_muc_san_pham] ([Ten_danh_muc], [Trang_thai], [Ngay_tao])
    VALUES (N'Giày cầu lông', 1, CAST(GETDATE() AS DATE));

IF NOT EXISTS (SELECT 1 FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Phụ kiện')
    INSERT INTO [dbo].[Danh_muc_san_pham] ([Ten_danh_muc], [Trang_thai], [Ngay_tao])
    VALUES (N'Phụ kiện', 1, CAST(GETDATE() AS DATE));

IF NOT EXISTS (SELECT 1 FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Quần áo')
    INSERT INTO [dbo].[Danh_muc_san_pham] ([Ten_danh_muc], [Trang_thai], [Ngay_tao])
    VALUES (N'Quần áo', 1, CAST(GETDATE() AS DATE));

IF NOT EXISTS (SELECT 1 FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Ba lô - Túi vợt')
    INSERT INTO [dbo].[Danh_muc_san_pham] ([Ten_danh_muc], [Trang_thai], [Ngay_tao])
    VALUES (N'Ba lô - Túi vợt', 1, CAST(GETDATE() AS DATE));

PRINT N'✓ Đã cập nhật danh mục sản phẩm';
GO
