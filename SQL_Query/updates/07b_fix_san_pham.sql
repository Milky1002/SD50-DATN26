-- =============================================
-- Script 07b: Cập nhật sản phẩm hiện có + thêm mới
-- Giữ nguyên SP hiện có (có FK từ HoaDonChiTiet)
-- =============================================
USE [sd50];
GO

-- Lấy ID danh mục
DECLARE @dmVot INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Vợt cầu lông');
DECLARE @dmGiay INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Giày cầu lông');
DECLARE @dmPK INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Phụ kiện');
DECLARE @dmQA INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Quần áo');
DECLARE @dmBL INT = (SELECT TOP 1 [Danh_muc_san_pham_id] FROM [dbo].[Danh_muc_san_pham] WHERE [Ten_danh_muc] = N'Ba lô - Túi vợt');

-- Lấy ID màu sắc
DECLARE @msDo INT = (SELECT TOP 1 [Mau_sac_id] FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Đỏ');
DECLARE @msXD INT = (SELECT TOP 1 [Mau_sac_id] FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Xanh dương');
DECLARE @msDen INT = (SELECT TOP 1 [Mau_sac_id] FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Đen');
DECLARE @msTrang INT = (SELECT TOP 1 [Mau_sac_id] FROM [dbo].[Mau_sac] WHERE [Ten_mau] = N'Trắng');

-- Cập nhật 2 sản phẩm hiện có (giữ ID, chỉ sửa tên/mô tả)
UPDATE [dbo].[SanPham] SET
    [Ten_san_pham] = N'Vợt Yonex Astrox 99 Pro',
    [Danh_muc_san_pham_id] = COALESCE(@dmVot, [Danh_muc_san_pham_id]),
    [Mau_sac_id] = @msDo,
    [Don_vi_tinh] = N'Cây',
    [Gia_nhap] = 2500000, [Gia_ban] = 3500000,
    [Mo_ta] = N'Vợt cao cấp cho người chơi chuyên nghiệp, thiết kế tấn công mạnh mẽ'
WHERE [San_pham_id] = 1;

UPDATE [dbo].[SanPham] SET
    [Ten_san_pham] = N'Vợt Lining Axforce 80',
    [Danh_muc_san_pham_id] = COALESCE(@dmVot, [Danh_muc_san_pham_id]),
    [Mau_sac_id] = @msDen,
    [Don_vi_tinh] = N'Cây',
    [Gia_nhap] = 1800000, [Gia_ban] = 2800000,
    [Mo_ta] = N'Vợt công thủ toàn diện, phù hợp nhiều lối chơi'
WHERE [San_pham_id] = 2;

PRINT N'✓ Đã cập nhật 2 sản phẩm hiện có';

-- Thêm sản phẩm mới (kiểm tra Ma_san_pham để tránh trùng)
IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH03') AND @dmGiay IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmGiay, @msTrang, N'Giày Yonex Power Cushion 65Z', 'HH03', 'SKU-HH03', '8934567890147', 1200000, 1900000, 30, N'Đôi', N'Giày cầu lông cao cấp, đệm êm, bám sân tốt', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH04') AND @dmPK IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmPK, N'Cước cầu lông Yonex BG65', 'HH04', 'SKU-HH04', '8934567890154', 80000, 150000, 100, N'Cuộn', N'Cước bền, phổ biến, phù hợp người chơi phong trào', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH05') AND @dmQA IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmQA, @msXD, N'Áo cầu lông Yonex 2026', 'HH05', 'SKU-HH05', '8934567890161', 250000, 450000, 50, N'Cái', N'Áo thi đấu chính hãng, chất liệu thoáng mát', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH06') AND @dmVot IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmVot, @msDen, N'Vợt Yonex Nanoflare 700', 'HH06', 'SKU-HH06', '8934567890178', 2200000, 3200000, 12, N'Cây', N'Vợt siêu nhẹ, tốc độ cao, phù hợp lối chơi phòng thủ phản công', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH07') AND @dmVot IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmVot, @msXD, N'Vợt Victor Thruster K 9900', 'HH07', 'SKU-HH07', '8934567890185', 2800000, 3800000, 8, N'Cây', N'Vợt tấn công hàng đầu của Victor, cân bằng tốt', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH08') AND @dmGiay IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmGiay, @msDo, N'Giày Yonex Aerus Z', 'HH08', 'SKU-HH08', '8934567890192', 1500000, 2300000, 25, N'Đôi', N'Giày siêu nhẹ, đệm Power Cushion+, phù hợp chơi đơn', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH09') AND @dmPK IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmPK, N'Quấn cán Yonex Super Grap (3 cuộn)', 'HH09', 'SKU-HH09', '8934567890208', 50000, 95000, 200, N'Bộ', N'Quấn cán chống trượt, mềm mại, thấm mồ hôi tốt', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH10') AND @dmQA IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmQA, @msTrang, N'Quần cầu lông Yonex 2026', 'HH10', 'SKU-HH10', '8934567890215', 200000, 380000, 60, N'Cái', N'Quần thể thao chính hãng, co giãn tốt, thoáng khí', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH11') AND @dmBL IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Mau_sac_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmBL, @msDen, N'Túi vợt Yonex BA82231W (6 vợt)', 'HH11', 'SKU-HH11', '8934567890222', 800000, 1350000, 15, N'Cái', N'Túi vợt cao cấp 6 ngăn, chống sốc, chống nước', 1);

IF NOT EXISTS (SELECT 1 FROM [dbo].[SanPham] WHERE [Ma_san_pham] = 'HH12') AND @dmPK IS NOT NULL
    INSERT INTO [dbo].[SanPham] ([Danh_muc_san_pham_id], [Ten_san_pham], [Ma_san_pham], [Sku], [Barcode], [Gia_nhap], [Gia_ban], [So_luong_ton], [Don_vi_tinh], [Mo_ta], [Trang_thai])
    VALUES (@dmPK, N'Cầu lông Yonex Mavis 350 (Hộp 6 quả)', 'HH12', 'SKU-HH12', '8934567890239', 60000, 120000, 150, N'Hộp', N'Cầu lông nhựa chất lượng cao, bay ổn định, bền bỉ', 1);

PRINT N'✓ Đã thêm sản phẩm mẫu mới';
GO
