# ============================================================
# install.ps1  –  Cài đặt database sd50 (UTF-8 safe)
#
# Chạy từ thư mục SQL_Query:
#   cd SQL_Query
#   .\install.ps1
#
# Tuỳ chọn tham số:
#   .\install.ps1 -Server ".\SQLEXPRESS" -User "sa" -Password "123"
#   .\install.ps1 -PatchOnly   # Chỉ chạy 99_patch_missing_columns.sql
# ============================================================
param(
    [string]$Server   = "127.0.0.1,1433",
    [string]$User     = "sa",
    [string]$Password = "123",
    [switch]$PatchOnly
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

function Invoke-SqlFile {
    param([string]$FilePath)
    $fileName = Split-Path -Leaf $FilePath
    Write-Host ""
    Write-Host ">>> $fileName" -ForegroundColor Cyan

    # Đọc nội dung file bằng UTF-8, bỏ BOM nếu có
    $content = [System.IO.File]::ReadAllText($FilePath, [System.Text.Encoding]::UTF8)

    # Tách thành các batch theo GO
    $batches = $content -split '(?im)^\s*GO\s*$'

    foreach ($batch in $batches) {
        $trimmed = $batch.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed)) { continue }

        # Chạy qua sqlcmd với encoding Unicode để tránh lỗi font
        $tmpFile = [System.IO.Path]::GetTempFileName() + ".sql"
        [System.IO.File]::WriteAllText($tmpFile, $trimmed, [System.Text.Encoding]::Unicode)

        & sqlcmd -S $Server -U $User -P $Password `
                 -f 65001 `
                 -b `
                 -Q $trimmed 2>&1 | ForEach-Object {
            if ($_ -match "^Msg \d") {
                Write-Host "  $_" -ForegroundColor Red
            } elseif ($_ -match "^\[OK\]|đã được tạo|đã tồn tại|rows affected") {
                Write-Host "  $_" -ForegroundColor Green
            } else {
                Write-Host "  $_"
            }
        }

        Remove-Item $tmpFile -ErrorAction SilentlyContinue

        if ($LASTEXITCODE -ne 0) {
            Write-Host "FAILED on batch in $fileName (exit $LASTEXITCODE)" -ForegroundColor Red
            exit $LASTEXITCODE
        }
    }
}

function Invoke-SqlFileRaw {
    param([string]$FilePath)
    $fileName = Split-Path -Leaf $FilePath
    Write-Host ""
    Write-Host ">>> $fileName" -ForegroundColor Cyan

    & sqlcmd -S $Server -U $User -P $Password `
             -f 65001 `
             -b `
             -i $FilePath 2>&1 | ForEach-Object {
        if ($_ -match "Msg \d") {
            Write-Host "  $_" -ForegroundColor Red
        } else {
            Write-Host "  $_"
        }
    }

    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED: $fileName (exit $LASTEXITCODE)" -ForegroundColor Red
        exit $LASTEXITCODE
    }
}

Write-Host "================================================" -ForegroundColor Yellow
Write-Host " SD50 Database Installer" -ForegroundColor Yellow
Write-Host " Server  : $Server" -ForegroundColor Yellow
Write-Host " Database: sd50" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Yellow

if ($PatchOnly) {
    Write-Host "`nChay patch only mode..." -ForegroundColor Magenta
    $patchFile = Join-Path $ScriptDir "99_patch_missing_columns.sql"
    Invoke-SqlFileRaw $patchFile
    Write-Host "`nPatch xong!" -ForegroundColor Green
    exit 0
}

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
    "11_seed_core.sql",
    "12_seed_product.sql",
    "13_seed_customer.sql",
    "14_seed_invoice.sql",
    "15_seed_promotion.sql",
    "16_seed_warehouse.sql",
    "17_seed_misc.sql",
    "99_patch_missing_columns.sql"
)

foreach ($file in $files) {
    $path = Join-Path $ScriptDir $file
    if (-not (Test-Path $path)) {
        Write-Host "SKIP (khong tim thay): $file" -ForegroundColor DarkGray
        continue
    }
    Invoke-SqlFileRaw $path
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host " Cai dat hoan thanh! Database sd50 san sang." -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
