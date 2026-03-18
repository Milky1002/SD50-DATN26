-- =============================================
-- Script 08: Cập nhật Chương trình khuyến mãi
-- Sửa text tiếng Việt có dấu
-- =============================================
USE [sd50];
GO

UPDATE [dbo].[Chuong_trinh_khuyen_mai] SET
    [Ten_chuong_trinh] = N'Giảm 10% hoá đơn trên 3 triệu',
    [Mo_ta] = N'Giảm 10% cho hoá đơn có giá trị trên 3.000.000đ'
WHERE [Ma_chuong_trinh] = N'SALE10';

UPDATE [dbo].[Chuong_trinh_khuyen_mai] SET
    [Ten_chuong_trinh] = N'Giảm 100K cho đơn hàng trên 1 triệu',
    [Mo_ta] = N'Giảm 100.000đ cho mỗi đơn hàng có giá trị trên 1.000.000đ'
WHERE [Ma_chuong_trinh] = N'GIAM100K';

UPDATE [dbo].[Chuong_trinh_khuyen_mai] SET
    [Ten_chuong_trinh] = N'Giảm 50K cho sản phẩm vợt cầu lông',
    [Mo_ta] = N'Giảm 50.000đ cho các sản phẩm vợt cầu lông'
WHERE [Ma_chuong_trinh] = N'GIAM50K-SP';

UPDATE [dbo].[Chuong_trinh_khuyen_mai] SET
    [Ten_chuong_trinh] = N'Tặng quà khi mua trên 5 triệu',
    [Mo_ta] = N'Tặng 1 áo thun khi mua hàng trên 5.000.000đ'
WHERE [Ma_chuong_trinh] = N'TANG-QUA';

UPDATE [dbo].[Chuong_trinh_khuyen_mai] SET
    [Ten_chuong_trinh] = N'Đồng giá 99K cho sản phẩm sale',
    [Mo_ta] = N'Tất cả sản phẩm sale chỉ 99.000đ'
WHERE [Ma_chuong_trinh] = N'DONGGIA99K';

UPDATE [dbo].[Chuong_trinh_khuyen_mai] SET
    [Ten_chuong_trinh] = N'Chương trình đã kết thúc',
    [Mo_ta] = N'Chương trình này đã kết thúc'
WHERE [Ma_chuong_trinh] = N'OLD-PROMO';

UPDATE [dbo].[Chuong_trinh_khuyen_mai] SET
    [Ten_chuong_trinh] = N'Giảm 20% cho khách VIP',
    [Mo_ta] = N'Giảm 20% cho khách hàng VIP, tối đa 1 triệu'
WHERE [Ma_chuong_trinh] = N'VIP20';

PRINT N'✓ Đã cập nhật bảng Chuong_trinh_khuyen_mai (tiếng Việt có dấu)';
GO
