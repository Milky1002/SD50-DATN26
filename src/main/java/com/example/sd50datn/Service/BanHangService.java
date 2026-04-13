package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.*;
import com.example.sd50datn.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BanHangService {

    private final InvoiceRepository hoaDonRepo;
    private final HoaDonChiTietRepository hoaDonChiTietRepo;
    private final SanPhamRepository sanPhamRepo;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepo;
    private final ThanhToanRepository thanhToanRepo;
    private final ChuongTrinhKhuyenMaiRepository chuongTrinhKhuyenMaiRepo;
    private final LichSuApDungKhuyenMaiRepository lichSuApDungRepo;
    private final NhanVienHoatDongService nhanVienHoatDongService;

    @Transactional
    public HoaDon checkout(String tenKhachHang, String sdtKhachHang,
                           String ghiChu, String phuongThucThanhToan,
                           int tienKhachDua,
                           List<Map<String, Object>> items,
                           Integer promotionId,
                           Integer nhanVienId,
                           String hoTenNhanVien) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống");
        }

        BigDecimal tongTien = BigDecimal.ZERO;

        // Validate stock first
        for (Map<String, Object> item : items) {
            int spId = ((Number) item.get("sanPhamId")).intValue();
            int soLuong = ((Number) item.get("soLuong")).intValue();
            SanPham sp = sanPhamRepo.findById(spId)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + spId));
            if (sp.getSoLuongTon() < soLuong) {
                throw new IllegalArgumentException("Sản phẩm " + sp.getTenSanPham() + " không đủ tồn kho");
            }
        }

        // Resolve payment method from DB instead of hardcoded IDs
        Integer hinhThucThanhToanId = resolvePaymentMethodId(phuongThucThanhToan);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setNhanVienId(nhanVienId != null ? nhanVienId : 1);
        hoaDon.setTenKhachHang(tenKhachHang != null && !tenKhachHang.isBlank() ? tenKhachHang : "Khách lẻ");
        hoaDon.setSdtKhachHang(sdtKhachHang);
        hoaDon.setGhiChu(ghiChu);
        hoaDon.setHinhThucThanhToanId(hinhThucThanhToanId);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTrangThai(1);
        hoaDon.setLoaiHoaDon("TAI_QUAY");
        hoaDon.setTongTienSauKhiGiam(BigDecimal.ZERO);

        hoaDon = hoaDonRepo.save(hoaDon);

        // Save line items
        for (Map<String, Object> item : items) {
            int spId = ((Number) item.get("sanPhamId")).intValue();
            int soLuong = ((Number) item.get("soLuong")).intValue();

            SanPham sp = sanPhamRepo.findById(spId).orElseThrow();

            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setSanPham(sp);
            chiTiet.setSoLuongSanPham(soLuong);
            chiTiet.setGia(sp.getGiaBan());
            hoaDonChiTietRepo.save(chiTiet);

            BigDecimal lineTotal = sp.getGiaBan().multiply(BigDecimal.valueOf(soLuong));
            tongTien = tongTien.add(lineTotal);

            sp.setSoLuongTon(sp.getSoLuongTon() - soLuong);
            sanPhamRepo.save(sp);
        }

        // Apply promotion discount
        BigDecimal discount = BigDecimal.ZERO;
        ChuongTrinhKhuyenMai appliedPromotion = null;

        if (promotionId != null) {
            // Manual promotion selected
            appliedPromotion = chuongTrinhKhuyenMaiRepo.findById(promotionId).orElse(null);
            if (appliedPromotion != null) {
                discount = calculateDiscount(appliedPromotion, tongTien);
            }
        } else {
            // Try auto-apply: find best active invoice-level promotion with tuDongApDung=true
            List<ChuongTrinhKhuyenMai> autoPromotions = chuongTrinhKhuyenMaiRepo
                    .findActivePromotionsByType(LocalDateTime.now(), 1); // loaiKhuyenMai=1 (invoice)
            BigDecimal bestDiscount = BigDecimal.ZERO;
            for (ChuongTrinhKhuyenMai promo : autoPromotions) {
                if (Boolean.TRUE.equals(promo.getTuDongApDung())) {
                    if (promo.getDonHangToiThieu() == null || tongTien.compareTo(promo.getDonHangToiThieu()) >= 0) {
                        BigDecimal d = calculateDiscount(promo, tongTien);
                        if (d.compareTo(bestDiscount) > 0) {
                            bestDiscount = d;
                            appliedPromotion = promo;
                        }
                    }
                }
            }
            discount = bestDiscount;
        }

        BigDecimal tongTienSauGiam = tongTien.subtract(discount);
        if (tongTienSauGiam.compareTo(BigDecimal.ZERO) < 0) {
            tongTienSauGiam = BigDecimal.ZERO;
        }
        hoaDon.setTongTienSauKhiGiam(tongTienSauGiam);
        hoaDonRepo.save(hoaDon);

        // Log promotion application history
        if (appliedPromotion != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            LichSuApDungKhuyenMai lichSu = new LichSuApDungKhuyenMai();
            lichSu.setChuongTrinhKhuyenMai(appliedPromotion);
            lichSu.setHoaDon(hoaDon);
            lichSu.setGiaTriGiam(discount);
            lichSu.setNgayApDung(LocalDateTime.now());
            lichSuApDungRepo.save(lichSu);
        }

        // Log staff activity
        String moTa = String.format("Bán tại quầy – %d sản phẩm – Tổng: %,.0f đ",
                items.size(), tongTienSauGiam);
        nhanVienHoatDongService.log(
                nhanVienId, hoTenNhanVien,
                "SALE_OFFLINE", "HOA_DON", hoaDon.getId(),
                moTa, tongTienSauGiam);

        if (!"transfer".equalsIgnoreCase(phuongThucThanhToan)) {
            saveSuccessfulPayment(hoaDon, hinhThucThanhToanId, tongTienSauGiam, "POS-CASH-" + hoaDon.getId());
        }

        return hoaDon;
    }

    /**
     * Backward-compatible checkout without nhanVienId / hoTen (defaults to nhanVienId=1)
     */
    @Transactional
    public HoaDon checkout(String tenKhachHang, String sdtKhachHang,
                           String ghiChu, String phuongThucThanhToan,
                           int tienKhachDua,
                           List<Map<String, Object>> items,
                           Integer promotionId) {
        return checkout(tenKhachHang, sdtKhachHang, ghiChu, phuongThucThanhToan,
                tienKhachDua, items, promotionId, null, null);
    }

    /**
     * Backward-compatible checkout without promotionId
     */
    @Transactional
    public HoaDon checkout(String tenKhachHang, String sdtKhachHang,
                           String ghiChu, String phuongThucThanhToan,
                           int tienKhachDua,
                           List<Map<String, Object>> items) {
        return checkout(tenKhachHang, sdtKhachHang, ghiChu, phuongThucThanhToan, tienKhachDua, items, null);
    }

    /**
     * Resolve payment method string to DB ID.
     * Falls back to first available payment method if lookup fails.
     */
    private Integer resolvePaymentMethodId(String phuongThucThanhToan) {
        if (phuongThucThanhToan == null || phuongThucThanhToan.isBlank()) {
            return getDefaultPaymentMethodId();
        }

        String tenHinhThuc;
        switch (phuongThucThanhToan.toLowerCase()) {
            case "cash":      tenHinhThuc = "Tien mat"; break;
            case "transfer":  tenHinhThuc = "Chuyen khoan"; break;
            case "card":      tenHinhThuc = "The tin dung"; break;
            case "ewallet":   tenHinhThuc = "Vi dien tu"; break;
            default:          tenHinhThuc = phuongThucThanhToan; break;
        }

        return hinhThucThanhToanRepo.findByTenHinhThuc(tenHinhThuc)
                .map(HinhThucThanhToan::getId)
                .orElseGet(this::getDefaultPaymentMethodId);
    }

    private Integer getDefaultPaymentMethodId() {
        List<HinhThucThanhToan> all = hinhThucThanhToanRepo.findAll();
        return all.isEmpty() ? null : all.get(0).getId();
    }

    @Transactional
    public void confirmTransferPayment(Integer hoaDonId) {
        HoaDon hoaDon = hoaDonRepo.findById(hoaDonId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));
        Integer paymentMethodId = hoaDon.getHinhThucThanhToanId();
        saveSuccessfulPayment(hoaDon, paymentMethodId, hoaDon.getTongTienSauKhiGiam(), "POS-QR-" + hoaDon.getId());
    }

    private void saveSuccessfulPayment(HoaDon hoaDon,
                                       Integer paymentMethodId,
                                       BigDecimal amount,
                                       String transactionCode) {
        if (hoaDon == null || paymentMethodId == null) {
            return;
        }
        ThanhToan thanhToan = thanhToanRepo.findByHoaDonId(hoaDon.getId()).orElseGet(ThanhToan::new);
        thanhToan.setHoaDonId(hoaDon.getId());
        thanhToan.setHinhThucThanhToanId(paymentMethodId);
        thanhToan.setSoTien(amount != null ? amount : BigDecimal.ZERO);
        thanhToan.setPaidAt(LocalDateTime.now());
        thanhToan.setMaGiaoDich(transactionCode);
        thanhToan.setTrangThai(1);
        thanhToanRepo.save(thanhToan);

        // Mark invoice as completed (trangThai=2) so dashboard counts it
        hoaDon.setTrangThai(2);
        hoaDonRepo.save(hoaDon);
    }

    /**
     * Calculate discount for a promotion based on order total.
     * Reuses same logic as ChuongTrinhKhuyenMaiService.calculateDiscountForInvoice.
     */
    private BigDecimal calculateDiscount(ChuongTrinhKhuyenMai promotion, BigDecimal orderTotal) {
        if (promotion.getLoaiKhuyenMai() != 1) {
            return BigDecimal.ZERO; // Only invoice-level promotions supported here
        }

        BigDecimal discount;
        if (promotion.getLoaiGiam() == 1) { // Percentage
            discount = orderTotal.multiply(promotion.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            if (promotion.getGiamToiDa() != null && discount.compareTo(promotion.getGiamToiDa()) > 0) {
                discount = promotion.getGiamToiDa();
            }
        } else { // Fixed amount
            discount = promotion.getGiaTriGiam();
        }

        return discount.min(orderTotal);
    }
}
