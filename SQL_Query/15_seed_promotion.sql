-- ============================================================
-- 15_seed_promotion.sql
-- Dữ liệu mẫu: Chuong_trinh_khuyen_mai (10),
--               Chuong_trinh_khuyen_mai_chi_tiet (10),
--               Lich_su_ap_dung_khuyen_mai (10)
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Chuong_trinh_khuyen_mai (10 chương trình)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Chuong_trinh_khuyen_mai WHERE Ma_chuong_trinh = 'CTKM001')
BEGIN
    INSERT INTO dbo.Chuong_trinh_khuyen_mai
        (Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta,
         Loai_khuyen_mai, Loai_giam, Gia_tri_giam, Giam_toi_da, Don_hang_toi_thieu,
         Ngay_bat_dau, Ngay_ket_thuc,
         Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung,
         Khach_hang_ap_dung, Trang_thai, Ngay_tao)
    VALUES
        ('CTKM001', N'Giảm 10% hóa đơn',        N'Giảm 10% cho mọi hóa đơn từ 500,000đ',          1, 1, 10,    50000,   500000,   DATEADD(DAY,-30,SYSDATETIME()), DATEADD(DAY, 30,SYSDATETIME()), 0, 1, 1, 1, SYSDATETIME()),
        ('CTKM002', N'Giảm 50,000đ hóa đơn',    N'Giảm trực tiếp 50,000đ cho HĐ từ 300,000đ',     1, 2, 50000, NULL,    300000,   DATEADD(DAY,-20,SYSDATETIME()), DATEADD(DAY, 20,SYSDATETIME()), 0, 0, 1, 1, SYSDATETIME()),
        ('CTKM003', N'Giảm 15% vợt cầu lông',   N'Giảm 15% cho danh mục vợt cầu lông',            2, 1, 15,    200000,  NULL,     DATEADD(DAY,-15,SYSDATETIME()), DATEADD(DAY, 15,SYSDATETIME()), 1, 0, 1, 1, SYSDATETIME()),
        ('CTKM004', N'Giảm 20,000đ cầu lông',   N'Giảm 20,000đ cho mỗi hộp cầu lông',             2, 2, 20000, NULL,    NULL,     DATEADD(DAY,-10,SYSDATETIME()), DATEADD(DAY, 20,SYSDATETIME()), 0, 0, 1, 1, SYSDATETIME()),
        ('CTKM005', N'Flash Sale cuối tuần',     N'Giảm 25% tất cả sản phẩm cuối tuần',            1, 1, 25,    300000,  200000,   DATEADD(DAY,-5, SYSDATETIME()), DATEADD(DAY, 5, SYSDATETIME()), 0, 1, 1, 1, SYSDATETIME()),
        ('CTKM006', N'Khuyến mãi giày thi đấu', N'Giảm 5% giày cầu lông cao cấp',                 2, 1, 5,     100000,  NULL,     DATEADD(DAY,-25,SYSDATETIME()), DATEADD(DAY, 5, SYSDATETIME()), 1, 0, 1, 1, SYSDATETIME()),
        ('CTKM007', N'Tri ân khách hàng thân',  N'Giảm 100,000đ cho KH có tổng HĐ > 5,000,000đ', 1, 2, 100000,NULL,    5000000,  DATEADD(DAY,-60,SYSDATETIME()), DATEADD(DAY,-1, SYSDATETIME()), 0, 0, 1, 3, SYSDATETIME()),
        ('CTKM008', N'Khai trương mùa hè',      N'Giảm 30% tất cả đơn hàng',                      1, 1, 30,    500000,  NULL,     DATEADD(DAY,-90,SYSDATETIME()), DATEADD(DAY,-60,SYSDATETIME()), 0, 1, 1, 3, SYSDATETIME()),
        ('CTKM009', N'Sắp diễn ra - KM sinh nhật',N'Giảm 20% nhân dịp sinh nhật cửa hàng',        1, 1, 20,    200000,  1000000,  DATEADD(DAY, 5, SYSDATETIME()), DATEADD(DAY, 15,SYSDATETIME()), 0, 0, 1, 2, SYSDATETIME()),
        ('CTKM010', N'Mua 2 tặng 1 cầu lông',   N'Mua 2 hộp tặng 1 hộp cầu lông nhựa',           3, 2, 145000,NULL,    NULL,     DATEADD(DAY,-3, SYSDATETIME()), DATEADD(DAY, 27,SYSDATETIME()), 0, 0, 1, 1, SYSDATETIME());
    PRINT N'Seed Chuong_trinh_khuyen_mai: OK';
END
ELSE
    PRINT N'Chuong_trinh_khuyen_mai đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Chuong_trinh_khuyen_mai_chi_tiet
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Chuong_trinh_khuyen_mai_chi_tiet)
BEGIN
    INSERT INTO dbo.Chuong_trinh_khuyen_mai_chi_tiet
        (Chuong_trinh_khuyen_mai_id, San_pham_id, Danh_muc_san_pham_id,
         So_luong_toi_thieu, So_luong_toi_da, Gia_tri_giam, Trang_thai)
    SELECT
        c.Chuong_trinh_khuyen_mai_id,
        sp.San_pham_id,
        NULL,
        1, 10,
        ROUND(sp.Gia_ban * 0.1, 0),
        1
    FROM (
        SELECT Chuong_trinh_khuyen_mai_id, ROW_NUMBER() OVER (ORDER BY Chuong_trinh_khuyen_mai_id) AS rn
        FROM dbo.Chuong_trinh_khuyen_mai
    ) c
    JOIN (
        SELECT San_pham_id, ROW_NUMBER() OVER (ORDER BY San_pham_id) AS rn
        FROM dbo.SanPham
    ) sp ON sp.rn = c.rn;
    PRINT N'Seed Chuong_trinh_khuyen_mai_chi_tiet: OK';
END
ELSE
    PRINT N'Chuong_trinh_khuyen_mai_chi_tiet đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Lich_su_ap_dung_khuyen_mai
-- Gắn với 10 hóa đơn đầu tiên đã hoàn thành
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Lich_su_ap_dung_khuyen_mai)
BEGIN
    INSERT INTO dbo.Lich_su_ap_dung_khuyen_mai
        (Chuong_trinh_khuyen_mai_id, Hoa_don_id, Gia_tri_giam, Ngay_ap_dung)
    SELECT
        c.Chuong_trinh_khuyen_mai_id,
        h.Hoa_don_id,
        ROUND(h.Tong_tien_sau_khi_giam * 0.1, 0),
        h.Ngay_tao
    FROM (
        SELECT Hoa_don_id, Tong_tien_sau_khi_giam, Ngay_tao,
               ROW_NUMBER() OVER (ORDER BY Hoa_don_id) AS rn
        FROM dbo.HoaDon
        WHERE Trang_thai = 2
    ) h
    JOIN (
        SELECT Chuong_trinh_khuyen_mai_id,
               ROW_NUMBER() OVER (ORDER BY Chuong_trinh_khuyen_mai_id) AS rn
        FROM dbo.Chuong_trinh_khuyen_mai
        WHERE Trang_thai IN (1, 3)
    ) c ON c.rn = h.rn
    WHERE h.rn <= 10;
    PRINT N'Seed Lich_su_ap_dung_khuyen_mai: OK';
END
ELSE
    PRINT N'Lich_su_ap_dung_khuyen_mai đã có dữ liệu, bỏ qua.';
GO
