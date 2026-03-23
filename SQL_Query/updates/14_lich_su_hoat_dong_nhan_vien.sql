-- =============================================
-- Script 14: Tạo bảng Lịch sử hoạt động nhân viên
-- Ghi lại các hoạt động của nhân viên:
--   SALE_OFFLINE  - bán hàng tại quầy (POS)
--   KH_TAO        - tạo khách hàng mới
--   KH_SUA        - sửa thông tin khách hàng
-- =============================================
USE [sd50];
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Lich_su_hoat_dong_nhan_vien')
BEGIN
    CREATE TABLE [dbo].[Lich_su_hoat_dong_nhan_vien] (
        [Id]               INT           IDENTITY(1,1) NOT NULL,
        [Nhan_vien_id]     INT           NOT NULL,
        [Ho_ten_nhan_vien] NVARCHAR(255) NULL,
        [Hanh_dong]        VARCHAR(50)   NOT NULL,
        [Doi_tuong]        VARCHAR(50)   NULL,
        [Doi_tuong_id]     INT           NULL,
        [Mo_ta]            NVARCHAR(MAX) NULL,
        [Gia_tri]          DECIMAL(18,2) NULL,
        [Thoi_gian]        DATETIME      NOT NULL CONSTRAINT [DF_LichSuHD_ThoiGian] DEFAULT GETDATE(),
        CONSTRAINT [PK_Lich_su_hoat_dong_nhan_vien] PRIMARY KEY ([Id])
    );

    CREATE INDEX [IX_LichSuHD_NhanVienId]  ON [dbo].[Lich_su_hoat_dong_nhan_vien] ([Nhan_vien_id]);
    CREATE INDEX [IX_LichSuHD_ThoiGian]    ON [dbo].[Lich_su_hoat_dong_nhan_vien] ([Thoi_gian]);

    PRINT N'Script 14: Tạo bảng Lich_su_hoat_dong_nhan_vien thành công.';
END
ELSE
BEGIN
    PRINT N'Script 14: Bảng Lich_su_hoat_dong_nhan_vien đã tồn tại — bỏ qua.';
END
GO
