package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.HinhThucThanhToanRepository;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import com.example.sd50datn.Service.GioHangService;
import com.example.sd50datn.Service.SanPhamService;
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

/**
 * Handles "Mua ngay" (quick buy) flow that does NOT require shop customer authentication.
 *
 * Routes:
 *   POST /dat-hang-nhanh  — create a HoaDon from a single product + customer info form
 *   GET  /dat-hang-nhanh/xac-nhan — order confirmation page (auth-free)
 *
 * Both endpoints must be excluded from ShopAuthInterceptor (WebMvcConfig only
 * applies ShopAuthInterceptor to /tai-khoan/** and /thanh-toan/**).
 * Also excluded from admin AuthInterceptor via WebMvcConfig.
 */
@Controller
@RequestMapping("/dat-hang-nhanh")
@RequiredArgsConstructor
public class GuestOrderController {

    private final SanPhamService           sanPhamService;
    private final InvoiceRepository        hoaDonRepo;
    private final HoaDonChiTietRepository  hoaDonChiTietRepo;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepo;
    private final SanPhamRepository        sanPhamRepo;
    private final GioHangService gioHangService;

    /** POST /dat-hang-nhanh — create order from single-product quick-buy form */
    @PostMapping
    @Transactional
    public String placeQuickOrder(
            @RequestParam Integer sanPhamId,
            @RequestParam(defaultValue = "1") Integer soLuong,
            @RequestParam String tenKhachHang,
            @RequestParam String sdtKhachHang,
            @RequestParam(required = false) String emailKhachHang,
            @RequestParam String diaChiKhachHang,
            @RequestParam Integer hinhThucThanhToanId,
            @RequestParam(required = false) String ghiChu,
            HttpSession session,
            RedirectAttributes ra) {

        SanPham product = sanPhamService.getByIdWithRelations(sanPhamId);
        if (product == null) {
            ra.addFlashAttribute("error", "Sản phẩm không tồn tại.");
            return "redirect:/cua-hang";
        }

        if (product.getSoLuongTon() == null || product.getSoLuongTon() <= 0) {
            ra.addFlashAttribute("error", "Sản phẩm tạm hết hàng.");
            return "redirect:/cua-hang/" + sanPhamId;
        }

        // Clamp quantity to available stock
        int qty = Math.max(1, Math.min(soLuong != null ? soLuong : 1, product.getSoLuongTon()));

        BigDecimal unitPrice = product.getGiaBan() != null ? product.getGiaBan() : BigDecimal.ZERO;
        BigDecimal total     = unitPrice.multiply(BigDecimal.valueOf(qty));

        // Build HoaDon
        HoaDon hoaDon = new HoaDon();
        hoaDon.setNhanVienId(1);                       // system employee for online orders
        hoaDon.setTenKhachHang(tenKhachHang.trim());
        hoaDon.setSdtKhachHang(sdtKhachHang.trim());
        hoaDon.setEmailKhachHang(emailKhachHang != null ? emailKhachHang.trim() : null);
        hoaDon.setDiaChiKhachHang(diaChiKhachHang.trim());
        hoaDon.setHinhThucThanhToanId(hinhThucThanhToanId);
        hoaDon.setTongTienSauKhiGiam(total);
        hoaDon.setTrangThai(0);                        // 0 = pending online order
        hoaDon.setLoaiHoaDon("Online");
        hoaDon.setGhiChu(ghiChu);
        hoaDon.setNgayTao(LocalDateTime.now());

        // Link to logged-in shop customer if present
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");
        if (customerId != null) {
            hoaDon.setKhachHangId(customerId);
        }

        hoaDon = hoaDonRepo.save(hoaDon);

        // Build line item
        HoaDonChiTiet chiTiet = new HoaDonChiTiet();
        chiTiet.setHoaDon(hoaDon);
        chiTiet.setSanPham(product);
        chiTiet.setSoLuongSanPham(qty);
        chiTiet.setGia(unitPrice);
        hoaDonChiTietRepo.save(chiTiet);

        sanPhamRepo.findById(product.getId()).ifPresent(sp -> {
            Integer currentStock = sp.getSoLuongTon() != null ? sp.getSoLuongTon() : 0;
            sp.setSoLuongTon(Math.max(0, currentStock - qty));
            sanPhamRepo.save(sp);
        });

        ra.addFlashAttribute("orderId",    hoaDon.getId());
        ra.addFlashAttribute("orderTotal", total);
        ra.addFlashAttribute("orderName",  tenKhachHang.trim());
        return "redirect:/dat-hang-nhanh/xac-nhan";
    }

    /** GET /dat-hang-nhanh/xac-nhan — confirmation page (no auth required) */
    @GetMapping("/xac-nhan")
    public String confirmation(HttpSession session, Model model) {
        model.addAttribute("pageTitle",    "Đặt hàng thành công — Yonex Store");
        model.addAttribute("pageCss",      "/shop/css/shop-checkout.css");
        model.addAttribute("content",      "shop/order-confirmation");
        model.addAttribute("cartItemCount", gioHangService.getCartItemCount(session));
        model.addAttribute("activeMenu",   "products");
        return "shop/shop-layout";
    }
}
