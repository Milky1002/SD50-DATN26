-- ============================================================
-- 17_seed_misc.sql
-- Dữ liệu mẫu: Gio_hang (5), Gio_hang_chi_tiet (10),
--               Trang_chu_danh_muc_noi_bat (5),
--               Trang_chu_san_pham_hot (10),
--               Lich_su_hoat_dong_nhan_vien (20)
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- Gio_hang (5 giỏ hàng - mix khách đăng nhập + session)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Gio_hang)
BEGIN
    DECLARE @kh1g INT = (SELECT TOP 1 Khach_hang_id FROM dbo.Khach_hang ORDER BY Khach_hang_id);
    DECLARE @kh2g INT = (SELECT TOP 1 Khach_hang_id FROM dbo.Khach_hang ORDER BY Khach_hang_id OFFSET 1 ROWS);
    DECLARE @kh3g INT = (SELECT TOP 1 Khach_hang_id FROM dbo.Khach_hang ORDER BY Khach_hang_id OFFSET 2 ROWS);

    INSERT INTO dbo.Gio_hang (Khach_hang_id, Session_id, Ngay_tao) VALUES
        (@kh1g,  NULL,                   DATEADD(HOUR,-5,SYSDATETIME())),
        (@kh2g,  NULL,                   DATEADD(HOUR,-3,SYSDATETIME())),
        (@kh3g,  NULL,                   DATEADD(HOUR,-1,SYSDATETIME())),
        (NULL,  'sess_aabbccdd1122',      DATEADD(MINUTE,-30,SYSDATETIME())),
        (NULL,  'sess_eeff00112233',      DATEADD(MINUTE,-10,SYSDATETIME()));
    PRINT N'Seed Gio_hang: OK';
END
ELSE
    PRINT N'Gio_hang đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Gio_hang_chi_tiet (2 item mỗi giỏ = 10 dòng)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Gio_hang_chi_tiet)
BEGIN
    INSERT INTO dbo.Gio_hang_chi_tiet
        (Gio_hang_id, San_pham_id, So_luong, Gia_tai_thoi_diem)
    SELECT
        gh.Gio_hang_id,
        sp.San_pham_id,
        1 + (gh.rn % 3),
        sp.Gia_ban
    FROM (
        SELECT Gio_hang_id, ROW_NUMBER() OVER (ORDER BY Gio_hang_id) AS rn
        FROM dbo.Gio_hang
    ) gh
    CROSS APPLY (
        SELECT TOP 2 San_pham_id, Gia_ban
        FROM dbo.SanPham
        ORDER BY San_pham_id
        OFFSET ((gh.rn - 1) * 2) ROWS
    ) sp;
    PRINT N'Seed Gio_hang_chi_tiet: OK';
END
ELSE
    PRINT N'Gio_hang_chi_tiet đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Trang_chu_danh_muc_noi_bat (5 danh mục nổi bật)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Trang_chu_danh_muc_noi_bat)
BEGIN
    INSERT INTO dbo.Trang_chu_danh_muc_noi_bat
        (Danh_muc_san_pham_id, Thu_tu, So_luong_hien_thi, Trang_thai, Ngay_tao)
    SELECT
        Danh_muc_san_pham_id,
        ROW_NUMBER() OVER (ORDER BY Danh_muc_san_pham_id),
        8,
        1,
        SYSDATETIME()
    FROM dbo.Danh_muc_san_pham
    WHERE Trang_thai = 1
    ORDER BY Danh_muc_san_pham_id
    OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY;
    PRINT N'Seed Trang_chu_danh_muc_noi_bat: OK';
END
ELSE
    PRINT N'Trang_chu_danh_muc_noi_bat đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Trang_chu_san_pham_hot (10 sản phẩm hot)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Trang_chu_san_pham_hot)
BEGIN
    INSERT INTO dbo.Trang_chu_san_pham_hot
        (San_pham_id, Thu_tu, Trang_thai, Ngay_tao)
    SELECT
        San_pham_id,
        ROW_NUMBER() OVER (ORDER BY San_pham_id),
        1,
        SYSDATETIME()
    FROM dbo.SanPham
    WHERE Trang_thai = 1
    ORDER BY Gia_ban DESC
    OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;
    PRINT N'Seed Trang_chu_san_pham_hot: OK';
END
ELSE
    PRINT N'Trang_chu_san_pham_hot đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- Lich_su_hoat_dong_nhan_vien (20 bản ghi log)
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.Lich_su_hoat_dong_nhan_vien)
BEGIN
    DECLARE @nv1l INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id);
    DECLARE @nv2l INT = (SELECT TOP 1 Nhan_vien_id FROM dbo.NhanVien ORDER BY Nhan_vien_id OFFSET 1 ROWS);

    -- Lấy danh sách hóa đơn để gắn log
    DECLARE @hd TABLE (id INT, rn INT);
    INSERT INTO @hd
    SELECT Hoa_don_id, ROW_NUMBER() OVER (ORDER BY Hoa_don_id)
    FROM dbo.HoaDon;

    INSERT INTO dbo.Lich_su_hoat_dong_nhan_vien
        (Nhan_vien_id, Ho_ten_nhan_vien, Hanh_dong, Doi_tuong, Doi_tuong_id, Mo_ta, Gia_tri, Thoi_gian)
    SELECT
        CASE WHEN h.rn % 2 = 1 THEN @nv1l ELSE @nv2l END,
        CASE WHEN h.rn % 2 = 1 THEN N'Nguyễn Admin' ELSE N'Trần Văn An' END,
        CASE
            WHEN h.rn % 3 = 0 THEN 'KH_TAO'
            WHEN h.rn % 3 = 1 THEN 'SALE_OFFLINE'
            ELSE 'KH_SUA'
        END,
        CASE
            WHEN h.rn % 3 = 0 THEN 'KHACH_HANG'
            WHEN h.rn % 3 = 1 THEN 'HOA_DON'
            ELSE 'KHACH_HANG'
        END,
        h.id,
        N'Hoạt động nhân viên #' + CAST(h.rn AS NVARCHAR),
        hd.Tong_tien_sau_khi_giam,
        DATEADD(MINUTE, -h.rn * 15, SYSDATETIME())
    FROM @hd h
    JOIN dbo.HoaDon hd ON hd.Hoa_don_id = h.id;
    PRINT N'Seed Lich_su_hoat_dong_nhan_vien: OK';
END
ELSE
    PRINT N'Lich_su_hoat_dong_nhan_vien đã có dữ liệu, bỏ qua.';
GO
