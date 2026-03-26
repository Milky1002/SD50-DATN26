package com.example.sd50datn.Controller;

import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiDTO;
import com.example.sd50datn.Entity.DanhMucSanPham;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Service.ChuongTrinhKhuyenMaiService;
import com.example.sd50datn.Service.DanhMucSanPhamService;
import com.example.sd50datn.Service.GioHangService;
import com.example.sd50datn.Service.HomepageConfigService;
import com.example.sd50datn.Service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Storefront homepage controller.
 * Route: GET /  — public, no auth required (excluded in WebMvcConfig).
 * Renders via the shared shop layout: templates/shop/shop-layout.html,
 * delegating the page body to the "content" fragment in templates/shop/home.html.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SanPhamService sanPhamService;
    private final DanhMucSanPhamService danhMucService;
    private final ChuongTrinhKhuyenMaiService khuyenMaiService;
    private final HomepageConfigService homepageConfigService;
    private final GioHangService gioHangService;

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        List<SanPham> activeProducts = sanPhamService.search(null, 1, null);

        List<SanPham> featuredProducts = homepageConfigService.getHotProducts();

        List<SanPham> newestProducts = activeProducts.stream()
                .limit(8)
                .collect(Collectors.toList());

        List<DanhMucSanPham> homepageCategories = homepageConfigService.getHomepageCategories();

        // Only display active categories on storefront
        List<DanhMucSanPham> categories = danhMucService.getAll().stream()
                .filter(dm -> Integer.valueOf(1).equals(dm.getTrangThai()))
                .collect(Collectors.toList());

        // Active promotions (within current date/time window)
        List<ChuongTrinhKhuyenMaiDTO> activePromotions = khuyenMaiService.getActivePromotions();

        model.addAttribute("pageTitle",       "Trang chủ - Yonex Store");
        model.addAttribute("activeMenu",      "home");
        model.addAttribute("content",         "shop/home");
        model.addAttribute("pageCss",         "/shop/css/shop-home.css");
        model.addAttribute("cartItemCount",   gioHangService.getCartItemCount(session));
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("newestProducts",   newestProducts);
        model.addAttribute("categories",       categories);
        model.addAttribute("homepageCategories", homepageCategories);
        model.addAttribute("homepageCategoryProducts", homepageConfigService.getLatestProductsByConfiguredCategory());
        model.addAttribute("activePromotions", activePromotions);
        model.addAttribute("trustHighlights", List.of(
                "Chính hãng 100%",
                "Giao nhanh toàn quốc",
                "Theo dõi đơn hàng rõ ràng",
                "Thanh toán linh hoạt"
        ));

        return "shop/shop-layout";
    }
}
