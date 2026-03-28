# ============================================================
# install.ps1  -  Cai dat database sd50
#
# Yeu cau: sqlcmd phai duoc cai (bat ky phien ban nao)
# Khong can Python, khong dung flag -f 65001
# Tieng Viet hien thi dung nho chcp 65001 + UTF-16 LE temp file
#
# Cach dung:
#   cd SQL_Query
#   .\install.ps1
#
# Tuy chon:
#   .\install.ps1 -Server ".\SQLEXPRESS" -User "sa" -Password "matkhau"
#   .\install.ps1 -PatchOnly
# ============================================================
param(
    [string]$Server   = "127.0.0.1,1433",
    [string]$User     = "sa",
    [string]$Password = "123",
    [switch]$PatchOnly
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

# ------------------------------------------------------------------
# Bat UTF-8 cho console TRUOC khi sqlcmd ghi output
# sqlcmd xuat UTF-8 bytes; neu console dung CP437/1252 se bi garbled
# ------------------------------------------------------------------
$originalCP = [Console]::OutputEncoding
chcp 65001 | Out-Null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# Kiem tra sqlcmd co cai khong
if (-not (Get-Command sqlcmd -ErrorAction SilentlyContinue)) {
    Write-Host "[LOI] Khong tim thay sqlcmd trong PATH." -ForegroundColor Red
    Write-Host "      Cai tai: https://learn.microsoft.com/sql/tools/sqlcmd-utility" -ForegroundColor Yellow
    exit 1
}

# ------------------------------------------------------------------
# Ham chinh: doc UTF-8 -> ghi UTF-16 LE+BOM -> sqlcmd -i
# sqlcmd tu nhan dien BOM, xu ly GO noi bo, khong can -f
# KHONG dung 2>&1 de sqlcmd ghi thang ra console (tranh re-encode)
# ------------------------------------------------------------------
function Invoke-SqlFile {
    param([string]$FilePath)
    $fileName = Split-Path -Leaf $FilePath
    Write-Host ""
    Write-Host "  >>> $fileName" -ForegroundColor Cyan

    $content = [System.IO.File]::ReadAllText($FilePath, [System.Text.Encoding]::UTF8)
    $tmpFile  = [System.IO.Path]::Combine([System.IO.Path]::GetTempPath(), "sd50_$fileName")
    [System.IO.File]::WriteAllText($tmpFile, $content, [System.Text.Encoding]::Unicode)

    & sqlcmd -S $Server -U $User -P $Password -b -i $tmpFile
    $exitCode = $LASTEXITCODE
    Remove-Item $tmpFile -ErrorAction SilentlyContinue

    if ($exitCode -ne 0) {
        Write-Host "  [THAT BAI] $fileName (exit $exitCode)" -ForegroundColor Red
        [Console]::OutputEncoding = $originalCP
        exit $exitCode
    }
    Write-Host "  [OK] $fileName" -ForegroundColor Green
}

# ------------------------------------------------------------------
# Header
# ------------------------------------------------------------------
Write-Host ""
Write-Host "================================================" -ForegroundColor Yellow
Write-Host "  SD50 Database Installer" -ForegroundColor Yellow
Write-Host "  Server  : $Server" -ForegroundColor Yellow
Write-Host "  Database: sd50" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Yellow

# ------------------------------------------------------------------
# Patch only mode
# ------------------------------------------------------------------
if ($PatchOnly) {
    Write-Host "  Che do: Patch Only" -ForegroundColor Magenta
    $patchFile = Join-Path $ScriptDir "99_patch_missing_columns.sql"
    if (-not (Test-Path $patchFile)) {
        Write-Host "  [LOI] Khong tim thay 99_patch_missing_columns.sql" -ForegroundColor Red
        exit 1
    }
    Invoke-SqlFile $patchFile
    Write-Host "================================================" -ForegroundColor Green
    Write-Host "  Patch hoan thanh!" -ForegroundColor Green
    Write-Host "================================================" -ForegroundColor Green
    [Console]::OutputEncoding = $originalCP
    exit 0
}

# ------------------------------------------------------------------
# Drop database cu -> cai sach hoan toan (tranh loi FK / dirty data)
# ------------------------------------------------------------------
Write-Host ""
Write-Host "  [*] Xoa database sd50 cu (neu co)..." -ForegroundColor Magenta
& sqlcmd -S $Server -U $User -P $Password -b -Q `
    "IF EXISTS (SELECT 1 FROM sys.databases WHERE name=N'sd50') BEGIN ALTER DATABASE sd50 SET SINGLE_USER WITH ROLLBACK IMMEDIATE; DROP DATABASE sd50; PRINT N'Da xoa database sd50.'; END ELSE PRINT N'Khong tim thay database sd50, bo qua.';"
if ($LASTEXITCODE -ne 0) {
    Write-Host "  [THAT BAI] Khong the xoa database sd50. Kiem tra quyen sa." -ForegroundColor Red
    [Console]::OutputEncoding = $originalCP
    exit 1
}

# ------------------------------------------------------------------
# Danh sach file SQL (schema truoc, seed sau)
# ------------------------------------------------------------------
$files = @(
    "01_create_database.sql",
    "02_schema_core.sql",
    "03_schema_product.sql",
    "04_schema_customer.sql",
    "05_schema_payment.sql",
    "06_schema_invoice.sql",
    "07_schema_promotion.sql",
    "08_schema_warehouse.sql",
    "09_schema_cart.sql",
    "10_schema_misc.sql",
    "18_schema_ca_lam_viec.sql",
    "11_seed_core.sql",
    "12_seed_product.sql",
    "13_seed_customer.sql",
    "14_seed_invoice.sql",
    "15_seed_promotion.sql",
    "16_seed_warehouse.sql",
    "17_seed_misc.sql",
    "99_patch_missing_columns.sql"
)

$total = $files.Count; $current = 0
foreach ($file in $files) {
    $current++
    $path = Join-Path $ScriptDir $file
    if (-not (Test-Path $path)) {
        Write-Host "  [$current/$total] SKIP: $file" -ForegroundColor DarkGray
        continue
    }
    Write-Host "  [$current/$total]" -NoNewline
    Invoke-SqlFile $path
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "  HOAN THANH! Database sd50 san sang." -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
[Console]::OutputEncoding = $originalCP
