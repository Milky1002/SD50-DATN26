-- ============================================================
-- 01_create_database.sql
-- Tạo database sd50 nếu chưa tồn tại
-- Chạy với user có quyền CREATE DATABASE (sa hoặc sysadmin)
-- ============================================================

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'sd50')
BEGIN
    CREATE DATABASE sd50
        COLLATE Vietnamese_CI_AS;
    PRINT N'Database sd50 đã được tạo.';
END
ELSE
BEGIN
    PRINT N'Database sd50 đã tồn tại, bỏ qua bước tạo.';
END
GO

USE sd50;
GO
