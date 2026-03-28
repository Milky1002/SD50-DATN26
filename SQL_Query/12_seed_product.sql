-- ============================================================
-- 12_seed_product.sql
-- Dữ liệu mẫu: Danh_muc_san_pham (10), Mau_sac (10),
--               Anh (20), SanPham (20)
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Danh_muc_san_pham
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Danh_muc_san_pham WHERE Ten_danh_muc = N'Vợt cầu lông')
BEGIN
    INSERT INTO dbo.Danh_muc_san_pham (Ten_danh_muc, Trang_thai, Ngay_tao) VALUES
        (N'Vợt cầu lông',      1, CAST(GETDATE() AS DATE)),
        (N'Cầu lông',          1, CAST(GETDATE() AS DATE)),
        (N'Giày cầu lông',     1, CAST(GETDATE() AS DATE)),
        (N'Túi vợt',           1, CAST(GETDATE() AS DATE)),
        (N'Lưới cầu lông',     1, CAST(GETDATE() AS DATE)),
        (N'Cước vợt',          1, CAST(GETDATE() AS DATE)),
        (N'Bảo vệ tay cầm',    1, CAST(GETDATE() AS DATE)),
        (N'Quần áo thi đấu',   1, CAST(GETDATE() AS DATE)),
        (N'Bình nước thể thao',1, CAST(GETDATE() AS DATE)),
        (N'Phụ kiện khác',     1, CAST(GETDATE() AS DATE));
    PRINT N'Seed Danh_muc_san_pham: OK';
END
ELSE
    PRINT N'Danh_muc_san_pham đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Mau_sac
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Mau_sac WHERE Ten_mau = N'Đỏ')
BEGIN
    INSERT INTO dbo.Mau_sac (Ten_mau, Ma_mau_hex, Trang_thai, Ngay_tao) VALUES
        (N'Đỏ',        '#FF0000', 1, SYSDATETIME()),
        (N'Xanh dương','#0000FF', 1, SYSDATETIME()),
        (N'Xanh lá',   '#00A651', 1, SYSDATETIME()),
        (N'Đen',       '#000000', 1, SYSDATETIME()),
        (N'Trắng',     '#FFFFFF', 1, SYSDATETIME()),
        (N'Vàng',      '#FFD700', 1, SYSDATETIME()),
        (N'Cam',       '#FF8C00', 1, SYSDATETIME()),
        (N'Tím',       '#800080', 1, SYSDATETIME()),
        (N'Xám',       '#808080', 1, SYSDATETIME()),
        (N'Hồng',      '#FF69B4', 1, SYSDATETIME());
    PRINT N'Seed Mau_sac: OK';
END
ELSE
    PRINT N'Mau_sac đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Anh (20 bản ghi - dùng placeholder URL)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Anh WHERE Ten_file_goc = 'vot-yonex-as02.jpg')
BEGIN
    INSERT INTO dbo.Anh (Anh_url, Ten_file_goc, Loai_nguon, Kich_thuoc_byte, Mime_type, Mo_ta, Thu_tu, Trang_thai, Ngay_tao) VALUES
        ('https://placehold.co/600x400?text=Vot+AS02',         'vot-yonex-as02.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh vợt Yonex AS02',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Vot+AS10',         'vot-yonex-as10.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh vợt Yonex AS10',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Vot+Li-Ning-91D',  'vot-li-ning-91d.jpg',   'URL', NULL, 'image/jpeg', N'Ảnh vợt Li-Ning 91D',        1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Vot+Victor-TK',    'vot-victor-tk.jpg',     'URL', NULL, 'image/jpeg', N'Ảnh vợt Victor TK',          1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Cau-Yonex-AS50',   'cau-yonex-as50.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh cầu Yonex AS50',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Cau-RSL-Silver',   'cau-rsl-silver.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh cầu RSL Silver',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Giay-Yonex-65Z',   'giay-yonex-65z.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh giày Yonex 65Z',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Giay-Victor-SH50', 'giay-victor-sh50.jpg',  'URL', NULL, 'image/jpeg', N'Ảnh giày Victor SH50',       1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Tui-Yonex-BAG3',   'tui-yonex-bag3.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh túi Yonex BAG3',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Tui-Victor-BR9',   'tui-victor-br9.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh túi Victor BR9',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Luoi-Yonex-ACE',   'luoi-yonex-ace.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh lưới Yonex ACE',         1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Cuoc-BG65',        'cuoc-bg65.jpg',         'URL', NULL, 'image/jpeg', N'Ảnh cước BG65',              1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Cuoc-BG80',        'cuoc-bg80.jpg',         'URL', NULL, 'image/jpeg', N'Ảnh cước BG80',              1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Grip-TW023',       'grip-tw023.jpg',        'URL', NULL, 'image/jpeg', N'Ảnh grip TW023',             1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Ao-Yonex-16500',   'ao-yonex-16500.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh áo thi đấu Yonex 16500', 1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Quan-Victor-R608', 'quan-victor-r608.jpg',  'URL', NULL, 'image/jpeg', N'Ảnh quần thi đấu Victor',    1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Binh-Yonex-750ml', 'binh-yonex-750ml.jpg',  'URL', NULL, 'image/jpeg', N'Ảnh bình nước Yonex 750ml',  1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Khau-trang-CBL',   'khau-trang-cbl.jpg',    'URL', NULL, 'image/jpeg', N'Ảnh khẩu trang thể thao',    1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Vot-Apacs-Z-Zigzag','vot-apacs-zigzag.jpg', 'URL', NULL, 'image/jpeg', N'Ảnh vợt Apacs Z-Zigzag',     1, 1, SYSDATETIME()),
        ('https://placehold.co/600x400?text=Cau-Feather-RSL',   'cau-feather-rsl.jpg',  'URL', NULL, 'image/jpeg', N'Ảnh cầu lông lông tự nhiên', 1, 1, SYSDATETIME());
    PRINT N'Seed Anh: OK';
END
ELSE
    PRINT N'Anh đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- SanPham (20 bản ghi)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.SanPham WHERE Ma_san_pham = 'SP001')
BEGIN
    INSERT INTO dbo.SanPham
        (Danh_muc_san_pham_id, Mau_sac_id, Anh_id, Ten_san_pham, Ma_san_pham, Sku,
         Gia_nhap, Gia_ban, So_luong_ton, Don_vi_tinh, Barcode, Mo_ta, Trang_thai, Ngay_tao)
    VALUES
        (1, 4, 1,  N'Vợt Yonex Astrox 2',            'SP001', 'SKU-SP001', 1200000, 1850000,  50, N'Cái', '8900001000001', N'Vợt cầu lông dòng Astrox chuẩn thi đấu',  1, SYSDATETIME()),
        (1, 2, 2,  N'Vợt Yonex Astrox 10',           'SP002', 'SKU-SP002', 1800000, 2700000,  40, N'Cái', '8900001000002', N'Vợt dòng tấn công mạnh Astrox 10',        1, SYSDATETIME()),
        (1, 1, 3,  N'Vợt Li-Ning 3D Calibar 91D',    'SP003', 'SKU-SP003', 2200000, 3200000,  30, N'Cái', '8900001000003', N'Vợt cao cấp Li-Ning dòng 3D Calibar',     1, SYSDATETIME()),
        (1, 3, 4,  N'Vợt Victor Thruster K',          'SP004', 'SKU-SP004', 1500000, 2350000,  35, N'Cái', '8900001000004', N'Vợt Victor dòng tấn công Thruster K',     1, SYSDATETIME()),
        (1, 5, 19, N'Vợt Apacs Z-Zigzag Speed',      'SP005', 'SKU-SP005', 900000,  1450000,  60, N'Cái', '8900001000005', N'Vợt Apacs dành cho người mới chơi',       1, SYSDATETIME()),
        (2, 6, 5,  N'Cầu Yonex AS-50 (hộp 12 quả)', 'SP006', 'SKU-SP006', 120000,  185000,  200, N'Hộp', '8900001000006', N'Cầu lông nhựa Yonex AS-50 tiêu chuẩn',   1, SYSDATETIME()),
        (2, 7, 6,  N'Cầu RSL Silver (hộp 12 quả)',   'SP007', 'SKU-SP007', 95000,   145000,  150, N'Hộp', '8900001000007', N'Cầu nhựa RSL Silver phổ thông',           1, SYSDATETIME()),
        (2, 5, 20, N'Cầu Lông Lông RSL (hộp 12)',    'SP008', 'SKU-SP008', 180000,  280000,   80, N'Hộp', '8900001000008', N'Cầu lông tự nhiên RSL chất lượng cao',    1, SYSDATETIME()),
        (3, 4, 7,  N'Giày Yonex Power Cushion 65Z',  'SP009', 'SKU-SP009', 2500000, 3800000,  25, N'Đôi', '8900001000009', N'Giày cầu lông cao cấp Yonex 65Z',         1, SYSDATETIME()),
        (3, 2, 8,  N'Giày Victor SH-A560',            'SP010', 'SKU-SP010', 1800000, 2700000,  30, N'Đôi', '8900001000010', N'Giày Victor SH-A560 nhẹ và bền',          1, SYSDATETIME()),
        (4, 4, 9,  N'Túi Yonex BAG-3PL',             'SP011', 'SKU-SP011', 450000,  680000,   45, N'Cái', '8900001000011', N'Túi vợt Yonex 3 ngăn chống thấm nước',    1, SYSDATETIME()),
        (4, 1, 10, N'Túi Victor BR9612',              'SP012', 'SKU-SP012', 380000,  580000,   55, N'Cái', '8900001000012', N'Túi Victor đựng 6 vợt',                   1, SYSDATETIME()),
        (5, 5, 11, N'Lưới Yonex AC268EX',            'SP013', 'SKU-SP013', 320000,  490000,   20, N'Bộ', '8900001000013', N'Lưới cầu lông tiêu chuẩn thi đấu',        1, SYSDATETIME()),
        (6, 4, 12, N'Cước BG65 (10m)',               'SP014', 'SKU-SP014', 55000,   85000,   300, N'Cuộn','8900001000014', N'Cước Yonex BG65 căng vợt',                 1, SYSDATETIME()),
        (6, 2, 13, N'Cước BG80 (10m)',               'SP015', 'SKU-SP015', 75000,   115000,  200, N'Cuộn','8900001000015', N'Cước Yonex BG80 tốc độ cao',               1, SYSDATETIME()),
        (7, 5, 14, N'Grip Yonex AC102EX (3 cuộn)',   'SP016', 'SKU-SP016', 45000,   72000,   250, N'Bộ', '8900001000016', N'Băng grip bảo vệ tay cầm vợt',             1, SYSDATETIME()),
        (8, 2, 15, N'Áo Yonex Polo 16500',           'SP017', 'SKU-SP017', 280000,  430000,   70, N'Cái', '8900001000017', N'Áo polo thi đấu Yonex chính hãng',         1, SYSDATETIME()),
        (8, 1, 16, N'Quần Victor R-6088',             'SP018', 'SKU-SP018', 220000,  340000,   65, N'Cái', '8900001000018', N'Quần short thể thao Victor chính hãng',    1, SYSDATETIME()),
        (9, 2, 17, N'Bình nước Yonex 750ml',         'SP019', 'SKU-SP019', 85000,   130000,  120, N'Cái', '8900001000019', N'Bình nước thể thao dung tích 750ml',       1, SYSDATETIME()),
        (10,4, 18, N'Khẩu trang thể thao CBL',       'SP020', 'SKU-SP020', 35000,   55000,   180, N'Cái', '8900001000020', N'Khẩu trang thể thao chống bụi tốt',        1, SYSDATETIME());
    PRINT N'Seed SanPham: OK';
END
ELSE
    PRINT N'SanPham đã có dữ liệu, bỏ qua.';
GO
