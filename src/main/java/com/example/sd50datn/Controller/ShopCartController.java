package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.GioHang;
import com.example.sd50datn.Entity.GioHangChiTiet;
import com.example.sd50datn.Service.GioHangService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/gio-hang")
@RequiredArgsConstructor
public class ShopCartController {

    private final GioHangService gioHangService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        GioHang cart = gioHangService.getOrCreateCart(session);
        List<GioHangChiTiet> items = gioHangService.getCartItems(cart);
        BigDecimal total = gioHangService.calculateTotal(items);

        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", total);
        model.addAttribute("cartItemCount", items.stream().mapToInt(i -> i.getSoLuong() != null ? i.getSoLuong() : 0).sum());
        model.addAttribute("pageTitle", "Giỏ hàng — Yonex Store");
        model.addAttribute("activeMenu", "cart");
        model.addAttribute("pageCss", "/shop/css/shop-cart.css");
        model.addAttribute("content", "shop/cart");

        return "shop/shop-layout";
    }

    @PostMapping("/them")
    public String addToCart(@RequestParam Integer sanPhamId,
                            @RequestParam(defaultValue = "1") Integer soLuong,
                            HttpSession session, RedirectAttributes ra) {
        String error = gioHangService.addToCart(session, sanPhamId, soLuong);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng!");
        }
        return "redirect:/gio-hang";
    }

    @PostMapping("/cap-nhat")
    public String updateQuantity(@RequestParam Integer chiTietId,
                                 @RequestParam Integer soLuong,
                                 RedirectAttributes ra) {
        gioHangService.updateQuantity(chiTietId, soLuong);
        ra.addFlashAttribute("success", "Đã cập nhật số lượng");
        return "redirect:/gio-hang";
    }

    @PostMapping("/xoa")
    public String removeItem(@RequestParam Integer chiTietId, RedirectAttributes ra) {
        gioHangService.removeItem(chiTietId);
        ra.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
        return "redirect:/gio-hang";
    }

    @PostMapping("/xoa-tat-ca")
    public String clearCart(HttpSession session, RedirectAttributes ra) {
        GioHang cart = gioHangService.getOrCreateCart(session);
        gioHangService.clearCart(cart);
        ra.addFlashAttribute("success", "Đã xóa toàn bộ giỏ hàng");
        return "redirect:/gio-hang";
    }
}
