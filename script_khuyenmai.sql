USE sd50;
GO

-- Xóa data cũ (nếu có)
DELETE FROM Lich_su_ap_dung_khuyen_mai;
DELETE FROM Chuong_trinh_khuyen_mai_chi_tiet;
DELETE FROM Chuong_trinh_khuyen_mai;
GO

-- Thêm data mẫu cho test
-- 1. Chương trình giảm giá hóa đơn - Đang hoạt động
INSERT INTO Chuong_trinh_khuyen_mai (
    Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta, 
    Loai_khuyen_mai, Loai_giam, Gia_tri_giam, Giam_toi_da, Don_hang_toi_thieu,
    Ngay_bat_dau, Ngay_ket_thuc, 
    Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung, 
    Khach_hang_ap_dung, Kenh_ban_ap_dung, Ngay_trong_tuan,
    Trang_thai, Ngay_tao
) VALUES (
    N'SALE10', 
    N'Giảm 10% hóa đơn trên 3 triệu', 
    N'Giảm 10% cho hóa đơn trên 3.000.000đ, giảm tối đa 500.000đ',
    1, -- Giảm giá hóa đơn
    1, -- Theo %
    10, -- 10%
    500000, -- Giảm tối đa 500k
    3000000, -- Đơn hàng tối thiểu 3tr
    '2026-03-01 00:00:00', 
    '2026-12-31 23:59:59',
    0, -- Không áp dụng cùng nhiều CTKM
    1, -- Tự động áp dụng
    1, -- Tất cả khách hàng
    N'["Facebook", "Zalo"]', -- Kênh bán
    N'["2", "3", "4", "5", "6"]', -- Thứ 2-6
    1, -- Hoạt động
    GETDATE()
);

-- 2. Chương trình giảm giá hóa đơn - Theo tiền
INSERT INTO Chuong_trinh_khuyen_mai (
    Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta, 
    Loai_khuyen_mai, Loai_giam, Gia_tri_giam, Don_hang_toi_thieu,
    Ngay_bat_dau, Ngay_ket_thuc, 
    Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung, 
    Khach_hang_ap_dung, Kenh_ban_ap_dung,
    Trang_thai, Ngay_tao
) VALUES (
    N'GIAM100K', 
    N'Giảm 100k cho đơn hàng trên 1 triệu', 
    N'Giảm 100.000đ cho mọi đơn hàng trên 1.000.000đ',
    1, -- Giảm giá hóa đơn
    2, -- Theo tiền
    100000, -- Giảm 100k
    1000000, -- Đơn hàng tối thiểu 1tr
    '2026-03-01 00:00:00', 
    '2026-06-30 23:59:59',
    1, -- Có thể áp dụng cùng nhiều CTKM
    1, -- Tự động áp dụng
    1, -- Tất cả khách hàng
    N'["Facebook", "Zalo", "TikTok"]',
    1, -- Hoạt động
    GETDATE()
);

-- 3. Chương trình giảm giá sản phẩm
INSERT INTO Chuong_trinh_khuyen_mai (
    Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta, 
    Loai_khuyen_mai, Loai_giam, Gia_tri_giam,
    Ngay_bat_dau, Ngay_ket_thuc, 
    Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung, 
    Khach_hang_ap_dung, Kenh_ban_ap_dung,
    Trang_thai, Ngay_tao
) VALUES (
    N'GIAM50K-SP', 
    N'Giảm 50k cho sản phẩm vợt cầu lông', 
    N'Giảm 50.000đ cho các sản phẩm vợt cầu lông',
    2, -- Giảm giá sản phẩm
    2, -- Theo tiền
    50000, -- Giảm 50k
    '2026-03-01 00:00:00', 
    '2026-12-31 23:59:59',
    1, -- Có thể áp dụng cùng nhiều CTKM
    0, -- Không tự động áp dụng
    1, -- Tất cả khách hàng
    N'["Facebook", "Zalo"]',
    1, -- Hoạt động
    GETDATE()
);

-- 4. Chương trình tặng hàng
INSERT INTO Chuong_trinh_khuyen_mai (
    Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta, 
    Loai_khuyen_mai, Loai_giam, Gia_tri_giam,
    Ngay_bat_dau, Ngay_ket_thuc, 
    Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung, 
    Khach_hang_ap_dung,
    Trang_thai, Ngay_tao
) VALUES (
    N'TANG-QUA', 
    N'Tặng quà khi mua trên 5 triệu', 
    N'Tặng 1 áo thun khi mua hàng trên 5.000.000đ',
    3, -- Tặng hàng
    2, -- Theo tiền
    0, -- Không giảm giá
    '2026-03-01 00:00:00', 
    '2026-12-31 23:59:59',
    1, 
    1, 
    1,
    1, -- Hoạt động
    GETDATE()
);

-- 5. Chương trình đồng giá
INSERT INTO Chuong_trinh_khuyen_mai (
    Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta, 
    Loai_khuyen_mai, Loai_giam, Gia_tri_giam,
    Ngay_bat_dau, Ngay_ket_thuc, 
    Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung, 
    Khach_hang_ap_dung,
    Trang_thai, Ngay_tao
) VALUES (
    N'DONGGIA99K', 
    N'Đồng giá 99k cho sản phẩm sale', 
    N'Tất cả sản phẩm sale chỉ 99.000đ',
    4, -- Đồng giá
    2, -- Theo tiền
    99000, -- Giá đồng giá
    '2026-03-01 00:00:00', 
    '2026-03-31 23:59:59',
    0, 
    0, 
    1,
    1, -- Hoạt động
    GETDATE()
);

-- 6. Chương trình đã hết hạn (để test filter)
INSERT INTO Chuong_trinh_khuyen_mai (
    Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta, 
    Loai_khuyen_mai, Loai_giam, Gia_tri_giam,
    Ngay_bat_dau, Ngay_ket_thuc, 
    Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung, 
    Khach_hang_ap_dung,
    Trang_thai, Ngay_tao
) VALUES (
    N'OLD-PROMO', 
    N'Chương trình đã kết thúc', 
    N'Chương trình này đã kết thúc',
    1,
    1,
    15,
    '2025-01-01 00:00:00', 
    '2025-12-31 23:59:59',
    0, 
    1, 
    1,
    0, -- Ngừng hoạt động
    GETDATE()
);

-- 7. Chương trình VIP (để test khách hàng cụ thể)
INSERT INTO Chuong_trinh_khuyen_mai (
    Ma_chuong_trinh, Ten_chuong_trinh, Mo_ta, 
    Loai_khuyen_mai, Loai_giam, Gia_tri_giam, Giam_toi_da,
    Ngay_bat_dau, Ngay_ket_thuc, 
    Ap_dung_cung_nhieu_ctkm, Tu_dong_ap_dung, 
    Khach_hang_ap_dung,
    Trang_thai, Ngay_tao
) VALUES (
    N'VIP20', 
    N'Giảm 20% cho khách VIP', 
    N'Giảm 20% cho khách hàng VIP, tối đa 1 triệu',
    1,
    1,
    20,
    1000000,
    '2026-03-01 00:00:00', 
    '2026-12-31 23:59:59',
    0, 
    1, 
    3, -- Khách hàng cụ thể
    1, -- Hoạt động
    GETDATE()
);

-- Kiểm tra kết quả
SELECT 
    Ma_chuong_trinh AS [Mã],
    Ten_chuong_trinh AS [Tên],
    CASE Loai_khuyen_mai
        WHEN 1 THEN N'Giảm giá hóa đơn'
        WHEN 2 THEN N'Giảm giá sản phẩm'
        WHEN 3 THEN N'Tặng hàng'
        WHEN 4 THEN N'Đồng giá'
    END AS [Loại],
    CASE Trang_thai
        WHEN 1 THEN N'Hoạt động'
        ELSE N'Ngừng'
    END AS [Trạng thái],
    Ngay_bat_dau AS [Bắt đầu],
    Ngay_ket_thuc AS [Kết thúc]
FROM Chuong_trinh_khuyen_mai
ORDER BY Ngay_tao DESC;

PRINT N'✅ Đã thêm 7 chương trình khuyến mại mẫu!';
GO
