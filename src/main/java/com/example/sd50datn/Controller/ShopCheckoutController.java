package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.GioHang;
import com.example.sd50datn.Entity.GioHangChiTiet;
import com.example.sd50datn.Entity.HinhThucThanhToan;
import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Entity.ThanhToan;
import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiDTO;
import com.example.sd50datn.Repository.HinhThucThanhToanRepository;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Repository.ThanhToanRepository;
import com.example.sd50datn.Service.ChuongTrinhKhuyenMaiService;
import com.example.sd50datn.Service.GioHangService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/thanh-toan")
@RequiredArgsConstructor
public class ShopCheckoutController {

    private static final String PAYMENT_CASH = "Tiền mặt";
    private static final String PAYMENT_TRANSFER = "Chuyển khoản";
    private static final String VIET_QR_TEMPLATE = "https://img.vietqr.io/image/vietinbank-101878509895-compact2.jpg?amount=%s&addInfo=%s&accountName=Vu%%20Bao%%20Linh";

    private final GioHangService gioHangService;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepo;
    private final InvoiceRepository hoaDonRepo;
    private final HoaDonChiTietRepository hoaDonChiTietRepo;
    private final ThanhToanRepository thanhToanRepo;
    private final ChuongTrinhKhuyenMaiService chuongTrinhKhuyenMaiService;

    @GetMapping
    public String checkoutPage(@RequestParam(value = "voucherCode", required = false) String voucherCode,
                               HttpSession session,
                               Model model) {
        GioHang cart = gioHangService.getOrCreateCart(session);
        List<GioHangChiTiet> items = gioHangService.getCartItems(cart);

        if (items.isEmpty()) {
            return "redirect:/gio-hang";
        }

        BigDecimal subtotal = gioHangService.calculateTotal(items);
        BigDecimal discount = BigDecimal.ZERO;
        String voucherInfo = null;
        String voucherError = null;

        if (voucherCode != null && !voucherCode.isBlank()) {
            try {
                ChuongTrinhKhuyenMaiDTO promotion = chuongTrinhKhuyenMaiService.getPromotionByCode(voucherCode.trim());
                if (promotion != null) {
                    discount = chuongTrinhKhuyenMaiService.calculateDiscountForInvoice(promotion.getId(), subtotal);
                    voucherInfo = promotion.getMaChuongTrinh() + " - " + promotion.getTenChuongTrinh();
                } else {
                    voucherError = "Mã voucher không tồn tại hoặc không khả dụng";
                }
            } catch (RuntimeException ex) {
                voucherError = ex.getMessage();
            }
        }

        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO);

        model.addAttribute("cartItems", items);
        model.addAttribute("cartSubtotal", subtotal);
        model.addAttribute("cartTotal", total);
        model.addAttribute("discountAmount", discount);
        model.addAttribute("voucherCode", voucherCode != null ? voucherCode : "");
        model.addAttribute("voucherInfo", voucherInfo);
        model.addAttribute("voucherError", voucherError);
        model.addAttribute("cartItemCount", items.stream().mapToInt(i -> i.getSoLuong() != null ? i.getSoLuong() : 0).sum());
        model.addAttribute("paymentMethods", getAllowedStorefrontPaymentMethods());
        model.addAttribute("customerName", session.getAttribute("shopCustomerName"));
        model.addAttribute("customerEmail", session.getAttribute("shopCustomerEmail"));

        model.addAttribute("pageTitle", "Thanh toán — Yonex Store");
        model.addAttribute("activeMenu", "checkout");
        model.addAttribute("pageCss", "/shop/css/shop-checkout.css");
        model.addAttribute("content", "shop/checkout");

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> cartCrumb = new HashMap<>();
        cartCrumb.put("label", "Giỏ hàng");
        cartCrumb.put("url", "/gio-hang");
        breadcrumbItems.add(cartCrumb);

        Map<String, String> checkoutCrumb = new HashMap<>();
        checkoutCrumb.put("label", "Thanh toán");
        checkoutCrumb.put("url", null);
        breadcrumbItems.add(checkoutCrumb);
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "shop/shop-layout";
    }

    @PostMapping
    @Transactional
    public String placeOrder(@RequestParam String tenKhachHang,
                             @RequestParam String sdtKhachHang,
                             @RequestParam(required = false) String emailKhachHang,
                             @RequestParam String diaChiKhachHang,
                             @RequestParam Integer hinhThucThanhToanId,
                             @RequestParam(required = false) String voucherCode,
                             @RequestParam(required = false) String ghiChu,
                             HttpSession session,
                             RedirectAttributes ra) {
        GioHang cart = gioHangService.getOrCreateCart(session);
        List<GioHangChiTiet> items = gioHangService.getCartItems(cart);

        if (items.isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống");
            return "redirect:/gio-hang";
        }

        if (sdtKhachHang == null || sdtKhachHang.isBlank()) {
            ra.addFlashAttribute("error", "Vui lòng nhập số điện thoại");
            return "redirect:/thanh-toan";
        }

        List<HinhThucThanhToan> allowedPaymentMethods = getAllowedStorefrontPaymentMethods();
        HinhThucThanhToan selectedPaymentMethod = allowedPaymentMethods.stream()
                .filter(pm -> pm.getId().equals(hinhThucThanhToanId))
                .findFirst()
                .orElse(null);
        if (selectedPaymentMethod == null) {
            ra.addFlashAttribute("error", "Phương thức thanh toán không hợp lệ");
            return "redirect:/thanh-toan";
        }

        BigDecimal subtotal = gioHangService.calculateTotal(items);
        BigDecimal discount = BigDecimal.ZERO;
        ChuongTrinhKhuyenMaiDTO appliedPromotion = null;
        String voucherInfo = null;

        if (voucherCode != null && !voucherCode.isBlank()) {
            try {
                appliedPromotion = chuongTrinhKhuyenMaiService.getPromotionByCode(voucherCode.trim());
                if (appliedPromotion == null) {
                    ra.addFlashAttribute("error", "Mã voucher không tồn tại hoặc không khả dụng");
                    return "redirect:/thanh-toan";
                }
                discount = chuongTrinhKhuyenMaiService.calculateDiscountForInvoice(appliedPromotion.getId(), subtotal);
                voucherInfo = appliedPromotion.getMaChuongTrinh() + " - " + appliedPromotion.getTenChuongTrinh();
            } catch (RuntimeException ex) {
                ra.addFlashAttribute("error", ex.getMessage());
                return "redirect:/thanh-toan";
            }
        }

        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setNhanVienId(1);
        hoaDon.setTenKhachHang(tenKhachHang);
        hoaDon.setSdtKhachHang(sdtKhachHang);
        hoaDon.setEmailKhachHang(emailKhachHang);
        hoaDon.setDiaChiKhachHang(diaChiKhachHang);
        hoaDon.setHinhThucThanhToanId(hinhThucThanhToanId);
        hoaDon.setTongTienSauKhiGiam(total);
        hoaDon.setTrangThai(0);
        hoaDon.setLoaiHoaDon("Online");
        hoaDon.setGhiChu(ghiChu);
        hoaDon.setNgayTao(LocalDateTime.now());
        if (appliedPromotion != null) {
            hoaDon.setVoucherId(appliedPromotion.getId());
            hoaDon.setThongTinVoucher(voucherInfo);
        }

        Integer customerId = (Integer) session.getAttribute("shopCustomerId");
        if (customerId != null) {
            hoaDon.setKhachHangId(customerId);
        }

        hoaDon = hoaDonRepo.save(hoaDon);

        for (GioHangChiTiet cartItem : items) {
            HoaDonChiTiet detail = new HoaDonChiTiet();
            detail.setHoaDon(hoaDon);
            detail.setSanPham(cartItem.getSanPham());
            detail.setSoLuongSanPham(cartItem.getSoLuong());
            detail.setGia(cartItem.getGiaTaiThoiDiem() != null
                    ? cartItem.getGiaTaiThoiDiem()
                    : cartItem.getSanPham().getGiaBan());
            hoaDonChiTietRepo.save(detail);
            // Stock deduction happens when admin confirms the order (DA_XAC_NHAN transition)
        }

        gioHangService.clearCart(cart);

        if (isCashPayment(selectedPaymentMethod)) {
            saveSuccessfulPayment(hoaDon, selectedPaymentMethod, total, "COD-" + hoaDon.getId());
            ra.addFlashAttribute("orderId", hoaDon.getId());
            ra.addFlashAttribute("orderTotal", total);
            ra.addFlashAttribute("paymentMethodName", selectedPaymentMethod.getTenHinhThuc());
            return "redirect:/thanh-toan/xac-nhan";
        }

        session.setAttribute("pendingTransferOrderId", hoaDon.getId());
        return "redirect:/thanh-toan/chuyen-khoan";
    }

    @GetMapping("/chuyen-khoan")
    public String transferConfirmation(@RequestParam(value = "orderId", required = false) Integer orderId,
                                       HttpSession session,
                                       Model model) {
        Integer resolvedOrderId = orderId;
        if (resolvedOrderId == null) {
            resolvedOrderId = (Integer) session.getAttribute("pendingTransferOrderId");
        }
        if (resolvedOrderId == null) {
            return "redirect:/thanh-toan";
        }

        HoaDon order = hoaDonRepo.findById(resolvedOrderId).orElse(null);
        if (order == null) {
            return "redirect:/thanh-toan";
        }

        model.addAttribute("orderId", order.getId());
        model.addAttribute("orderTotal", order.getTongTienSauKhiGiam());
        model.addAttribute("paymentMethodName", PAYMENT_TRANSFER);
        model.addAttribute("vietQrUrl", buildVietQrUrl(order.getTongTienSauKhiGiam(), "SEVQR DH" + order.getId()));
        model.addAttribute("pageTitle", "Xác nhận chuyển khoản — Yonex Store");
        model.addAttribute("pageCss", "/shop/css/shop-checkout.css");
        model.addAttribute("content", "shop/order-confirmation");
        model.addAttribute("cartItemCount", gioHangService.getCartItemCount(session));
        model.addAttribute("isTransferPending", true);
        return "shop/shop-layout";
    }

    @PostMapping("/xac-nhan-chuyen-khoan")
    @Transactional
    public String confirmTransfer(@RequestParam Integer orderId, RedirectAttributes ra) {
        HoaDon order = hoaDonRepo.findById(orderId).orElse(null);
        if (order == null) {
            ra.addFlashAttribute("error", "Không tìm thấy đơn hàng để xác nhận thanh toán");
            return "redirect:/thanh-toan";
        }

        HinhThucThanhToan paymentMethod = hinhThucThanhToanRepo.findById(order.getHinhThucThanhToanId()).orElse(null);
        saveSuccessfulPayment(order, paymentMethod, order.getTongTienSauKhiGiam(), "QR-" + order.getId());

        ra.addFlashAttribute("orderId", order.getId());
        ra.addFlashAttribute("orderTotal", order.getTongTienSauKhiGiam());
        ra.addFlashAttribute("paymentMethodName", paymentMethod != null ? paymentMethod.getTenHinhThuc() : PAYMENT_TRANSFER);
        return "redirect:/thanh-toan/xac-nhan";
    }

    @GetMapping("/xac-nhan")
    public String orderConfirmation(HttpSession session, Model model) {
        model.addAttribute("pageTitle", "Đặt hàng thành công — Yonex Store");
        model.addAttribute("pageCss", "/shop/css/shop-checkout.css");
        model.addAttribute("content", "shop/order-confirmation");
        model.addAttribute("cartItemCount", gioHangService.getCartItemCount(session));
        model.addAttribute("isTransferPending", false);
        return "shop/shop-layout";
    }

    private List<HinhThucThanhToan> getAllowedStorefrontPaymentMethods() {
        return hinhThucThanhToanRepo.findByTenHinhThucIn(List.of(PAYMENT_TRANSFER, PAYMENT_CASH));
    }

    private boolean isCashPayment(HinhThucThanhToan paymentMethod) {
        return paymentMethod != null && PAYMENT_CASH.equalsIgnoreCase(paymentMethod.getTenHinhThuc());
    }

    private String buildVietQrUrl(BigDecimal amount, String addInfo) {
        long safeAmount = amount != null ? amount.longValue() : 0L;
        String safeInfo = addInfo != null ? addInfo.replace(" ", "%20") : "SEVQR";
        return String.format(VIET_QR_TEMPLATE, safeAmount, safeInfo);
    }

    private void saveSuccessfulPayment(HoaDon order,
                                       HinhThucThanhToan paymentMethod,
                                       BigDecimal amount,
                                       String transactionCode) {
        if (order == null || paymentMethod == null) {
            return;
        }
        ThanhToan thanhToan = thanhToanRepo.findByHoaDonId(order.getId()).orElseGet(ThanhToan::new);
        thanhToan.setHoaDonId(order.getId());
        thanhToan.setHinhThucThanhToanId(paymentMethod.getId());
        thanhToan.setSoTien(amount != null ? amount : BigDecimal.ZERO);
        thanhToan.setPaidAt(LocalDateTime.now());
        thanhToan.setMaGiaoDich(transactionCode);
        thanhToan.setTrangThai(1);
        thanhToanRepo.save(thanhToan);
    }
}
