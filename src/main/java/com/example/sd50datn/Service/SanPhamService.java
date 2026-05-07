package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.Anh;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.AnhRepository;
import com.example.sd50datn.Repository.DanhMucSanPhamRepository;
import com.example.sd50datn.Repository.MauSacRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepo;
    private final DanhMucSanPhamRepository danhMucRepo;
    private final MauSacRepository mauSacRepo;
    private final AnhRepository anhRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public List<SanPham> search(String keyword, Integer trangThai, Integer danhMucId) {
        String q = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return sanPhamRepo.searchProducts(q, trangThai, danhMucId);
    }

    public SanPham getById(Integer id) {
        return sanPhamRepo.findById(id).orElse(null);
    }

    public SanPham getByIdWithRelations(Integer id) {
        return sanPhamRepo.findByIdWithRelations(id).orElse(null);
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

    @Transactional
    public SanPham saveWithImage(SanPham sp, MultipartFile imageFile, String imageUrl) {
        Anh currentImage = null;
        if (sp.getId() != null) {
            SanPham existing = getById(sp.getId());
            if (existing != null) {
                currentImage = existing.getAnh();
            }
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            Anh anh = storeUploadedImage(imageFile, currentImage);
            sp.setAnh(anh);
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            Anh anh = currentImage != null ? currentImage : new Anh();
            anh.setAnhUrl(imageUrl.trim());
            anh.setTenFileGoc(null);
            anh.setLoaiNguon("url");
            anh.setKichThuocByte(null);
            anh.setMimeType(null);
            if (anh.getTrangThai() == null) {
                anh.setTrangThai(1);
            }
            if (anh.getThuTu() == null) {
                anh.setThuTu(0);
            }
            sp.setAnh(anhRepository.save(anh));
        } else if (currentImage != null) {
            sp.setAnh(currentImage);
        }

        // Resolve transient MauSac reference from form binding
        if (sp.getMauSac() != null && sp.getMauSac().getMauSacId() != null) {
            sp.setMauSac(mauSacRepo.findById(sp.getMauSac().getMauSacId()).orElse(null));
        } else {
            sp.setMauSac(null);
        }

        // Resolve transient DanhMucSanPham reference from form binding
        if (sp.getDanhMucSanPham() != null && sp.getDanhMucSanPham().getDanhMucSanPhamId() != null) {
            sp.setDanhMucSanPham(danhMucRepo.findById(sp.getDanhMucSanPham().getDanhMucSanPhamId()).orElse(null));
        } else {
            sp.setDanhMucSanPham(null);
        }

        return sanPhamRepo.save(sp);
    }

    public List<SanPham> getLatestProductsByCategory(Integer danhMucId, int limit) {
        return sanPhamRepo.findLatestActiveByCategory(danhMucId)
                .stream()
                .limit(Math.max(limit, 0))
                .toList();
    }

    private Anh storeUploadedImage(MultipartFile imageFile, Anh currentImage) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String fileName = UUID.randomUUID() + extension;
            Path uploadRoot = Paths.get(uploadDir, "products");
            Files.createDirectories(uploadRoot);
            Path destination = uploadRoot.resolve(fileName);
            Files.copy(imageFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            Anh anh = currentImage != null ? currentImage : new Anh();
            anh.setAnhUrl("/uploads/products/" + fileName);
            anh.setTenFileGoc(originalFilename);
            anh.setLoaiNguon("upload");
            anh.setKichThuocByte(imageFile.getSize());
            anh.setMimeType(imageFile.getContentType());
            if (anh.getTrangThai() == null) {
                anh.setTrangThai(1);
            }
            if (anh.getThuTu() == null) {
                anh.setThuTu(0);
            }
            return anhRepository.save(anh);
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu ảnh sản phẩm: " + e.getMessage(), e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
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
            // New product
            if (sanPhamRepo.existsByTenSanPhamIgnoreCase(sp.getTenSanPham().trim())) {
                return "Tên hàng hóa đã tồn tại (trùng tên)";
            }
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
            // Editing existing product
            if (sanPhamRepo.existsByTenSanPhamIgnoreCaseAndIdNot(sp.getTenSanPham().trim(), sp.getId())) {
                return "Tên hàng hóa đã tồn tại (trùng tên)";
            }
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
