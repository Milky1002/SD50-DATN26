-- ============================================================================
-- install_full_database.sql
-- One-file full database installer for SD50-DATN26
-- Usage: run this single file on SQL Server via SSMS or sqlcmd.
-- This script creates database [sd50], creates the consolidated schema,
-- then applies all idempotent update scripts to align old/new structures.
-- ============================================================================

PRINT N'========================================================';
PRINT N' SD50-DATN26 - FULL DATABASE INSTALLER';
PRINT N'========================================================';
PRINT N' 1) Create database + base schema';
PRINT N' 2) Apply all update scripts';
PRINT N'========================================================';
GO

:r SetupDatabaseSQL.sql

PRINT N'';
PRINT N'========================================================';
PRINT N' APPLYING POST-SETUP UPDATE SCRIPTS';
PRINT N'========================================================';
GO

:r updates\01_cap_nhat_chuc_vu.sql
:r updates\02_cap_nhat_tai_khoan.sql
:r updates\03_cap_nhat_nhan_vien.sql
:r updates\04_cap_nhat_hinh_thuc_thanh_toan.sql
:r updates\05_cap_nhat_danh_muc.sql
:r updates\05b_fix_danh_muc.sql
:r updates\06_cap_nhat_mau_sac.sql
:r updates\06b_fix_mau_sac.sql
:r updates\07_cap_nhat_san_pham.sql
:r updates\07b_fix_san_pham.sql
:r updates\08_cap_nhat_khuyen_mai.sql
:r updates\09_them_khach_hang.sql
:r updates\10_shop_tables.sql
:r updates\11_sync_storefront_and_homepage.sql
:r updates\12_unify_tai_khoan.sql
:r updates\13_normalize_customer_account_links.sql
:r updates\14_lich_su_hoat_dong_nhan_vien.sql

PRINT N'';
PRINT N'========================================================';
PRINT N' ✓ FULL DATABASE INSTALL COMPLETE';
PRINT N' Database: sd50';
PRINT N' Default admin: admin / admin@123';
PRINT N'========================================================';
GO
