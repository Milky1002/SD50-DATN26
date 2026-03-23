package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class BanHangReceiptController {

    private final InvoiceRepository invoiceRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    @GetMapping(value = "/ban-hang/{id}/phieu-tinh-tien", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> printReceiptPdf(@PathVariable Integer id) {
        HoaDon hoaDon = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn #" + id));
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(id);

        String html = buildReceiptHtml(hoaDon, items);
        byte[] pdf = renderPdf(html);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("phieu-tinh-tien-" + id + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    private byte[] renderPdf(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String safeText = sanitizePdfText(html);
            String escaped = safeText
                    .replace("\\", "\\\\")
                    .replace("(", "\\(")
                    .replace(")", "\\)");

            String pdf = "%PDF-1.4\n"
                    + "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n"
                    + "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n"
                    + "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >> endobj\n"
                    + "4 0 obj << /Length 0 >> stream\n"
                    + "BT\n/F1 10 Tf\n40 800 Td\n14 TL\n(" + escaped.replace("\n", ") Tj T* (") + ") Tj\nET\n"
                    + "endstream endobj\n"
                    + "5 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Courier >> endobj\n"
                    + "xref\n0 6\n0000000000 65535 f \n"
                    + "trailer << /Root 1 0 R /Size 6 >>\nstartxref\n0\n%%EOF";

            outputStream.write(pdf.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể tạo PDF phiếu tính tiền", ex);
        }
    }

    private String buildReceiptHtml(HoaDon hoaDon, List<HoaDonChiTiet> items) {
        NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder plainText = new StringBuilder();
        plainText.append("PHIEU TINH TIEN\n");
        plainText.append("Ma hoa don: #HD-").append(hoaDon.getId()).append("\n");
        plainText.append("Ngay tao: ").append(hoaDon.getNgayTao() != null ? formatter.format(hoaDon.getNgayTao()) : "").append("\n");
        plainText.append("Khach hang: ").append(hoaDon.getTenKhachHang() != null ? hoaDon.getTenKhachHang() : "Khach le").append("\n");
        plainText.append("So dien thoai: ").append(hoaDon.getSdtKhachHang() != null ? hoaDon.getSdtKhachHang() : "").append("\n");
        plainText.append("Tong thanh toan: ").append(currency.format(hoaDon.getTongTienSauKhiGiam() != null ? hoaDon.getTongTienSauKhiGiam() : BigDecimal.ZERO)).append("d\n\n");
        plainText.append("San pham | SL | Don gia | Thanh tien\n");
        plainText.append("------------------------------------------------------------\n");

        for (HoaDonChiTiet item : items) {
            BigDecimal gia = item.getGia() != null ? item.getGia() : BigDecimal.ZERO;
            int soLuong = item.getSoLuongSanPham() != null ? item.getSoLuongSanPham() : 0;
            BigDecimal thanhTien = gia.multiply(BigDecimal.valueOf(soLuong));
            plainText.append((item.getSanPham() != null ? item.getSanPham().getTenSanPham() : "San pham"))
                    .append(" | ")
                    .append(soLuong)
                    .append(" | ")
                    .append(currency.format(gia)).append("d")
                    .append(" | ")
                    .append(currency.format(thanhTien)).append("d\n");
        }

        plainText.append("\nCam on quy khach da mua hang.");
        return plainText.toString();
    }

    private String sanitizePdfText(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace('Đ', 'D')
                .replace('đ', 'd')
                .replace('Á', 'A').replace('À', 'A').replace('Ả', 'A').replace('Ã', 'A').replace('Ạ', 'A')
                .replace('Ă', 'A').replace('Ắ', 'A').replace('Ằ', 'A').replace('Ẳ', 'A').replace('Ẵ', 'A').replace('Ặ', 'A')
                .replace('Â', 'A').replace('Ấ', 'A').replace('Ầ', 'A').replace('Ẩ', 'A').replace('Ẫ', 'A').replace('Ậ', 'A')
                .replace('á', 'a').replace('à', 'a').replace('ả', 'a').replace('ã', 'a').replace('ạ', 'a')
                .replace('ă', 'a').replace('ắ', 'a').replace('ằ', 'a').replace('ẳ', 'a').replace('ẵ', 'a').replace('ặ', 'a')
                .replace('â', 'a').replace('ấ', 'a').replace('ầ', 'a').replace('ẩ', 'a').replace('ẫ', 'a').replace('ậ', 'a')
                .replace('É', 'E').replace('È', 'E').replace('Ẻ', 'E').replace('Ẽ', 'E').replace('Ẹ', 'E')
                .replace('Ê', 'E').replace('Ế', 'E').replace('Ề', 'E').replace('Ể', 'E').replace('Ễ', 'E').replace('Ệ', 'E')
                .replace('é', 'e').replace('è', 'e').replace('ẻ', 'e').replace('ẽ', 'e').replace('ẹ', 'e')
                .replace('ê', 'e').replace('ế', 'e').replace('ề', 'e').replace('ể', 'e').replace('ễ', 'e').replace('ệ', 'e')
                .replace('Í', 'I').replace('Ì', 'I').replace('Ỉ', 'I').replace('Ĩ', 'I').replace('Ị', 'I')
                .replace('í', 'i').replace('ì', 'i').replace('ỉ', 'i').replace('ĩ', 'i').replace('ị', 'i')
                .replace('Ó', 'O').replace('Ò', 'O').replace('Ỏ', 'O').replace('Õ', 'O').replace('Ọ', 'O')
                .replace('Ô', 'O').replace('Ố', 'O').replace('Ồ', 'O').replace('Ổ', 'O').replace('Ỗ', 'O').replace('Ộ', 'O')
                .replace('Ơ', 'O').replace('Ớ', 'O').replace('Ờ', 'O').replace('Ở', 'O').replace('Ỡ', 'O').replace('Ợ', 'O')
                .replace('ó', 'o').replace('ò', 'o').replace('ỏ', 'o').replace('õ', 'o').replace('ọ', 'o')
                .replace('ô', 'o').replace('ố', 'o').replace('ồ', 'o').replace('ổ', 'o').replace('ỗ', 'o').replace('ộ', 'o')
                .replace('ơ', 'o').replace('ớ', 'o').replace('ờ', 'o').replace('ở', 'o').replace('ỡ', 'o').replace('ợ', 'o')
                .replace('Ú', 'U').replace('Ù', 'U').replace('Ủ', 'U').replace('Ũ', 'U').replace('Ụ', 'U')
                .replace('Ư', 'U').replace('Ứ', 'U').replace('Ừ', 'U').replace('Ử', 'U').replace('Ữ', 'U').replace('Ự', 'U')
                .replace('ú', 'u').replace('ù', 'u').replace('ủ', 'u').replace('ũ', 'u').replace('ụ', 'u')
                .replace('ư', 'u').replace('ứ', 'u').replace('ừ', 'u').replace('ử', 'u').replace('ữ', 'u').replace('ự', 'u')
                .replace('Ý', 'Y').replace('Ỳ', 'Y').replace('Ỷ', 'Y').replace('Ỹ', 'Y').replace('Ỵ', 'Y')
                .replace('ý', 'y').replace('ỳ', 'y').replace('ỷ', 'y').replace('ỹ', 'y').replace('ỵ', 'y');
    }
}
