-- =============================================
-- Script 07: Cập nhật Sản phẩm
-- Sửa text tiếng Việt có dấu + thêm sản phẩm mẫu
-- =============================================
USE [sd50];
GO

-- Cập nhật 5 sản phẩm hiện có sang tiếng Việt có dấu
UPDATE [dbo].[SanPham] SET
    [Ten_san_pham] = N'Vợt Yonex Astrox 99 Pro',
    [Don_vi_tinh] = N'Cây',
    [Mo_ta] = N'Vợt cao cấp cho người chơi chuyên nghiệp, thiết kế tấn công mạnh mẽ'
WHERE [Ma_san_pham] = 'HH01';

UPDATE [dbo].[SanPham] SET
    [Ten_san_pham] = N'Vợt Lining Axforce 80',
    [Don_vi_tinh] = N'Cây',
    [Mo_ta] = N'Vợt công thủ toàn diện, phù hợp nhiều lối chơi'
WHERE [Ma_san_pham] = 'HH02';

UPDATE [dbo].[SanPham] SET
    [Ten_san_pham] = N'Giày Yonex Power Cushion 65Z',
    [Don_vi_tinh] = N'Đôi',
    [Mo_ta] = N'Giày cầu lông cao cấp, đệm êm, bám sân tốt'
WHERE [Ma_san_pham] = 'HH03';

UPDATE [dbo].[SanPham] SET
    [Ten_san_pham] = N'Cước cầu lông Yonex BG65',
    [Don_vi_tinh] = N'Cuộn',
    [Mo_ta] = N'Cước bền, phổ biến, phù hợp người chơi phong trào'
WHERE [Ma_san_pham] = 'HH04';

UPDATE [dbo].[SanPham] SET
    [Ten_san_pham] = N'Áo cầu lông Yonex 2026',
    [Don_vi_tinh] = N'Cái',
    [Mo_ta] = N'Áo thi đấu chính hãng, chất liệu thoáng mát'
WHERE [Ma_san_pham] = 'HH05';

PRINT N'✓ Đã cập nhật 5 sản phẩm sang tiếng Việt có dấu';
GO

-- Thêm thêm sản phẩm mẫu
DECLARE @dmVot INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] LIKE N'%ợt%');
DECLARE @dmGiay INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] LIKE N'%iày%');
DECLARE @dmPK INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] LIKE N'%ụ kiện%');
DECLARE @dmQA INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] LIKE N'%uần áo%');
DECLARE @dmBL INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] LIKE N'%a lô%');

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH06') AND @dmVot IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmVot, 3, N'Vợt Yonex Nanoflare 700', 'HH06', 'SKU-HH06', '8934567890178', 2200000, 3200000, 12, N'Cây', N'Vợt siêu nhẹ, tốc độ cao, phù hợp lối chơi phòng thủ phản công', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH07') AND @dmVot IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmVot, 2, N'Vợt Victor Thruster K 9900', 'HH07', 'SKU-HH07', '8934567890185', 2800000, 3800000, 8, N'Cây', N'Vợt tấn công hàng đầu của Victor, cân bằng tốt', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH08') AND @dmGiay IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmGiay, 1, N'Giày Yonex Aerus Z', 'HH08', 'SKU-HH08', '8934567890192', 1500000, 2300000, 25, N'Đôi', N'Giày siêu nhẹ, đệm Power Cushion+, phù hợp chơi đơn', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH09') AND @dmPK IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmPK, N'Quấn cán Yonex Super Grap (3 cuộn)', 'HH09', 'SKU-HH09', '8934567890208', 50000, 95000, 200, N'Bộ', N'Quấn cán chống trượt, mềm mại, thấm mồ hôi tốt', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH10') AND @dmQA IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmQA, 4, N'Quần cầu lông Yonex 2026', 'HH10', 'SKU-HH10', '8934567890215', 200000, 380000, 60, N'Cái', N'Quần thể thao chính hãng, co giãn tốt, thoáng khí', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH11') AND @dmBL IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmBL, 3, N'Túi vợt Yonex BA82231W (6 vợt)', 'HH11', 'SKU-HH11', '8934567890222', 800000, 1350000, 15, N'Cái', N'Túi vợt cao cấp 6 ngăn, chống sốc, chống nước', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH12') AND @dmPK IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmPK, N'Cầu lông Yonex Mavis 350 (Hộp 6 quả)', 'HH12', 'SKU-HH12', '8934567890239', 60000, 120000, 150, N'Hộp', N'Cầu lông nhựa chất lượng cao, bay ổn định, bền bỉ', 1);

PRINT N'✓ Đã thêm sản phẩm mẫu mới';
GO
