-- ============================================================
-- 13_seed_customer.sql
-- Dữ liệu mẫu: Khach_hang (20 bản ghi)
-- ============================================================

USE sd50;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Khach_hang WHERE Email = 'khachhang01@gmail.com')
BEGIN
    INSERT INTO dbo.Khach_hang
        (Ten_khach_hang, SDT, Email, Trang_thai, Ngay_tao, Dia_chi_khach_hang, Mat_khau, Tai_khoan_id)
    VALUES
        (N'Nguyễn Thị Anh',    '0911111101', 'khachhang01@gmail.com', 1, SYSDATETIME(), N'101 Lê Lợi, Q.1, TP.HCM',              NULL, NULL),
        (N'Trần Văn Bảo',      '0911111102', 'khachhang02@gmail.com', 1, SYSDATETIME(), N'202 Nguyễn Huệ, Q.1, TP.HCM',           NULL, NULL),
        (N'Lê Thị Châu',       '0911111103', 'khachhang03@gmail.com', 1, SYSDATETIME(), N'303 Điện Biên Phủ, Bình Thạnh, TP.HCM', NULL, NULL),
        (N'Phạm Văn Duy',      '0911111104', 'khachhang04@gmail.com', 1, SYSDATETIME(), N'404 Cách Mạng Tháng 8, Q.3, TP.HCM',    NULL, NULL),
        (N'Hoàng Thị Ema',     '0911111105', 'khachhang05@gmail.com', 1, SYSDATETIME(), N'505 Pasteur, Q.3, TP.HCM',               NULL, NULL),
        (N'Vũ Văn Phát',       '0911111106', 'khachhang06@gmail.com', 1, SYSDATETIME(), N'606 Nam Kỳ Khởi Nghĩa, Q.3, TP.HCM',    NULL, NULL),
        (N'Đỗ Thị Gấm',        '0911111107', 'khachhang07@gmail.com', 1, SYSDATETIME(), N'707 Trần Phú, Q.5, TP.HCM',             NULL, NULL),
        (N'Ngô Văn Hạnh',      '0911111108', 'khachhang08@gmail.com', 1, SYSDATETIME(), N'808 An Dương Vương, Q.5, TP.HCM',        NULL, NULL),
        (N'Bùi Thị Iris',      '0911111109', 'khachhang09@gmail.com', 1, SYSDATETIME(), N'909 Lạc Long Quân, Q.11, TP.HCM',       NULL, NULL),
        (N'Đinh Văn Khôi',     '0911111110', 'khachhang10@gmail.com', 1, SYSDATETIME(), N'1010 Trường Chinh, Tân Bình, TP.HCM',   NULL, NULL),
        (N'Lý Thị Loan',       '0911111111', 'khachhang11@gmail.com', 1, SYSDATETIME(), N'11 Cộng Hòa, Tân Bình, TP.HCM',         NULL, NULL),
        (N'Trịnh Văn Minh',    '0911111112', 'khachhang12@gmail.com', 1, SYSDATETIME(), N'12 Bình Thới, Q.11, TP.HCM',            NULL, NULL),
        (N'Nguyễn Thị Ngân',   '0911111113', 'khachhang13@gmail.com', 1, SYSDATETIME(), N'13 Âu Cơ, Tân Bình, TP.HCM',            NULL, NULL),
        (N'Phan Văn Oánh',     '0911111114', 'khachhang14@gmail.com', 1, SYSDATETIME(), N'14 Tân Sơn Nhì, Tân Phú, TP.HCM',      NULL, NULL),
        (N'Võ Thị Phụng',      '0911111115', 'khachhang15@gmail.com', 1, SYSDATETIME(), N'15 Bình Long, Tân Phú, TP.HCM',         NULL, NULL),
        (N'Cao Văn Quý',       '0911111116', 'khachhang16@gmail.com', 1, SYSDATETIME(), N'16 Hòa Bình, Q.11, TP.HCM',             NULL, NULL),
        (N'Lưu Thị Rin',       '0911111117', 'khachhang17@gmail.com', 1, SYSDATETIME(), N'17 Tân Hương, Tân Phú, TP.HCM',         NULL, NULL),
        (N'Hà Văn Sáng',       '0911111118', 'khachhang18@gmail.com', 1, SYSDATETIME(), N'18 Phan Anh, Tân Phú, TP.HCM',          NULL, NULL),
        (N'Đặng Thị Thủy',     '0911111119', 'khachhang19@gmail.com', 1, SYSDATETIME(), N'19 Lê Văn Khương, Q.12, TP.HCM',        NULL, NULL),
        (N'Kiều Văn Uyên',     '0911111120', 'khachhang20@gmail.com', 1, SYSDATETIME(), N'20 Tô Ký, Q.12, TP.HCM',                NULL, NULL);
    PRINT N'Seed Khach_hang: OK';
END
ELSE
    PRINT N'Khach_hang đã có dữ liệu, bỏ qua.';
GO
