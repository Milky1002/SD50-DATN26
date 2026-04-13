package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Entity.ThanhToan;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Repository.ThanhToanRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BanHangReceiptController {

    private final InvoiceRepository invoiceRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final SpringTemplateEngine templateEngine;

    @GetMapping(value = "/ban-hang/{id}/phieu-tinh-tien", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> printReceiptPdf(@PathVariable Integer id) {
        HoaDon hoaDon = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay hoa don #" + id));
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(id);

        NumberFormat currency = NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Build model data (same logic as preview)
        String dateStr = hoaDon.getNgayTao() != null ? hoaDon.getNgayTao().format(dateFormatter) : "";
        String timeStr = hoaDon.getNgayTao() != null ? hoaDon.getNgayTao().format(timeFormatter) : "";
        String customerName = hoaDon.getTenKhachHang() != null ? hoaDon.getTenKhachHang() : "Khach le";
        BigDecimal totalAmount = hoaDon.getTongTienSauKhiGiam() != null ? hoaDon.getTongTienSauKhiGiam() : BigDecimal.ZERO;

        String paymentMethod = "TIEN MAT";
        ThanhToan thanhToan = thanhToanRepository.findByHoaDonId(id).orElse(null);
        if (thanhToan != null && thanhToan.getHinhThucThanhToanId() != null && thanhToan.getHinhThucThanhToanId() == 2) {
            paymentMethod = "CHUYEN KHOAN";
        }

        var itemList = new java.util.ArrayList<Map<String, Object>>();
        int totalQty = 0;
        for (int i = 0; i < items.size(); i++) {
            HoaDonChiTiet item = items.get(i);
            Map<String, Object> row = new HashMap<>();
            row.put("stt", i + 1);
            row.put("name", item.getSanPham() != null ? item.getSanPham().getTenSanPham() : "San pham");
            int qty = item.getSoLuongSanPham() != null ? item.getSoLuongSanPham() : 0;
            BigDecimal price = item.getGia() != null ? item.getGia() : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));
            row.put("quantity", qty);
            row.put("price", currency.format(price));
            row.put("lineTotal", currency.format(lineTotal));
            totalQty += qty;
            itemList.add(row);
        }

        // Build Thymeleaf context
        Context context = new Context();
        context.setVariable("invoiceId", hoaDon.getId());
        context.setVariable("date", dateStr);
        context.setVariable("time", timeStr);
        context.setVariable("customerName", customerName);
        context.setVariable("items", itemList);
        context.setVariable("totalQuantity", totalQty);
        context.setVariable("total", currency.format(totalAmount));
        context.setVariable("paymentMethod", paymentMethod);

        // Render HTML from Thymeleaf template
        String htmlContent = templateEngine.process("receipt/pdf", context);

        // Convert HTML to PDF using OpenHTMLToPDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            
            // Register font for Vietnamese character support
            File fontFile = new File("C:/Windows/Fonts/arial.ttf");
            if (fontFile.exists()) {
                builder.useFont(fontFile, "Arial");
            }
            
            // Use baseUri with proper format for OpenHTMLToPDF
            builder.withHtmlContent(htmlContent, "file:///");
            builder.toStream(os);
            builder.run();
            byte[] pdf = os.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("phieu-tinh-tien-" + id + ".pdf")
                    .build());

            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception ex) {
            ex.printStackTrace(); // Log full stack trace for debugging
            throw new IllegalStateException("Khong the tao PDF phieu tinh tien: " + ex.getMessage(), ex);
        }
    }

    @GetMapping(value = "/ban-hang/{id}/phieu-tinh-tien/preview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> previewReceipt(@PathVariable Integer id) {
        HoaDon hoaDon = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay hoa don #" + id));
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(id);

        NumberFormat currency = NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        Map<String, Object> result = new HashMap<>();
        result.put("invoiceId", hoaDon.getId());
        result.put("date", hoaDon.getNgayTao() != null ? hoaDon.getNgayTao().format(dateFormatter) : "");
        result.put("time", hoaDon.getNgayTao() != null ? hoaDon.getNgayTao().format(timeFormatter) : "");
        result.put("customerName", hoaDon.getTenKhachHang() != null ? hoaDon.getTenKhachHang() : "Khach le");
        result.put("customerPhone", hoaDon.getSdtKhachHang() != null ? hoaDon.getSdtKhachHang() : "");
        result.put("total", currency.format(hoaDon.getTongTienSauKhiGiam() != null ? hoaDon.getTongTienSauKhiGiam() : BigDecimal.ZERO));

        String paymentMethod = "Tien mat";
        ThanhToan thanhToan = thanhToanRepository.findByHoaDonId(id).orElse(null);
        if (thanhToan != null && thanhToan.getHinhThucThanhToanId() != null && thanhToan.getHinhThucThanhToanId() == 2) {
            paymentMethod = "Chuyen khoan";
        }
        result.put("paymentMethod", paymentMethod);

        var itemList = new java.util.ArrayList<Map<String, Object>>();
        int totalQty = 0;
        for (int i = 0; i < items.size(); i++) {
            HoaDonChiTiet item = items.get(i);
            Map<String, Object> row = new HashMap<>();
            row.put("stt", i + 1);
            row.put("name", item.getSanPham() != null ? item.getSanPham().getTenSanPham() : "San pham");
            int qty = item.getSoLuongSanPham() != null ? item.getSoLuongSanPham() : 0;
            BigDecimal price = item.getGia() != null ? item.getGia() : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));
            row.put("quantity", qty);
            row.put("price", currency.format(price));
            row.put("lineTotal", currency.format(lineTotal));
            totalQty += qty;
            itemList.add(row);
        }
        result.put("items", itemList);
        result.put("totalQuantity", totalQty);

        return ResponseEntity.ok(result);
    }
}
