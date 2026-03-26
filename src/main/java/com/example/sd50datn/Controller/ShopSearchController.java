package com.example.sd50datn.Controller;

import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiDTO;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Service.ChuongTrinhKhuyenMaiService;
import com.example.sd50datn.Service.GioHangService;
import com.example.sd50datn.Service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ShopSearchController {

    private final SanPhamService sanPhamService;
    private final ChuongTrinhKhuyenMaiService khuyenMaiService;
    private final GioHangService gioHangService;

    @GetMapping("/tim-kiem")
    public String search(@RequestParam(value = "q", required = false) String q, HttpSession session, Model model) {
        List<SanPham> results = new ArrayList<>();
        if (q != null && !q.trim().isEmpty()) {
            results = sanPhamService.search(q.trim(), 1, null);
        }

        model.addAttribute("query", q != null ? q : "");
        model.addAttribute("results", results);
        model.addAttribute("resultCount", results.size());
        model.addAttribute("pageTitle", (q != null ? "Tìm kiếm: " + q : "Tìm kiếm") + " — Yonex Store");
        model.addAttribute("activeMenu", "");
        model.addAttribute("pageCss", "/shop/css/shop-home.css");
        model.addAttribute("pageCss2", "/shop/css/shop-search.css");
        model.addAttribute("content", "shop/search");
        model.addAttribute("cartItemCount", gioHangService.getCartItemCount(session));

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> searchCrumb = new HashMap<>();
        searchCrumb.put("label", "Tìm kiếm");
        searchCrumb.put("url", null);
        breadcrumbItems.add(searchCrumb);
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "shop/shop-layout";
    }

    @GetMapping("/khuyen-mai")
    public String promotions(HttpSession session, Model model) {
        List<ChuongTrinhKhuyenMaiDTO> activePromotions = khuyenMaiService.getActivePromotions();
        List<ChuongTrinhKhuyenMaiDTO> allPromotions = khuyenMaiService.getAllPromotions();

        model.addAttribute("activePromotions", activePromotions);
        model.addAttribute("allPromotions", allPromotions);
        model.addAttribute("pageTitle", "Khuyến mãi — Yonex Store");
        model.addAttribute("activeMenu", "");
        model.addAttribute("pageCss", "/shop/css/shop-search.css");
        model.addAttribute("content", "shop/promotions");
        model.addAttribute("cartItemCount", gioHangService.getCartItemCount(session));

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> promoCrumb = new HashMap<>();
        promoCrumb.put("label", "Khuyến mãi");
        promoCrumb.put("url", null);
        breadcrumbItems.add(promoCrumb);
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "shop/shop-layout";
    }
}
