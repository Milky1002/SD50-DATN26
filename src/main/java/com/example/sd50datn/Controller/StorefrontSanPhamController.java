package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.DanhMucSanPham;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Service.DanhMucSanPhamService;
import com.example.sd50datn.Service.GioHangService;
import com.example.sd50datn.Service.MauSacService;
import com.example.sd50datn.Service.SanPhamService;
import com.example.sd50datn.Repository.HinhThucThanhToanRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Storefront product listing page.
 *
 * Route: GET /cua-hang — public (excluded from admin AuthInterceptor in WebMvcConfig).
 *
 * NOTE on routing: The admin CRUD controller for products is mapped to /san-pham
 * (SanPhamController). Mapping both to /san-pham would cause an AmbiguousMappingException,
 * so the storefront listing lives at /cua-hang instead. The shop nav and footer links in
 * shop-layout.html have been updated accordingly. Links inside home.html (hero button,
 * category cards) that still point to /san-pham need a follow-up pass to point to /cua-hang.
 *
 * Supported query params:
 *   q          — keyword search (matched by SanPhamService against name/code/sku/barcode)
 *   danhMucId  — category filter (Integer)
 *   mauSacId   — color filter, applied in-memory (Integer)
 *   conHang    — stock filter; "true" keeps only items with soLuongTon > 0
 *   sort       — newest (default) | price_asc | price_desc | name
 */
@Controller
@RequestMapping("/cua-hang")
@RequiredArgsConstructor
public class StorefrontSanPhamController {

    private final SanPhamService sanPhamService;
    private final DanhMucSanPhamService danhMucService;
    private final MauSacService mauSacService;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepo;
    private final GioHangService gioHangService;

    @GetMapping
    public String list(
            @RequestParam(value = "q",         required = false)                  String  q,
            @RequestParam(value = "danhMucId", required = false)                  Integer danhMucId,
            @RequestParam(value = "mauSacId",  required = false)                  Integer mauSacId,
            @RequestParam(value = "conHang",   required = false, defaultValue = "false") boolean conHang,
            @RequestParam(value = "sort",      required = false, defaultValue = "newest") String sort,
            HttpSession session,
            Model model) {

        // --- Fetch: active products only (trangThai = 1), with keyword + category filter ---
        List<SanPham> products = sanPhamService.search(q, 1, danhMucId);

        // --- In-memory: color filter ---
        if (mauSacId != null) {
            final Integer colorId = mauSacId;
            products = products.stream()
                    .filter(sp -> sp.getMauSac() != null
                            && colorId.equals(sp.getMauSac().getMauSacId()))
                    .collect(Collectors.toList());
        }

        // --- In-memory: stock filter ---
        if (conHang) {
            products = products.stream()
                    .filter(sp -> sp.getSoLuongTon() != null && sp.getSoLuongTon() > 0)
                    .collect(Collectors.toList());
        }

        // --- In-memory: sort ---
        switch (sort) {
            case "price_asc":
                products.sort(Comparator.comparing(
                        sp -> sp.getGiaBan() != null ? sp.getGiaBan() : java.math.BigDecimal.ZERO));
                break;
            case "price_desc":
                products.sort(Comparator.comparing(
                        (SanPham sp) -> sp.getGiaBan() != null ? sp.getGiaBan() : java.math.BigDecimal.ZERO
                ).reversed());
                break;
            case "name":
                products.sort(Comparator.comparing(
                        sp -> sp.getTenSanPham() != null ? sp.getTenSanPham().toLowerCase() : ""));
                break;
            default: // "newest" — repository already returns ORDER BY id DESC; nothing extra needed
                break;
        }

        // --- Sidebar data ---
        List<DanhMucSanPham> activeCategories = danhMucService.getAll().stream()
                .filter(dm -> Integer.valueOf(1).equals(dm.getTrangThai()))
                .collect(Collectors.toList());

        // --- Breadcrumb: [Trang chủ →] Cửa hàng [→ Danh mục name] ---
        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> listingCrumb = new HashMap<>();
        listingCrumb.put("label", "Sản phẩm");
        listingCrumb.put("url", null); // last item — rendered as <span>
        if (danhMucId != null) {
            listingCrumb.put("url", "/cua-hang");
            breadcrumbItems.add(listingCrumb);
            DanhMucSanPham selected = danhMucService.getById(danhMucId);
            if (selected != null) {
                Map<String, String> catCrumb = new HashMap<>();
                catCrumb.put("label", selected.getTenDanhMuc());
                catCrumb.put("url", null);
                breadcrumbItems.add(catCrumb);
            }
        } else {
            breadcrumbItems.add(listingCrumb);
        }

        // --- Model ---
        model.addAttribute("products",           products);
        model.addAttribute("danhMucList",        activeCategories);
        model.addAttribute("mauSacList",         mauSacService.getAll());

        model.addAttribute("q",                  q != null ? q : "");
        model.addAttribute("selectedDanhMucId",  danhMucId);
        model.addAttribute("selectedMauSacId",   mauSacId);
        model.addAttribute("selectedSort",       sort);
        model.addAttribute("conHang",            conHang);

        model.addAttribute("breadcrumbItems",    breadcrumbItems);
        model.addAttribute("cartItemCount",      gioHangService.getCartItemCount(session));
        model.addAttribute("pageTitle",          "Sản phẩm — Yonex Store");
        model.addAttribute("activeMenu",         "products");
        // pageCss loads shared product-card styles; pageCss2 loads listing-layout-specific styles
        model.addAttribute("pageCss",            "/shop/css/shop-home.css");
        model.addAttribute("pageCss2",           "/shop/css/shop-product-list.css");
        model.addAttribute("content",            "shop/product-list");

        return "shop/shop-layout";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, HttpSession session, Model model) {
        SanPham product = sanPhamService.getByIdWithRelations(id);
        if (product == null) {
            return "redirect:/cua-hang";
        }

        List<SanPham> relatedProducts = new java.util.ArrayList<>();
        if (product.getDanhMucSanPham() != null) {
            relatedProducts = sanPhamService.search(null, 1, product.getDanhMucSanPham().getDanhMucSanPhamId())
                    .stream()
                    .filter(sp -> !sp.getId().equals(id))
                    .limit(4)
                    .collect(Collectors.toList());
        }

        List<Map<String, String>> breadcrumbItems = new ArrayList<>();
        Map<String, String> listCrumb = new HashMap<>();
        listCrumb.put("label", "Sản phẩm");
        listCrumb.put("url", "/cua-hang");
        breadcrumbItems.add(listCrumb);

        if (product.getDanhMucSanPham() != null) {
            Map<String, String> catCrumb = new HashMap<>();
            catCrumb.put("label", product.getDanhMucSanPham().getTenDanhMuc());
            catCrumb.put("url", "/cua-hang?danhMucId=" + product.getDanhMucSanPham().getDanhMucSanPhamId());
            breadcrumbItems.add(catCrumb);
        }

        Map<String, String> productCrumb = new HashMap<>();
        productCrumb.put("label", product.getTenSanPham());
        productCrumb.put("url", null);
        breadcrumbItems.add(productCrumb);

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("breadcrumbItems", breadcrumbItems);
        model.addAttribute("cartItemCount", gioHangService.getCartItemCount(session));
        model.addAttribute("paymentMethods", hinhThucThanhToanRepo.findAll());
        model.addAttribute("pageTitle", product.getTenSanPham() + " — Yonex Store");
        model.addAttribute("activeMenu", "products");
        model.addAttribute("pageCss", "/shop/css/shop-home.css");
        model.addAttribute("pageCss2", "/shop/css/shop-product-detail.css");
        model.addAttribute("content", "shop/product-detail");

        return "shop/shop-layout";
    }
}
