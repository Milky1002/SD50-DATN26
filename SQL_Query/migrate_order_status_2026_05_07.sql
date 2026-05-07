-- Migration: Add DA_XAC_NHAN status (2026-05-07)
-- Run BEFORE deploying new application code.
--
-- Old status mapping:  0=Chờ xác nhận, 1=Đang giao, 2=Hoàn tất, 3=Đã hủy
-- New status mapping:  0=Chờ xác nhận, 1=Đã xác nhận, 2=Đang giao, 3=Hoàn tất, 4=Đã hủy
--
-- Shift all existing non-zero statuses up by 1 to make room for DA_XAC_NHAN = 1.
-- Process in descending order to avoid collision (3→4, 2→3, 1→2).

USE SD50_DATN26;  -- adjust database name if needed

UPDATE HoaDon SET Trang_thai = 4 WHERE Trang_thai = 3;  -- Đã hủy:   3 → 4
UPDATE HoaDon SET Trang_thai = 3 WHERE Trang_thai = 2;  -- Hoàn tất: 2 → 3
UPDATE HoaDon SET Trang_thai = 2 WHERE Trang_thai = 1;  -- Đang giao: 1 → 2
-- Trang_thai = 0 (Chờ xác nhận) stays unchanged.
-- No existing rows have Trang_thai = 1 (DA_XAC_NHAN) after migration — correct.
