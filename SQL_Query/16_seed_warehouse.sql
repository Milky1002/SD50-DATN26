-- ============================================================
-- 16_seed_warehouse.sql
-- Dữ liệu mẫu: NhaCungCap (10), PhieuNhap (10),
--               PhieuNhapChiTiet (20), PhieuXuat (5),
--               PhieuXuatChiTiet (10)
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- NhaCungCap
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.NhaCungCap WHERE Ten_nha_cung_cap = N'Công ty TNHH Yonex Việt Nam')
BEGIN
    INSERT INTO dbo.NhaCungCap (Ten_nha_cung_cap, Nguoi_lien_he, SDT, Email, Dia_chi, Trang_thai, Ngay_tao)
    VALUES
        (N'Công ty TNHH Yonex Việt Nam',    N'Nguyễn Văn Hùng',   '0283000001', 'yonex@vn.yonex.com',    N'12 Nguyễn Thị Minh Khai, Q.1, TP.HCM',  1, SYSDATETIME()),
        (N'Victor Việt Nam Co., Ltd',        N'Trần Thị Lan',       '0283000002', 'victor@victorsport.vn', N'45 Phạm Hùng, Q.8, TP.HCM',             1, SYSDATETIME()),
        (N'Li-Ning Sports Việt Nam',         N'Lê Văn Phú',         '0283000003', 'lining@lining.vn',     N'78 Đinh Tiên Hoàng, Q.1, TP.HCM',       1, SYSDATETIME()),
        (N'RSL Sport Co., Ltd',              N'Phạm Thị Hoa',       '0283000004', 'rsl@rslsport.com',     N'23 Nguyễn Hữu Cảnh, Bình Thạnh, TP.HCM',1, SYSDATETIME()),
        (N'Apacs Sports Vietnam',            N'Hoàng Văn Tuấn',     '0283000005', 'apacs@apacs.vn',       N'56 Điện Biên Phủ, Q.3, TP.HCM',         1, SYSDATETIME()),
        (N'Công ty CP Thể thao Đại Việt',   N'Vũ Thị Mai',         '0283000006', 'daiviet@sport.vn',     N'89 Lê Thánh Tôn, Q.1, TP.HCM',          1, SYSDATETIME()),
        (N'Carlsbad Sport Việt Nam',         N'Đỗ Văn Bình',        '0283000007', 'carlsbad@sport.vn',    N'34 Bà Huyện Thanh Quan, Q.3, TP.HCM',   1, SYSDATETIME()),
        (N'Công ty TNHH Huy Hoàng Sport',   N'Ngô Thị Cúc',        '0283000008', 'huyhoangsport@vn.com', N'67 Nguyễn Đình Chiểu, Q.3, TP.HCM',     1, SYSDATETIME()),
        (N'Mizuno Việt Nam',                 N'Bùi Văn Đạt',        '0283000009', 'mizuno@mizuno.vn',     N'22 Đồng Khởi, Q.1, TP.HCM',             1, SYSDATETIME()),
        (N'Kawasaki Vietnam Co., Ltd',       N'Đinh Thị Hằng',      '0283000010', 'kawasaki@sport.vn',    N'11 Hai Bà Trưng, Q.1, TP.HCM',          1, SYSDATETIME());
    PRINT N'Seed NhaCungCap: OK';
END
ELSE
    PRINT N'NhaCungCap đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- PhieuNhap (10 phiếu nhập)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.PhieuNhap WHERE Ma_phieu_nhap = 'PN202601001')
BEGIN
    DECLARE @ncc1 INT = (SELECT TOP 1 Nha_cung_cap_id FROM dbo.NhaCungCap ORDER BY Nha_cung_cap_id);
    DECLARE @ncc2 INT = (SELECT TOP 1 Nha_cung_cap_id FROM dbo.NhaCungCap ORDER BY Nha_cung_cap_id OFFSET 1 ROWS);
    DECLARE @ncc3 INT = (SELECT TOP 1 Nha_cung_cap_id FROM dbo.NhaCungCap ORDER BY Nha_cung_cap_id OFFSET 2 ROWS);
    DECLARE @ncc4 INT = (SELECT TOP 1 Nha_cung_cap_id FROM dbo.NhaCungCap ORDER BY Nha_cung_cap_id OFFSET 3 ROWS);
    DECLARE @nv1w INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id);
    DECLARE @nv2w INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id OFFSET 1 ROWS);

    INSERT INTO dbo.PhieuNhap
        (Ma_phieu_nhap, Nha_cung_cap_id, Nhan_vien_id, Ngay_nhap, Tong_tien, Trang_thai, Ghi_chu, Ngay_tao)
    VALUES
        ('PN202601001', @ncc1, @nv1w, DATEADD(DAY,-60,SYSDATETIME()), 60000000, 1, N'Nhập vợt Yonex đợt 1',     SYSDATETIME()),
        ('PN202601002', @ncc2, @nv2w, DATEADD(DAY,-55,SYSDATETIME()), 47000000, 1, N'Nhập vợt Victor đợt 1',    SYSDATETIME()),
        ('PN202601003', @ncc3, @nv1w, DATEADD(DAY,-50,SYSDATETIME()), 32000000, 1, N'Nhập vợt Li-Ning đợt 1',  SYSDATETIME()),
        ('PN202601004', @ncc4, @nv2w, DATEADD(DAY,-45,SYSDATETIME()), 28000000, 1, N'Nhập cầu RSL đợt 1',      SYSDATETIME()),
        ('PN202601005', @ncc1, @nv1w, DATEADD(DAY,-40,SYSDATETIME()), 95000000, 1, N'Nhập giày Yonex đợt 1',   SYSDATETIME()),
        ('PN202601006', @ncc2, @nv2w, DATEADD(DAY,-35,SYSDATETIME()), 75000000, 1, N'Nhập giày Victor đợt 1',  SYSDATETIME()),
        ('PN202601007', @ncc1, @nv1w, DATEADD(DAY,-30,SYSDATETIME()), 30600000, 1, N'Nhập túi vợt đợt 1',      SYSDATETIME()),
        ('PN202601008', @ncc4, @nv2w, DATEADD(DAY,-20,SYSDATETIME()), 16500000, 1, N'Nhập cước BG65/BG80',     SYSDATETIME()),
        ('PN202601009', @ncc1, @nv1w, DATEADD(DAY,-10,SYSDATETIME()), 42000000, 1, N'Nhập vợt Yonex đợt 2',   SYSDATETIME()),
        ('PN202601010', @ncc3, @nv2w, SYSDATETIME(),                  19600000, 1, N'Nhập quần áo thi đấu',    SYSDATETIME());
    PRINT N'Seed PhieuNhap: OK';
END
ELSE
    PRINT N'PhieuNhap đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- PhieuNhapChiTiet (2 dòng mỗi phiếu = 20 dòng)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.PhieuNhapChiTiet)
BEGIN
    INSERT INTO dbo.PhieuNhapChiTiet
        (Phieu_nhap_id, San_pham_id, So_luong_nhap, Don_gia_nhap, Ghi_chu)
    SELECT
        pn.Phieu_nhap_id,
        sp.San_pham_id,
        50,
        sp.Gia_nhap,
        NULL
    FROM (
        SELECT Phieu_nhap_id, ROW_NUMBER() OVER (ORDER BY Phieu_nhap_id) AS rn
        FROM dbo.PhieuNhap
    ) pn
    CROSS APPLY (
        SELECT TOP 2 San_pham_id, Gia_nhap,
               ROW_NUMBER() OVER (ORDER BY San_pham_id) AS sp_rn
        FROM dbo.SanPham
        WHERE San_pham_id % 10 = (pn.rn % 10)
           OR San_pham_id % 10 = ((pn.rn + 1) % 10)
    ) sp;
    PRINT N'Seed PhieuNhapChiTiet: OK';
END
ELSE
    PRINT N'PhieuNhapChiTiet đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- PhieuXuat (5 phiếu xuất)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.PhieuXuat WHERE Ma_phieu_xuat = 'PX202601001')
BEGIN
    DECLARE @nv1x INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id);
    DECLARE @nv2x INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id OFFSET 1 ROWS);

    INSERT INTO dbo.PhieuXuat
        (Ma_phieu_xuat, Nhan_vien_id, Ngay_xuat, Tong_tien, Trang_thai, Ly_do, Ghi_chu, Ngay_tao)
    VALUES
        ('PX202601001', @nv1x, DATEADD(DAY,-30,SYSDATETIME()), 5550000, 1, N'Hàng lỗi / hư hỏng',         N'3 vợt bị gãy khung',       SYSDATETIME()),
        ('PX202601002', @nv2x, DATEADD(DAY,-20,SYSDATETIME()), 1110000, 1, N'Trả hàng nhà cung cấp',      N'Hàng không đúng quy cách', SYSDATETIME()),
        ('PX202601003', @nv1x, DATEADD(DAY,-15,SYSDATETIME()), 435000,  1, N'Hàng dùng nội bộ',           N'Dùng cho giải đấu nội bộ', SYSDATETIME()),
        ('PX202601004', @nv2x, DATEADD(DAY,-7, SYSDATETIME()), 540000,  1, N'Hàng lỗi / hư hỏng',         N'Cầu lông ẩm mốc',          SYSDATETIME()),
        ('PX202601005', @nv1x, SYSDATETIME(),                   340000,  0, N'Đang chờ xác nhận xuất kho', N'Chờ xác nhận',             SYSDATETIME());
    PRINT N'Seed PhieuXuat: OK';
END
ELSE
    PRINT N'PhieuXuat đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- PhieuXuatChiTiet (2 dòng mỗi phiếu = 10 dòng)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.PhieuXuatChiTiet)
BEGIN
    INSERT INTO dbo.PhieuXuatChiTiet
        (Phieu_xuat_id, San_pham_id, So_luong_xuat, Don_gia, Ghi_chu)
    SELECT
        px.Phieu_xuat_id,
        sp.San_pham_id,
        3,
        sp.Gia_ban,
        N'Xuất kho kiểm tra'
    FROM (
        SELECT Phieu_xuat_id, ROW_NUMBER() OVER (ORDER BY Phieu_xuat_id) AS rn
        FROM dbo.PhieuXuat
    ) px
    CROSS APPLY (
        SELECT TOP 2 San_pham_id, Gia_ban
        FROM dbo.SanPham
        ORDER BY San_pham_id
        OFFSET ((px.rn - 1) * 2) ROWS
    ) sp;
    PRINT N'Seed PhieuXuatChiTiet: OK';
END
ELSE
    PRINT N'PhieuXuatChiTiet đã có dữ liệu, bỏ qua.';
GO
