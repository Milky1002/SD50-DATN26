# ============================================================
# install.ps1  -  Cai dat database sd50 (UTF-8 safe, khong can -f)
#
# Chay tu thu muc SQL_Query:
#   cd SQL_Query
#   .\install.ps1
#
# Tuy chon tham so:
#   .\install.ps1 -Server ".\SQLEXPRESS" -User "sa" -Password "123"
#   .\install.ps1 -PatchOnly   # Chi chay 99_patch_missing_columns.sql
#
# Ghi chu: Script nay KHONG dung -f 65001 nen chay duoc voi moi phien ban sqlcmd.
#          Thay vao do, script doc file UTF-8 bang PowerShell roi ghi tam sang
#          UTF-16 LE (Unicode) truoc khi truyen vao sqlcmd.
# ============================================================
param(
    [string]$Server   = "127.0.0.1,1433",
    [string]$User     = "sa",
    [string]$Password = "123",
    [switch]$PatchOnly
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Chay 1 file SQL: doc UTF-8 -> ghi UTF-16 LE -> sqlcmd -i (khong can -f)
function Invoke-SqlFile {
    param([string]$FilePath)
    $fileName = Split-Path -Leaf $FilePath
    Write-Host ""
    Write-Host ">>> $fileName" -ForegroundColor Cyan

    # Doc noi dung file voi encoding UTF-8
    $content = [System.IO.File]::ReadAllText($FilePath, [System.Text.Encoding]::UTF8)

    # Tach batch theo GO (moi dong rieng)
    $batches = $content -split '(?im)^\s*GO\s*$'

    $batchIndex = 0
    foreach ($batch in $batches) {
        $trimmed = $batch.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed)) { continue }
        $batchIndex++

        # Ghi batch vao file tam voi encoding UTF-16 LE (sqlcmd doc duoc khong can -f)
        $tmpFile = [System.IO.Path]::Combine([System.IO.Path]::GetTempPath(), "sd50_batch_$batchIndex.sql")
        [System.IO.File]::WriteAllText($tmpFile, $trimmed, [System.Text.Encoding]::Unicode)

        $output = & sqlcmd -S $Server -U $User -P $Password -b -i $tmpFile 2>&1
        $exitCode = $LASTEXITCODE

        Remove-Item $tmpFile -ErrorAction SilentlyContinue

        foreach ($line in $output) {
            $lineStr = "$line"
            if ($lineStr -match "^Msg \d|error|Error") {
                Write-Host "  $lineStr" -ForegroundColor Red
            } elseif ($lineStr -match "rows affected|affected") {
                Write-Host "  $lineStr" -ForegroundColor DarkGray
            } else {
                Write-Host "  $lineStr"
            }
        }

        if ($exitCode -ne 0) {
            Write-Host "  THAT BAI o batch $batchIndex trong $fileName (exit $exitCode)" -ForegroundColor Red
            exit $exitCode
        }
    }

    Write-Host "  OK: $fileName" -ForegroundColor Green
}

Write-Host "================================================" -ForegroundColor Yellow
Write-Host " SD50 Database Installer" -ForegroundColor Yellow
Write-Host " Server  : $Server" -ForegroundColor Yellow
Write-Host " Database: sd50" -ForegroundColor Yellow
Write-Host " Encoding: UTF-16 LE temp files (khong dung -f)" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Yellow

if ($PatchOnly) {
    Write-Host "`nChay patch only mode..." -ForegroundColor Magenta
    $patchFile = Join-Path $ScriptDir "99_patch_missing_columns.sql"
    Invoke-SqlFile $patchFile
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

foreach ($file in $files) {
    $path = Join-Path $ScriptDir $file
    if (-not (Test-Path $path)) {
        Write-Host "SKIP (khong tim thay): $file" -ForegroundColor DarkGray
        continue
    }
    Invoke-SqlFile $path
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host " Cai dat hoan thanh! Database sd50 san sang." -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
