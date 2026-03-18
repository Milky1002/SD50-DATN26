-- =============================================
-- Script 03: Cập nhật bảng NhanVien (Nhân viên)
-- Sửa text tiếng Việt có dấu + thêm nhân viên mẫu
-- =============================================
USE [sd50];
GO

-- Cập nhật nhân viên Admin
UPDATE [dbo].[NhanVien]
SET [Ho_ten] = N'Nguyễn Văn Admin',
    [Gioi_tinh] = N'Nam',
    [Dia_chi] = N'Số 1, Phố Huế, Quận Hai Bà Trưng, Hà Nội',
    [Tai_khoan_id] = 1
WHERE [Nhan_vien_id] = 1;

PRINT N'✓ Đã cập nhật nhân viên Admin';

-- Thêm nhân viên bán hàng mẫu 1
DECLARE @tkNV1 INT = (SELECT [Tai_khoan_id] FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien01');
IF @tkNV1 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[NhanVien] WHERE [Tai_khoan_id] = @tkNV1)
BEGIN
    INSERT INTO [dbo].[NhanVien] ([Ho_ten], [Gioi_tinh], [SDT], [Email], [Dia_chi], [Ngay_sinh], [Chuc_vu_id], [Trang_thai], [Tai_khoan_id])
    VALUES (N'Trần Thị Hương', N'Nữ', N'0912345678', N'huong.tran@shop.vn',
            N'Số 15, Đường Lê Lợi, Quận 1, TP. Hồ Chí Minh', '1995-05-15', 2, 1, @tkNV1);
    PRINT N'✓ Đã thêm nhân viên Trần Thị Hương';
END;

-- Thêm nhân viên bán hàng mẫu 2
DECLARE @tkNV2 INT = (SELECT [Tai_khoan_id] FROM [dbo].[TaiKhoan] WHERE [User_name] = N'nhanvien02');
IF @tkNV2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM [dbo].[NhanVien] WHERE [Tai_khoan_id] = @tkNV2)
BEGIN
    INSERT INTO [dbo].[NhanVien] ([Ho_ten], [Gioi_tinh], [SDT], [Email], [Dia_chi], [Ngay_sinh], [Chuc_vu_id], [Trang_thai], [Tai_khoan_id])
    VALUES (N'Lê Minh Đức', N'Nam', N'0987654321', N'duc.le@shop.vn',
            N'Số 42, Phố Bạch Mai, Quận Hai Bà Trưng, Hà Nội', '1998-08-20', 2, 1, @tkNV2);
    PRINT N'✓ Đã thêm nhân viên Lê Minh Đức';
END;
GO
