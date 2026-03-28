-- ============================================================
-- 11_seed_core.sql
-- Dữ liệu mẫu: ChucVu (5), TaiKhoan (22), NhanVien (20)
-- Mật khẩu BCrypt tương ứng "admin@123"
-- ============================================================

USE sd50;
GO

-- -------------------------------------------------------
-- ChucVu
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.ChucVu WHERE Ten_chuc_vu = N'Quản lý')
BEGIN
    SET IDENTITY_INSERT dbo.ChucVu ON;
    INSERT INTO dbo.ChucVu (Chuc_vu_id, Ten_chuc_vu) VALUES
        (1, N'Quản lý'),
        (2, N'Nhân viên bán hàng'),
        (3, N'Nhân viên kho'),
        (4, N'Kế toán'),
        (5, N'Nhân viên IT');
    SET IDENTITY_INSERT dbo.ChucVu OFF;
    PRINT N'Seed ChucVu: OK';
END
ELSE
    PRINT N'ChucVu đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- TaiKhoan
-- Mật khẩu lưu dạng plaintext "admin@123" — AuthService sẽ
-- tự động migrate sang BCrypt khi đăng nhập thành công lần đầu
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE User_name = 'admin')
BEGIN
    INSERT INTO dbo.TaiKhoan
        (User_name, Pass_word, Trang_thai, Role_code, Email, Ho_ten, So_dien_thoai, Ngay_tao)
    VALUES
        ('admin',      'admin@123', 1, 'ADMIN', 'admin@badminton.vn',       N'Nguyễn Admin',       '0901000001', SYSDATETIME()),
        ('nhanvien01', 'admin@123', 1, 'STAFF', 'nv01@badminton.vn',        N'Trần Văn An',        '0901000002', SYSDATETIME()),
        ('nhanvien02', 'admin@123', 1, 'STAFF', 'nv02@badminton.vn',        N'Lê Thị Bình',        '0901000003', SYSDATETIME()),
        ('nhanvien03', 'admin@123', 1, 'STAFF', 'nv03@badminton.vn',        N'Phạm Thị Cúc',       '0901000004', SYSDATETIME()),
        ('nhanvien04', 'admin@123', 1, 'STAFF', 'nv04@badminton.vn',        N'Hoàng Văn Dũng',     '0901000005', SYSDATETIME()),
        ('nhanvien05', 'admin@123', 1, 'STAFF', 'nv05@badminton.vn',        N'Vũ Thị Em',          '0901000006', SYSDATETIME()),
        ('nhanvien06', 'admin@123', 1, 'STAFF', 'nv06@badminton.vn',        N'Đỗ Văn Phúc',        '0901000007', SYSDATETIME()),
        ('nhanvien07', 'admin@123', 1, 'STAFF', 'nv07@badminton.vn',        N'Ngô Thị Giang',      '0901000008', SYSDATETIME()),
        ('nhanvien08', 'admin@123', 1, 'STAFF', 'nv08@badminton.vn',        N'Bùi Văn Hải',        '0901000009', SYSDATETIME()),
        ('nhanvien09', 'admin@123', 1, 'STAFF', 'nv09@badminton.vn',        N'Đinh Thị Hoa',       '0901000010', SYSDATETIME()),
        ('nhanvien10', 'admin@123', 1, 'STAFF', 'nv10@badminton.vn',        N'Lý Văn Kiên',        '0901000011', SYSDATETIME()),
        ('nhanvien11', 'admin@123', 1, 'STAFF', 'nv11@badminton.vn',        N'Trịnh Thị Lan',      '0901000012', SYSDATETIME()),
        ('nhanvien12', 'admin@123', 1, 'STAFF', 'nv12@badminton.vn',        N'Nguyễn Văn Minh',    '0901000013', SYSDATETIME()),
        ('nhanvien13', 'admin@123', 1, 'STAFF', 'nv13@badminton.vn',        N'Phan Thị Ngọc',      '0901000014', SYSDATETIME()),
        ('nhanvien14', 'admin@123', 1, 'STAFF', 'nv14@badminton.vn',        N'Võ Văn Oanh',        '0901000015', SYSDATETIME()),
        ('nhanvien15', 'admin@123', 1, 'STAFF', 'nv15@badminton.vn',        N'Cao Thị Phương',     '0901000016', SYSDATETIME()),
        ('nhanvien16', 'admin@123', 1, 'STAFF', 'nv16@badminton.vn',        N'Lưu Văn Quang',      '0901000017', SYSDATETIME()),
        ('nhanvien17', 'admin@123', 1, 'STAFF', 'nv17@badminton.vn',        N'Hà Thị Ry',          '0901000018', SYSDATETIME()),
        ('nhanvien18', 'admin@123', 1, 'STAFF', 'nv18@badminton.vn',        N'Đặng Văn Sơn',       '0901000019', SYSDATETIME()),
        ('nhanvien19', 'admin@123', 1, 'STAFF', 'nv19@badminton.vn',        N'Kiều Thị Thu',       '0901000020', SYSDATETIME()),
        ('nhanvien20', 'admin@123', 1, 'STAFF', 'nv20@badminton.vn',        N'Mai Văn Thành',      '0901000021', SYSDATETIME()),
        ('quanly01',   'admin@123', 1, 'ADMIN', 'ql01@badminton.vn',        N'Nguyễn Quản Lý',     '0901000022', SYSDATETIME());
    PRINT N'Seed TaiKhoan: OK';
END
ELSE
    PRINT N'TaiKhoan đã có dữ liệu, bỏ qua.';
GO

-- -------------------------------------------------------
-- NhanVien (20 bản ghi) — liên kết TaiKhoan bằng sub-query
-- -------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE Ho_ten = N'Nguyễn Admin')
BEGIN
    INSERT INTO dbo.NhanVien
        (Ho_ten, Gioi_tinh, SDT, Email, Dia_chi, Ngay_sinh, Chuc_vu_id, Tai_khoan_id, Trang_thai, Ngay_tao)
    VALUES
        (N'Nguyễn Admin',    N'Nam', '0901000001', 'admin@badminton.vn',    N'123 Lý Thường Kiệt, Q.10, TP.HCM',              '1985-03-15', 1, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='admin'),      1, SYSDATETIME()),
        (N'Trần Văn An',     N'Nam', '0901000002', 'nv01@badminton.vn',     N'45 Nguyễn Huệ, Q.1, TP.HCM',                    '1992-06-20', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien01'),  1, SYSDATETIME()),
        (N'Lê Thị Bình',     N'Nữ',  '0901000003', 'nv02@badminton.vn',     N'78 Trần Hưng Đạo, Q.5, TP.HCM',                 '1994-11-08', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien02'),  1, SYSDATETIME()),
        (N'Phạm Thị Cúc',    N'Nữ',  '0901000004', 'nv03@badminton.vn',     N'12 Cách Mạng Tháng 8, Q.3, TP.HCM',             '1996-04-17', 3, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien03'),  1, SYSDATETIME()),
        (N'Hoàng Văn Dũng',  N'Nam', '0901000005', 'nv04@badminton.vn',     N'56 Điện Biên Phủ, Bình Thạnh, TP.HCM',          '1990-09-25', 3, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien04'),  1, SYSDATETIME()),
        (N'Vũ Thị Em',       N'Nữ',  '0901000006', 'nv05@badminton.vn',     N'89 Lê Văn Sỹ, Q.3, TP.HCM',                     '1995-01-30', 4, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien05'),  1, SYSDATETIME()),
        (N'Đỗ Văn Phúc',     N'Nam', '0901000007', 'nv06@badminton.vn',     N'34 Bùi Thị Xuân, Q.1, TP.HCM',                  '1993-07-14', 4, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien06'),  1, SYSDATETIME()),
        (N'Ngô Thị Giang',   N'Nữ',  '0901000008', 'nv07@badminton.vn',     N'67 Hai Bà Trưng, Q.1, TP.HCM',                  '1997-02-28', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien07'),  1, SYSDATETIME()),
        (N'Bùi Văn Hải',     N'Nam', '0901000009', 'nv08@badminton.vn',     N'23 Pasteur, Q.3, TP.HCM',                        '1991-12-05', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien08'),  1, SYSDATETIME()),
        (N'Đinh Thị Hoa',    N'Nữ',  '0901000010', 'nv09@badminton.vn',     N'90 Nguyễn Đình Chiểu, Q.3, TP.HCM',             '1998-08-19', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien09'),  1, SYSDATETIME()),
        (N'Lý Văn Kiên',     N'Nam', '0901000011', 'nv10@badminton.vn',     N'11 Lê Duẩn, Q.1, TP.HCM',                       '1989-05-11', 5, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien10'),  1, SYSDATETIME()),
        (N'Trịnh Thị Lan',   N'Nữ',  '0901000012', 'nv11@badminton.vn',     N'55 Nguyễn Văn Trỗi, Phú Nhuận, TP.HCM',         '1996-10-22', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien11'),  1, SYSDATETIME()),
        (N'Nguyễn Văn Minh', N'Nam', '0901000013', 'nv12@badminton.vn',     N'44 Trường Chinh, Tân Bình, TP.HCM',              '1993-03-07', 3, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien12'),  1, SYSDATETIME()),
        (N'Phan Thị Ngọc',   N'Nữ',  '0901000014', 'nv13@badminton.vn',     N'88 Hoàng Văn Thụ, Phú Nhuận, TP.HCM',           '1997-06-13', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien13'),  1, SYSDATETIME()),
        (N'Võ Văn Oanh',     N'Nam', '0901000015', 'nv14@badminton.vn',     N'22 Cộng Hòa, Tân Bình, TP.HCM',                 '1991-01-18', 3, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien14'),  1, SYSDATETIME()),
        (N'Cao Thị Phương',  N'Nữ',  '0901000016', 'nv15@badminton.vn',     N'16 Bình Thới, Q.11, TP.HCM',                    '1995-09-04', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien15'),  1, SYSDATETIME()),
        (N'Lưu Văn Quang',   N'Nam', '0901000017', 'nv16@badminton.vn',     N'77 Lạc Long Quân, Q.11, TP.HCM',                '1988-12-29', 4, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien16'),  1, SYSDATETIME()),
        (N'Hà Thị Ry',       N'Nữ',  '0901000018', 'nv17@badminton.vn',     N'33 Âu Cơ, Tân Bình, TP.HCM',                    '1999-04-01', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien17'),  1, SYSDATETIME()),
        (N'Đặng Văn Sơn',    N'Nam', '0901000019', 'nv18@badminton.vn',     N'19 Tân Sơn Nhì, Tân Phú, TP.HCM',               '1990-07-23', 3, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien18'),  1, SYSDATETIME()),
        (N'Kiều Thị Thu',    N'Nữ',  '0901000020', 'nv19@badminton.vn',     N'60 Bình Long, Tân Phú, TP.HCM',                 '1994-11-15', 2, (SELECT Tai_khoan_id FROM dbo.TaiKhoan WHERE User_name='nhanvien19'),  1, SYSDATETIME());
    PRINT N'Seed NhanVien: OK';
END
ELSE
    PRINT N'NhanVien đã có dữ liệu, bỏ qua.';
GO
