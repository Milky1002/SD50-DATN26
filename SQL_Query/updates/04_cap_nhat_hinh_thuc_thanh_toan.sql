-- =============================================
-- Script 04: Cập nhật HinhThucThanhToan
-- Sửa text tiếng Việt có dấu
-- =============================================
USE [sd50];
GO

-- Xóa dữ liệu cũ và thêm lại với tiếng Việt có dấu
DELETE FROM [dbo].[HinhThucThanhToan];
GO

DBCC CHECKIDENT ('[dbo].[HinhThucThanhToan]', RESEED, 0);
GO

INSERT INTO [dbo].[HinhThucThanhToan] ([Ten_hinh_thuc], [Mo_ta])
VALUES (N'Tiền mặt', N'Thanh toán bằng tiền mặt'),
       (N'Chuyển khoản', N'Chuyển khoản ngân hàng'),
       (N'Thẻ tín dụng', N'Thanh toán bằng thẻ tín dụng'),
       (N'Ví điện tử', N'Thanh toán qua ví điện tử');

PRINT N'✓ Đã cập nhật bảng HinhThucThanhToan (tiếng Việt có dấu)';
GO
