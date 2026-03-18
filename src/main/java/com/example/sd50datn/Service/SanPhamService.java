package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.DanhMucSanPhamRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepo;
    private final DanhMucSanPhamRepository danhMucRepo;

    public List<SanPham> search(String keyword, Integer trangThai, Integer danhMucId) {
        String q = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return sanPhamRepo.searchProducts(q, trangThai, danhMucId);
    }

    public SanPham getById(Integer id) {
        return sanPhamRepo.findById(id).orElse(null);
    }

    public SanPham findByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) return null;
        return sanPhamRepo.findByBarcode(barcode.trim()).orElse(null);
    }

    public boolean existsByMaSanPham(String ma) {
        return sanPhamRepo.existsByMaSanPham(ma);
    }

    public boolean existsByMaSanPhamExcluding(String ma, Integer excludeId) {
        return sanPhamRepo.existsByMaSanPhamAndIdNot(ma, excludeId);
    }

    public boolean existsByBarcode(String barcode) {
        return sanPhamRepo.existsByBarcode(barcode);
    }

    public boolean existsByBarcodeExcluding(String barcode, Integer excludeId) {
        return sanPhamRepo.existsByBarcodeAndIdNot(barcode, excludeId);
    }

    public SanPham save(SanPham sp) {
        return sanPhamRepo.save(sp);
    }

    public void delete(Integer id) {
        sanPhamRepo.deleteById(id);
    }

    public String generateNextMaSanPham() {
        String prefix = "HH";
        List<SanPham> all = sanPhamRepo.findAll();
        int maxNum = 0;
        for (SanPham sp : all) {
            String ma = sp.getMaSanPham();
            if (ma != null && ma.toUpperCase().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(ma.substring(prefix.length()));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return prefix + String.format("%02d", maxNum + 1);
    }

    public String generateSku(String maSanPham) {
        if (maSanPham == null || maSanPham.isBlank()) {
            maSanPham = generateNextMaSanPham();
        }
        return "SKU-" + maSanPham;
    }

    public String generateBarcode() {
        String barcode;
        do {
            long base = ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L);
            String partial = String.valueOf(base);
            int checkDigit = calculateEan13Check(partial);
            barcode = partial + checkDigit;
        } while (sanPhamRepo.existsByBarcode(barcode));
        return barcode;
    }

    private int calculateEan13Check(String digits12) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = digits12.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        int remainder = sum % 10;
        return (remainder == 0) ? 0 : (10 - remainder);
    }

    public String validateForSave(SanPham sp) {
        if (sp.getTenSanPham() == null || sp.getTenSanPham().isBlank()) {
            return "Tên hàng hóa không được để trống";
        }
        if (sp.getMaSanPham() == null || sp.getMaSanPham().isBlank()) {
            sp.setMaSanPham(generateNextMaSanPham());
        }
        if (sp.getSku() == null || sp.getSku().isBlank()) {
            sp.setSku(generateSku(sp.getMaSanPham()));
        }
        if (sp.getId() == null) {
            if (sanPhamRepo.existsByMaSanPham(sp.getMaSanPham())) {
                return "Mã hàng hóa đã tồn tại";
            }
            if (sanPhamRepo.existsBySku(sp.getSku())) {
                return "SKU đã tồn tại";
            }
            if (sp.getBarcode() != null && !sp.getBarcode().isBlank()
                    && sanPhamRepo.existsByBarcode(sp.getBarcode())) {
                return "Mã vạch đã tồn tại";
            }
        } else {
            if (sanPhamRepo.existsByMaSanPhamAndIdNot(sp.getMaSanPham(), sp.getId())) {
                return "Mã hàng hóa đã tồn tại";
            }
            if (sanPhamRepo.existsBySkuAndIdNot(sp.getSku(), sp.getId())) {
                return "SKU đã tồn tại";
            }
            if (sp.getBarcode() != null && !sp.getBarcode().isBlank()
                    && sanPhamRepo.existsByBarcodeAndIdNot(sp.getBarcode(), sp.getId())) {
                return "Mã vạch đã tồn tại";
            }
        }
        return null;
    }

    public ByteArrayInputStream exportToExcel(List<SanPham> list) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh sách hàng hóa");

            String[] headers = {"Mã hàng hóa", "Tên hàng hóa", "Nhóm hàng hóa", "Màu sắc", "SKU", "Mã vạch",
                    "Đơn giá vốn", "Giá bán", "Tồn kho", "Đơn vị tính", "Trạng thái"};
            Row headerRow = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (SanPham sp : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(sp.getMaSanPham());
                row.createCell(1).setCellValue(sp.getTenSanPham());
                row.createCell(2).setCellValue(
                        sp.getDanhMucSanPham() != null ? sp.getDanhMucSanPham().getTenDanhMuc() : "");
                row.createCell(3).setCellValue(
                        sp.getMauSac() != null ? sp.getMauSac().getTenMau() : "");
                row.createCell(4).setCellValue(sp.getSku());
                row.createCell(5).setCellValue(sp.getBarcode() != null ? sp.getBarcode() : "");
                row.createCell(6).setCellValue(
                        sp.getGiaNhap() != null ? sp.getGiaNhap().doubleValue() : 0);
                row.createCell(7).setCellValue(
                        sp.getGiaBan() != null ? sp.getGiaBan().doubleValue() : 0);
                row.createCell(8).setCellValue(
                        sp.getSoLuongTon() != null ? sp.getSoLuongTon() : 0);
                row.createCell(9).setCellValue(sp.getDonViTinh() != null ? sp.getDonViTinh() : "");
                row.createCell(10).setCellValue(sp.getTrangThai() == 1 ? "Đang kinh doanh" : "Ngừng kinh doanh");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }
}
