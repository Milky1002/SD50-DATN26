package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.GioHang;
import com.example.sd50datn.Entity.GioHangChiTiet;
import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Repository.HinhThucThanhToanRepository;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
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

    private final GioHangService gioHangService;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepo;
    private final InvoiceRepository hoaDonRepo;
    private final HoaDonChiTietRepository hoaDonChiTietRepo;
    private final SanPhamRepository sanPhamRepo;

    @GetMapping
    public String checkoutPage(HttpSession session, Model model) {
        GioHang cart = gioHangService.getOrCreateCart(session);
        List<GioHangChiTiet> items = gioHangService.getCartItems(cart);

        if (items.isEmpty()) {
            return "redirect:/gio-hang";
        }

        BigDecimal total = gioHangService.calculateTotal(items);

        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", total);
        model.addAttribute("cartItemCount", items.stream().mapToInt(i -> i.getSoLuong() != null ? i.getSoLuong() : 0).sum());
        model.addAttribute("paymentMethods", hinhThucThanhToanRepo.findAll());
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
                             @RequestParam(required = false) String ghiChu,
                             HttpSession session,
                             RedirectAttributes ra) {
        GioHang cart = gioHangService.getOrCreateCart(session);
        List<GioHangChiTiet> items = gioHangService.getCartItems(cart);

        if (items.isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống");
            return "redirect:/gio-hang";
        }

        BigDecimal total = gioHangService.calculateTotal(items);

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

            if (cartItem.getSanPham() != null && cartItem.getSanPham().getId() != null && cartItem.getSoLuong() != null) {
                sanPhamRepo.findById(cartItem.getSanPham().getId()).ifPresent(sp -> {
                    Integer currentStock = sp.getSoLuongTon() != null ? sp.getSoLuongTon() : 0;
                    sp.setSoLuongTon(Math.max(0, currentStock - cartItem.getSoLuong()));
                    sanPhamRepo.save(sp);
                });
            }
        }

        gioHangService.clearCart(cart);

        ra.addFlashAttribute("orderId", hoaDon.getId());
        ra.addFlashAttribute("orderTotal", total);
        return "redirect:/thanh-toan/xac-nhan";
    }

    @GetMapping("/xac-nhan")
    public String orderConfirmation(Model model) {
        model.addAttribute("pageTitle", "Đặt hàng thành công — Yonex Store");
        model.addAttribute("pageCss", "/shop/css/shop-checkout.css");
        model.addAttribute("content", "shop/order-confirmation");
        model.addAttribute("cartItemCount", 0);
        return "shop/shop-layout";
    }
}
