-- =============================================
-- Script 12: Unify TaiKhoan (unified user model)
-- IDEMPOTENT: safe to run multiple times
-- =============================================
-- Adds Role_code, Email, Ho_ten, So_dien_thoai to TaiKhoan
-- Backfills staff roles from ChucVu
-- Creates TaiKhoan rows for KhachHang without one
-- =============================================

USE [sd50];
GO

PRINT N'[12] Bắt đầu unify TaiKhoan...';
GO

-- ── 1. Add new columns to TaiKhoan (idempotent) ──────────────────────────────
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.TaiKhoan') AND name = 'Role_code')
BEGIN
    ALTER TABLE dbo.TaiKhoan ADD Role_code NVARCHAR(20) NOT NULL DEFAULT 'STAFF';
    PRINT N'  + TaiKhoan.Role_code added';
END

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.TaiKhoan') AND name = 'Email')
BEGIN
    ALTER TABLE dbo.TaiKhoan ADD Email NVARCHAR(255) NULL;
    PRINT N'  + TaiKhoan.Email added';
END

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.TaiKhoan') AND name = 'Ho_ten')
BEGIN
    ALTER TABLE dbo.TaiKhoan ADD Ho_ten NVARCHAR(255) NULL;
    PRINT N'  + TaiKhoan.Ho_ten added';
END

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.TaiKhoan') AND name = 'So_dien_thoai')
BEGIN
    ALTER TABLE dbo.TaiKhoan ADD So_dien_thoai NVARCHAR(50) NULL;
    PRINT N'  + TaiKhoan.So_dien_thoai added';
END

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.TaiKhoan') AND name = 'Ngay_tao')
BEGIN
    ALTER TABLE dbo.TaiKhoan ADD Ngay_tao DATETIME NULL;
    PRINT N'  + TaiKhoan.Ngay_tao added';
END

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.TaiKhoan') AND name = 'Ngay_cap_nhat')
BEGIN
    ALTER TABLE dbo.TaiKhoan ADD Ngay_cap_nhat DATETIME NULL;
    PRINT N'  + TaiKhoan.Ngay_cap_nhat added';
END
GO

-- ── 2. Ensure Khach_hang.Tai_khoan_id exists (may have been added earlier) ───
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Khach_hang') AND name = 'Tai_khoan_id')
BEGIN
    ALTER TABLE dbo.Khach_hang ADD Tai_khoan_id INT NULL;
    PRINT N'  + Khach_hang.Tai_khoan_id added';
END
GO

-- ── 3. Filtered unique index on TaiKhoan.Email ────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID('dbo.TaiKhoan') AND name = 'UQ_TaiKhoan_Email')
BEGIN
    CREATE UNIQUE INDEX UQ_TaiKhoan_Email ON dbo.TaiKhoan(Email) WHERE Email IS NOT NULL;
    PRINT N'  + UQ_TaiKhoan_Email index created';
END
GO

-- ── 4. Backfill Role_code, Email, Ho_ten for existing staff/admin accounts ────
UPDATE tk
SET
    tk.Role_code = CASE
        WHEN cv.Ten_chuc_vu LIKE N'%Quản lý%' OR cv.Ten_chuc_vu LIKE N'%Quan ly%' THEN 'ADMIN'
        ELSE 'STAFF'
    END,
    tk.Email     = ISNULL(tk.Email, nv.Email),
    tk.Ho_ten    = ISNULL(tk.Ho_ten, nv.Ho_ten)
FROM dbo.TaiKhoan tk
INNER JOIN dbo.NhanVien nv ON nv.Tai_khoan_id = tk.Tai_khoan_id
INNER JOIN dbo.ChucVu   cv ON cv.Chuc_vu_id   = nv.Chuc_vu_id
WHERE tk.Role_code IN ('STAFF', 'ADMIN') -- only update staff/admin rows
   OR tk.Role_code IS NULL
   OR tk.Role_code = '';
GO
PRINT N'  ✓ Staff/Admin Role_code backfilled';
GO

-- ── 5. Create TaiKhoan for KhachHang rows that have Mat_khau but no TaiKhoan ─
-- Using a cursor to handle INSERT + UPDATE in sequence safely
DECLARE @khId INT, @khEmail NVARCHAR(255), @khName NVARCHAR(255), @khPwd NVARCHAR(255), @khSdt NVARCHAR(50), @newAccId INT, @genUsername NVARCHAR(255);

DECLARE cur_kh CURSOR FOR
    SELECT Khach_hang_id, Email, Ten_khach_hang, Mat_khau, SDT
    FROM dbo.Khach_hang
    WHERE Tai_khoan_id IS NULL
      AND Mat_khau IS NOT NULL
      AND LEN(ISNULL(Mat_khau, '')) > 0;

OPEN cur_kh;
FETCH NEXT FROM cur_kh INTO @khId, @khEmail, @khName, @khPwd, @khSdt;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Only create if no TaiKhoan with this email already exists
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE Email = @khEmail)
    BEGIN
        -- Generate a safe username: kh_<id>
        SET @genUsername = 'kh_' + CAST(@khId AS NVARCHAR(20));
        -- Ensure username uniqueness
        IF EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE User_name = @genUsername)
            SET @genUsername = 'kh_' + CAST(@khId AS NVARCHAR(20)) + '_' + CAST(NEWID() AS NVARCHAR(10));

        INSERT INTO dbo.TaiKhoan (User_name, Pass_word, Trang_thai, Role_code, Email, Ho_ten, So_dien_thoai, Ngay_tao)
        VALUES (@genUsername, @khPwd, 1, 'USER', @khEmail, @khName, @khSdt, GETDATE());

        SET @newAccId = SCOPE_IDENTITY();

        -- Link back to KhachHang
        UPDATE dbo.Khach_hang SET Tai_khoan_id = @newAccId WHERE Khach_hang_id = @khId;
    END
    ELSE
    BEGIN
        -- TaiKhoan with this email already exists — just link it
        UPDATE dbo.Khach_hang
        SET Tai_khoan_id = (SELECT TOP 1 Tai_khoan_id FROM dbo.TaiKhoan WHERE Email = @khEmail)
        WHERE Khach_hang_id = @khId;
    END

    FETCH NEXT FROM cur_kh INTO @khId, @khEmail, @khName, @khPwd, @khSdt;
END

CLOSE cur_kh;
DEALLOCATE cur_kh;
GO
PRINT N'  ✓ KhachHang → TaiKhoan migration done';
GO

PRINT N'[12] ✓ TaiKhoan unification complete.';
GO
