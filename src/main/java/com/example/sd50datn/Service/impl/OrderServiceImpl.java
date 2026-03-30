package com.example.sd50datn.Service.impl;

import com.example.sd50datn.Dto.OrderSummaryDTO;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.OrderRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import com.example.sd50datn.Repository.ThanhToanRepository;
import com.example.sd50datn.Service.OrderService;
import com.example.sd50datn.Util.OrderStatusUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final float PAGE_MARGIN = 40f;
    private static final float FONT_SIZE = 10f;
    private static final float LEADING = 15f;
    private static final String DEFAULT_FONT_PATH = "C:/Windows/Fonts/arial.ttf";

    private final OrderRepository orderRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ThanhToanRepository thanhToanRepository;

    @Override
    public List<OrderSummaryDTO> getOrderSummaries() {
        log.info("Bat dau lay danh sach don hang");
        try {
            List<OrderSummaryDTO> result = orderRepository.fetchOrderSummaries();
            log.info("Lay thanh cong, kich thuoc: {}", result != null ? result.size() : 0);
            return result;
        } catch (Exception ex) {
            log.error("Loi khi lay danh sach don hang: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteOrder(Integer id) {
        if (id == null) {
            return;
        }
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Integer id, Integer status) {
        if (id == null || status == null) {
            return;
        }
        orderRepository.findById(id).ifPresent(order -> {
            Integer previousStatus = order.getTrangThai();
            if (!OrderStatusUtil.isValidTransition(previousStatus, status)) {
                throw new IllegalStateException(
                        "Khong the chuyen trang thai tu '" + OrderStatusUtil.getLabel(previousStatus)
                                + "' sang '" + OrderStatusUtil.getLabel(status) + "'");
            }

            if (status == OrderStatusUtil.DA_HUY) {
                restoreStock(order.getId());
            }

            // Khi đơn hàng hoàn tất → tự động cập nhật thanh toán thành "Đã thanh toán"
            if (status == OrderStatusUtil.HOAN_TAT) {
                thanhToanRepository.findByHoaDonId(order.getId()).ifPresent(thanhToan -> {
                    thanhToan.setTrangThai(1); // 1 = Đã thanh toán
                    thanhToanRepository.save(thanhToan);
                });
            }

            order.setTrangThai(status);
            orderRepository.save(order);
        });
    }

    @Override
    public ByteArrayInputStream exportOrdersPdf() {
        List<OrderSummaryDTO> orders = getOrderSummaries();
        return new ByteArrayInputStream(renderOrdersPdf(orders));
    }

    private void restoreStock(Integer orderId) {
        List<HoaDonChiTiet> orderItems = hoaDonChiTietRepository.findByHoaDonId(orderId);
        for (HoaDonChiTiet item : orderItems) {
            SanPham sanPham = item.getSanPham();
            if (sanPham == null || item.getSoLuongSanPham() == null) {
                continue;
            }
            Integer currentStock = sanPham.getSoLuongTon() != null ? sanPham.getSoLuongTon() : 0;
            sanPham.setSoLuongTon(currentStock + item.getSoLuongSanPham());
            sanPhamRepository.save(sanPham);
        }
    }

    private byte[] renderOrdersPdf(List<OrderSummaryDTO> orders) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDFont font = PDType0Font.load(document, Files.newInputStream(resolveFontPath()));
            NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            float y = page.getMediaBox().getHeight() - PAGE_MARGIN;
            float width = page.getMediaBox().getWidth() - PAGE_MARGIN * 2;

            y = writeLine(contentStream, font, 14f, PAGE_MARGIN, y, "DANH SACH DON HANG");
            y = writeLine(contentStream, font, FONT_SIZE, PAGE_MARGIN, y, "Tong so don: " + orders.size());
            y -= 6f;
            y = writeLine(contentStream, font, FONT_SIZE, PAGE_MARGIN, y, repeat("-", 95));

            for (OrderSummaryDTO order : orders) {
                List<String> block = buildOrderLines(order, currency, formatter);
                for (String line : block) {
                    for (String wrapped : wrapLine(line, font, FONT_SIZE, width)) {
                        if (y <= PAGE_MARGIN + LEADING) {
                            contentStream.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            y = page.getMediaBox().getHeight() - PAGE_MARGIN;
                        }
                        y = writeLine(contentStream, font, FONT_SIZE, PAGE_MARGIN, y, wrapped);
                    }
                }
                y -= 4f;
            }

            contentStream.close();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Khong the tao PDF danh sach don hang", ex);
        }
    }

    private List<String> buildOrderLines(OrderSummaryDTO order,
                                         NumberFormat currency,
                                         DateTimeFormatter formatter) {
        List<String> lines = new ArrayList<>();
        lines.add("Ma don: " + order.getCode());
        lines.add("Khach hang: " + fallback(order.getCustomerName(), "Khach le"));
        lines.add("So dien thoai: " + fallback(order.getCustomerPhone(), ""));
        lines.add("Ngay dat: " + (order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : ""));
        lines.add("San pham: " + fallback(order.getProductName(), ""));
        lines.add("So luong: " + (order.getQuantity() != null ? order.getQuantity() : 0));
        lines.add("Tong tien: " + currency.format(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO) + "d");
        lines.add("Thanh toan: " + fallback(order.getDisplayPaymentStatus(), ""));
        lines.add("Van chuyen: " + fallback(order.getDisplayShippingStatus(), ""));
        lines.add(repeat("-", 95));
        return lines;
    }

    private float writeLine(PDPageContentStream contentStream,
                            PDFont font,
                            float fontSize,
                            float x,
                            float y,
                            String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
        return y - LEADING;
    }

    private List<String> wrapLine(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("");
            return lines;
        }

        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            float width = font.getStringWidth(candidate) / 1000f * fontSize;
            if (width <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
            } else {
                if (!current.isEmpty()) {
                    lines.add(current.toString());
                }
                current.setLength(0);
                current.append(word);
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private Path resolveFontPath() {
        Path fontPath = Path.of(DEFAULT_FONT_PATH);
        if (Files.exists(fontPath)) {
            return fontPath;
        }
        throw new IllegalStateException("Khong tim thay font Unicode de tao PDF: " + DEFAULT_FONT_PATH);
    }

    private String repeat(String value, int count) {
        return value.repeat(Math.max(0, count));
    }

    private String fallback(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
