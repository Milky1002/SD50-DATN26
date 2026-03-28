# ============================================================
# install.ps1  -  Cai dat database sd50
#
# Yeu cau: sqlcmd phai duoc cai (bat ky phien ban nao)
# Khong can Python, khong dung flag -f 65001
# Tieng Viet duoc bao toan qua UTF-16 LE + BOM
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

# Kiem tra sqlcmd co cai khong
if (-not (Get-Command sqlcmd -ErrorAction SilentlyContinue)) {
    Write-Host "[LOI] Khong tim thay sqlcmd trong PATH." -ForegroundColor Red
    Write-Host "      Cai sqlcmd tai: https://learn.microsoft.com/sql/tools/sqlcmd-utility" -ForegroundColor Yellow
    exit 1
}

# -------------------------------------------------------
# Ham chinh: doc UTF-8 -> ghi UTF-16 LE (co BOM) -> sqlcmd -i
# sqlcmd tu nhan dien BOM, xu ly GO noi bo, khong can -f
# -------------------------------------------------------
function Invoke-SqlFile {
    param([string]$FilePath)
    $fileName = Split-Path -Leaf $FilePath

    Write-Host ""
    Write-Host "  >>> $fileName" -ForegroundColor Cyan

    # B1: Doc file bang UTF-8 trong PowerShell (xu ly ky tu Unicode dung)
    $content = [System.IO.File]::ReadAllText($FilePath, [System.Text.Encoding]::UTF8)

    # B2: Ghi toan bo noi dung sang file tam voi encoding UTF-16 LE + BOM
    #     (sqlcmd nhan dien BOM FF FE tu dong, khong can flag -f)
    $tmpFile = [System.IO.Path]::Combine(
        [System.IO.Path]::GetTempPath(),
        "sd50_tmp_" + [System.IO.Path]::GetFileName($FilePath)
    )
    [System.IO.File]::WriteAllText($tmpFile, $content, [System.Text.Encoding]::Unicode)

    # B3: Chay sqlcmd voi file tam (GO trong file se duoc xu ly tu dong)
    $output = & sqlcmd -S $Server -U $User -P $Password -b -i $tmpFile 2>&1
    $exitCode = $LASTEXITCODE

    # Don dep file tam
    Remove-Item $tmpFile -ErrorAction SilentlyContinue

    # In ket qua
    foreach ($line in $output) {
        $str = "$line".Trim()
        if ($str -eq "") { continue }
        if ($str -match "^Msg \d|Login failed|Cannot open|Invalid object|error") {
            Write-Host "      [LOI] $str" -ForegroundColor Red
        } elseif ($str -match "rows affected") {
            Write-Host "      $str" -ForegroundColor DarkGray
        } else {
            Write-Host "      $str"
        }
    }

    if ($exitCode -ne 0) {
        Write-Host "  [THAT BAI] $fileName (exit $exitCode)" -ForegroundColor Red
        exit $exitCode
    }

    Write-Host "  [OK] $fileName" -ForegroundColor Green
}

# -------------------------------------------------------
# Header
# -------------------------------------------------------
Write-Host ""
Write-Host "================================================" -ForegroundColor Yellow
Write-Host "  SD50 Database Installer" -ForegroundColor Yellow
Write-Host "  Server  : $Server" -ForegroundColor Yellow
Write-Host "  Database: sd50" -ForegroundColor Yellow
Write-Host "  Encoding: UTF-16 LE temp (khong dung -f, khong can Python)" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Yellow

# -------------------------------------------------------
# Patch only mode
# -------------------------------------------------------
if ($PatchOnly) {
    Write-Host ""
    Write-Host "  Che do: Patch Only" -ForegroundColor Magenta
    $patchFile = Join-Path $ScriptDir "99_patch_missing_columns.sql"
    if (-not (Test-Path $patchFile)) {
        Write-Host "  [LOI] Khong tim thay 99_patch_missing_columns.sql" -ForegroundColor Red
        exit 1
    }
    Invoke-SqlFile $patchFile
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Green
    Write-Host "  Patch hoan thanh!" -ForegroundColor Green
    Write-Host "================================================" -ForegroundColor Green
    exit 0
}

# -------------------------------------------------------
# Danh sach file SQL theo thu tu (schema truoc, seed sau)
# -------------------------------------------------------
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

Write-Host ""
$total   = $files.Count
$current = 0

foreach ($file in $files) {
    $current++
    $path = Join-Path $ScriptDir $file
    if (-not (Test-Path $path)) {
        Write-Host "  [$current/$total] SKIP: $file (khong tim thay)" -ForegroundColor DarkGray
        continue
    }
    Write-Host "  [$current/$total]" -NoNewline
    Invoke-SqlFile $path
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "  HOAN THANH! Database sd50 san sang." -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
