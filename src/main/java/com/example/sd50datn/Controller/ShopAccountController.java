package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Entity.HinhThucThanhToan;
import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Repository.HinhThucThanhToanRepository;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Service.GioHangService;
import com.example.sd50datn.Service.KhachHangService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tai-khoan")
@RequiredArgsConstructor
public class ShopAccountController {

    private final KhachHangService khachHangService;
    private final InvoiceRepository hoaDonRepo;
    private final HoaDonChiTietRepository hoaDonChiTietRepo;
    private final GioHangService gioHangService;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepo;

    private void addCommonAttributes(Model model, HttpSession session, String activeTab) {
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");
        model.addAttribute("customerId", customerId);
        model.addAttribute("customerName", session.getAttribute("shopCustomerName"));
        model.addAttribute("customerEmail", session.getAttribute("shopCustomerEmail"));
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("activeMenu", "account");
        model.addAttribute("pageCss", "/shop/css/shop-account.css");
        model.addAttribute("cartItemCount", gioHangService.getCartItemCount(session));
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        addCommonAttributes(model, session, "dashboard");
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");

        List<HoaDon> allOrders = hoaDonRepo.findAll().stream()
                .filter(hd -> customerId != null && customerId.equals(hd.getKhachHangId()))
                .sorted(Comparator.comparing(HoaDon::getNgayTao, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        model.addAttribute("recentOrders", allOrders.stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("totalOrders", allOrders.size());
        model.addAttribute("pageTitle", "Tài khoản — Yonex Store");
        model.addAttribute("content", "shop/account/dashboard");

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> crumb = new HashMap<>();
        crumb.put("label", "Tài khoản");
        crumb.put("url", null);
        breadcrumbItems.add(crumb);
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "shop/shop-layout";
    }

    @GetMapping("/don-hang")
    public String orders(HttpSession session, Model model) {
        addCommonAttributes(model, session, "orders");
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");

        List<HoaDon> orders = hoaDonRepo.findAll().stream()
                .filter(hd -> customerId != null && customerId.equals(hd.getKhachHangId()))
                .sorted(Comparator.comparing(HoaDon::getNgayTao, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Đơn hàng — Yonex Store");
        model.addAttribute("content", "shop/account/orders");

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> accCrumb = new HashMap<>();
        accCrumb.put("label", "Tài khoản");
        accCrumb.put("url", "/tai-khoan");
        breadcrumbItems.add(accCrumb);
        Map<String, String> orderCrumb = new HashMap<>();
        orderCrumb.put("label", "Đơn hàng");
        orderCrumb.put("url", null);
        breadcrumbItems.add(orderCrumb);
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "shop/shop-layout";
    }

    @GetMapping("/don-hang/{id}")
    public String orderDetail(@PathVariable Integer id, HttpSession session, Model model) {
        addCommonAttributes(model, session, "orders");
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");

        HoaDon order = hoaDonRepo.findById(id).orElse(null);
        if (order == null || customerId == null || !customerId.equals(order.getKhachHangId())) {
            return "redirect:/tai-khoan/don-hang";
        }

        List<HoaDonChiTiet> orderItems = hoaDonChiTietRepo.findByHoaDonId(id);
        HinhThucThanhToan paymentMethod = order.getHinhThucThanhToanId() != null
                ? hinhThucThanhToanRepo.findById(order.getHinhThucThanhToanId()).orElse(null)
                : null;

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("paymentMethodName", paymentMethod != null ? paymentMethod.getTenHinhThuc() : null);
        model.addAttribute("pageTitle", "Đơn hàng #" + id + " — Yonex Store");
        model.addAttribute("content", "shop/account/order-detail");

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> accCrumb = new HashMap<>();
        accCrumb.put("label", "Tài khoản");
        accCrumb.put("url", "/tai-khoan");
        breadcrumbItems.add(accCrumb);
        Map<String, String> ordersCrumb = new HashMap<>();
        ordersCrumb.put("label", "Đơn hàng");
        ordersCrumb.put("url", "/tai-khoan/don-hang");
        breadcrumbItems.add(ordersCrumb);
        Map<String, String> detailCrumb = new HashMap<>();
        detailCrumb.put("label", "#" + id);
        detailCrumb.put("url", null);
        breadcrumbItems.add(detailCrumb);
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "shop/shop-layout";
    }

    @GetMapping("/ho-so")
    public String profile(HttpSession session, Model model) {
        addCommonAttributes(model, session, "profile");
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");
        KhachHang customer = khachHangService.getById(customerId);

        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "Hồ sơ — Yonex Store");
        model.addAttribute("content", "shop/account/profile");

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> accCrumb = new HashMap<>();
        accCrumb.put("label", "Tài khoản");
        accCrumb.put("url", "/tai-khoan");
        breadcrumbItems.add(accCrumb);
        Map<String, String> profileCrumb = new HashMap<>();
        profileCrumb.put("label", "Hồ sơ");
        profileCrumb.put("url", null);
        breadcrumbItems.add(profileCrumb);
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "shop/shop-layout";
    }

    @PostMapping("/ho-so")
    public String updateProfile(@RequestParam String tenKhachHang,
                                @RequestParam String sdt,
                                @RequestParam String diaChiKhachHang,
                                HttpSession session,
                                RedirectAttributes ra) {
        Integer customerId = (Integer) session.getAttribute("shopCustomerId");
        KhachHang customer = khachHangService.getById(customerId);
        if (customer == null) {
            return "redirect:/dang-nhap";
        }

        customer.setTenKhachHang(tenKhachHang);
        customer.setSdt(sdt);
        customer.setDiaChiKhachHang(diaChiKhachHang);
        customer.setNgayCapNhat(LocalDateTime.now());
        khachHangService.update(customerId, customer);

        session.setAttribute("shopCustomerName", tenKhachHang);
        ra.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        return "redirect:/tai-khoan/ho-so";
    }
}
