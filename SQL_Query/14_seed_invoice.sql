-- ============================================================
-- 14_seed_invoice.sql
-- Dữ liệu mẫu: HinhThucThanhToan (5), HoaDon (20),
--               HoaDonChiTiet (20), ThanhToan (20)
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- HinhThucThanhToan
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.HinhThucThanhToan WHERE Ten_hinh_thuc = N'Tiền mặt')
BEGIN
    INSERT INTO dbo.HinhThucThanhToan (Ten_hinh_thuc, Mo_ta, Ngay_tao) VALUES
        (N'Tiền mặt',          N'Thanh toán bằng tiền mặt tại quầy',                   SYSDATETIME()),
        (N'Chuyển khoản',      N'Chuyển khoản ngân hàng',                               SYSDATETIME()),
        (N'Thẻ ATM / NAPAS',   N'Quẹt thẻ ATM nội địa qua máy POS',                    SYSDATETIME()),
        (N'Thẻ Visa / Master', N'Thẻ tín dụng / ghi nợ quốc tế',                       SYSDATETIME()),
        (N'Ví MoMo',           N'Thanh toán qua ví điện tử MoMo',                       SYSDATETIME());
    PRINT N'Seed HinhThucThanhToan: OK';
END
ELSE
    PRINT N'HinhThucThanhToan đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- HoaDon (20 bản ghi - bán tại quầy + online mix)
-- NhanVien id 1 (admin) và 2 (nhanvien01) luân phiên
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.HoaDon WHERE Ten_khach_hang = N'Nguyễn Thị Anh' AND Loai_hoa_don = 'POS')
BEGIN
    DECLARE @nv1 INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id);
    DECLARE @nv2 INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id OFFSET 1 ROWS);
    DECLARE @kh1 INT  = (SELECT TOP 1 Khach_hang_id FROM dbo.Khach_hang ORDER BY Khach_hang_id);
    DECLARE @kh2 INT  = (SELECT TOP 1 Khach_hang_id FROM dbo.Khach_hang ORDER BY Khach_hang_id OFFSET 1 ROWS);
    DECLARE @kh3 INT  = (SELECT TOP 1 Khach_hang_id FROM dbo.Khach_hang ORDER BY Khach_hang_id OFFSET 2 ROWS);
    DECLARE @ht1 INT  = (SELECT TOP 1 Hinh_thuc_thanh_toan_id FROM dbo.HinhThucThanhToan ORDER BY Hinh_thuc_thanh_toan_id);
    DECLARE @ht2 INT  = (SELECT TOP 1 Hinh_thuc_thanh_toan_id FROM dbo.HinhThucThanhToan ORDER BY Hinh_thuc_thanh_toan_id OFFSET 1 ROWS);

    INSERT INTO dbo.HoaDon
        (Nhan_vien_id, Khach_hang_id, Hinh_thuc_thanh_toan_id, Ten_khach_hang, Sdt_khach_hang, Email_khach_hang,
         Ngay_tao, Ngay_nhan_hang, Tong_tien_sau_khi_giam, Trang_thai, Loai_hoa_don, Dia_chi_khach_hang, Ghi_chu)
    VALUES
        (@nv1, @kh1, @ht1, N'Nguyễn Thị Anh',   '0911111101','khachhang01@gmail.com', DATEADD(DAY,-19,SYSDATETIME()), NULL,                        1850000, 2, 'POS',    NULL,                              NULL),
        (@nv2, @kh2, @ht2, N'Trần Văn Bảo',     '0911111102','khachhang02@gmail.com', DATEADD(DAY,-18,SYSDATETIME()), NULL,                        2700000, 2, 'POS',    NULL,                              NULL),
        (@nv1, @kh3, @ht1, N'Lê Thị Châu',      '0911111103','khachhang03@gmail.com', DATEADD(DAY,-17,SYSDATETIME()), NULL,                        185000,  2, 'POS',    NULL,                              NULL),
        (@nv2, NULL, @ht1, N'Khách lẻ 1',        '0900000001', NULL,                   DATEADD(DAY,-16,SYSDATETIME()), NULL,                        145000,  2, 'POS',    NULL,                              NULL),
        (@nv1, @kh1, @ht2, N'Nguyễn Thị Anh',   '0911111101','khachhang01@gmail.com', DATEADD(DAY,-15,SYSDATETIME()), DATEADD(DAY,-12,SYSDATETIME()),3200000, 2,'ONLINE', N'101 Lê Lợi, Q.1, TP.HCM',       N'Giao nhanh'),
        (@nv2, @kh2, @ht2, N'Trần Văn Bảo',     '0911111102','khachhang02@gmail.com', DATEADD(DAY,-14,SYSDATETIME()), DATEADD(DAY,-11,SYSDATETIME()),2350000, 2,'ONLINE', N'202 Nguyễn Huệ, Q.1, TP.HCM',   NULL),
        (@nv1, @kh3, @ht1, N'Lê Thị Châu',      '0911111103','khachhang03@gmail.com', DATEADD(DAY,-13,SYSDATETIME()), NULL,                        3800000, 2, 'POS',    NULL,                              NULL),
        (@nv2, NULL, @ht1, N'Khách lẻ 2',        '0900000002', NULL,                   DATEADD(DAY,-12,SYSDATETIME()), NULL,                        680000,  2, 'POS',    NULL,                              NULL),
        (@nv1, @kh1, @ht2, N'Nguyễn Thị Anh',   '0911111101','khachhang01@gmail.com', DATEADD(DAY,-11,SYSDATETIME()), DATEADD(DAY,-8,SYSDATETIME()), 430000, 2, 'ONLINE', N'101 Lê Lợi, Q.1, TP.HCM',       NULL),
        (@nv2, @kh2, @ht1, N'Trần Văn Bảo',     '0911111102','khachhang02@gmail.com', DATEADD(DAY,-10,SYSDATETIME()), NULL,                        1450000, 2, 'POS',    NULL,                              NULL),
        (@nv1, @kh3, @ht2, N'Lê Thị Châu',      '0911111103','khachhang03@gmail.com', DATEADD(DAY,-9, SYSDATETIME()), DATEADD(DAY,-6,SYSDATETIME()), 490000, 2, 'ONLINE', N'303 Điện Biên Phủ, Bình Thạnh',  NULL),
        (@nv2, NULL, @ht1, N'Khách lẻ 3',        '0900000003', NULL,                   DATEADD(DAY,-8, SYSDATETIME()), NULL,                        85000,   2, 'POS',    NULL,                              NULL),
        (@nv1, @kh1, @ht2, N'Nguyễn Thị Anh',   '0911111101','khachhang01@gmail.com', DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,-4,SYSDATETIME()),115000,  2,'ONLINE', N'101 Lê Lợi, Q.1, TP.HCM',        NULL),
        (@nv2, @kh2, @ht1, N'Trần Văn Bảo',     '0911111102','khachhang02@gmail.com', DATEADD(DAY,-6, SYSDATETIME()), NULL,                        72000,   2, 'POS',    NULL,                              NULL),
        (@nv1, @kh3, @ht2, N'Lê Thị Châu',      '0911111103','khachhang03@gmail.com', DATEADD(DAY,-5, SYSDATETIME()), DATEADD(DAY,-2,SYSDATETIME()),280000,  2,'ONLINE', N'303 Điện Biên Phủ, Bình Thạnh',   NULL),
        (@nv2, NULL, @ht1, N'Khách lẻ 4',        '0900000004', NULL,                   DATEADD(DAY,-4, SYSDATETIME()), NULL,                        185000,  2, 'POS',    NULL,                              NULL),
        (@nv1, @kh1, @ht2, N'Nguyễn Thị Anh',   '0911111101','khachhang01@gmail.com', DATEADD(DAY,-3, SYSDATETIME()), DATEADD(DAY,-1,SYSDATETIME()),1850000, 2,'ONLINE', N'101 Lê Lợi, Q.1, TP.HCM',        N'Yêu cầu đóng gói đặc biệt'),
        (@nv2, @kh2, @ht1, N'Trần Văn Bảo',     '0911111102','khachhang02@gmail.com', DATEADD(DAY,-2, SYSDATETIME()), NULL,                        580000,  1, 'POS',    NULL,                              NULL),
        (@nv1, @kh3, @ht2, N'Lê Thị Châu',      '0911111103','khachhang03@gmail.com', DATEADD(DAY,-1, SYSDATETIME()), NULL,                        130000,  0, 'ONLINE', N'303 Điện Biên Phủ, Bình Thạnh',   NULL),
        (@nv2, NULL, @ht1, N'Khách lẻ 5',        '0900000005', NULL,                   SYSDATETIME(),                  NULL,                        55000,   0, 'POS',    NULL,                              NULL);
    PRINT N'Seed HoaDon: OK';
END
ELSE
    PRINT N'HoaDon đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- HoaDonChiTiet (1 dòng chi tiết mỗi hóa đơn)
-- SP1..SP20 luân phiên theo Hoa_don_id
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.HoaDonChiTiet)
BEGIN
    INSERT INTO dbo.HoaDonChiTiet (Hoa_don_id, San_pham_id, So_luong_san_pham, Gia)
    SELECT
        h.Hoa_don_id,
        sp.San_pham_id,
        1,
        sp.Gia_ban
    FROM (
        SELECT Hoa_don_id, ROW_NUMBER() OVER (ORDER BY Hoa_don_id) AS rn
        FROM dbo.HoaDon
    ) h
    JOIN (
        SELECT San_pham_id, ROW_NUMBER() OVER (ORDER BY San_pham_id) AS rn
        FROM dbo.SanPham
    ) sp ON sp.rn = ((h.rn - 1) % 20) + 1;
    PRINT N'Seed HoaDonChiTiet: OK';
END
ELSE
    PRINT N'HoaDonChiTiet đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- ThanhToan (1 giao dịch mỗi hóa đơn, chỉ các hóa đơn đã hoàn thành)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.ThanhToan)
BEGIN
    INSERT INTO dbo.ThanhToan (Hinh_thuc_thanh_toan_id, Hoa_don_id, So_tien, Paid_at, Ma_giao_dich, Trang_thai)
    SELECT
        ISNULL(h.Hinh_thuc_thanh_toan_id, 1),
        h.Hoa_don_id,
        h.Tong_tien_sau_khi_giam,
        h.Ngay_tao,
        'TXN' + RIGHT('000000' + CAST(h.Hoa_don_id AS VARCHAR), 6),
        1   -- Đã thanh toán
    FROM dbo.HoaDon h
    WHERE h.Trang_thai = 2;  -- Chỉ lấy hóa đơn hoàn thành
    PRINT N'Seed ThanhToan: OK';
END
ELSE
    PRINT N'ThanhToan đã có dữ liệu, bỏ qua.';
GO
