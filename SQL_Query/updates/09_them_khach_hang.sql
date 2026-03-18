-- =============================================
-- Script 09: Thêm khách hàng mẫu
-- Dữ liệu tiếng Việt có dấu
-- =============================================
USE [sd50];
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Nguyễn Thị Lan')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Nguyễn Thị Lan', N'0901234567', N'lan.nguyen@gmail.com', 1, N'Số 10, Đường Nguyễn Trãi, Quận Thanh Xuân, Hà Nội');

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Phạm Văn Hùng')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Phạm Văn Hùng', N'0918765432', N'hung.pham@gmail.com', 1, N'Số 25, Phố Hàng Bông, Quận Hoàn Kiếm, Hà Nội');

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Trương Thị Mai')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Trương Thị Mai', N'0932456789', N'mai.truong@gmail.com', 1, N'Số 8, Đường Trần Phú, Quận Hải Châu, TP. Đà Nẵng');

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Hoàng Đức Anh')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Hoàng Đức Anh', N'0945678901', N'anh.hoang@gmail.com', 1, N'Số 30, Đường Lý Tự Trọng, Quận 1, TP. Hồ Chí Minh');

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Vũ Thị Hồng Nhung')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Vũ Thị Hồng Nhung', N'0956789012', N'nhung.vu@gmail.com', 1, N'Số 12, Phố Nguyễn Du, TP. Hải Phòng');

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Đặng Quốc Tuấn')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Đặng Quốc Tuấn', N'0967890123', N'tuan.dang@gmail.com', 1, N'Số 5, Đường Võ Nguyên Giáp, TP. Huế, Thừa Thiên Huế');

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Bùi Thị Thanh Hà')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Bùi Thị Thanh Hà', N'0978901234', N'ha.bui@gmail.com', 1, N'Số 18, Phố Phan Chu Trinh, Quận Hoàn Kiếm, Hà Nội');

IF NOT EXISTS (SELECT 1 FROM [dbo].[Khach_hang] WHERE [Ten_khach_hang] = N'Lý Minh Quân')
    INSERT INTO [dbo].[Khach_hang] ([Ten_khach_hang], [SDT], [Email], [Trang_thai], [Dia_chi_khach_hang])
    VALUES (N'Lý Minh Quân', N'0989012345', N'quan.ly@gmail.com', 1, N'Số 22, Đường Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh');

PRINT N'✓ Đã thêm 8 khách hàng mẫu (tiếng Việt có dấu)';
GO
