package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.DanhMucSanPhamService;
import com.example.sd50datn.Service.HomepageConfigService;
import com.example.sd50datn.Service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trang-chu-config")
@RequiredArgsConstructor
public class HomepageConfigController {

    private final HomepageConfigService homepageConfigService;
    private final SanPhamService sanPhamService;
    private final DanhMucSanPhamService danhMucSanPhamService;

    @GetMapping
    public String index(Model model) {
        List<Integer> selectedHotIds = homepageConfigService.getHotConfigs().stream()
                .map(cfg -> cfg.getSanPham().getId())
                .collect(Collectors.toList());
        List<Integer> selectedCategoryIds = homepageConfigService.getFeaturedCategoryConfigs().stream()
                .map(cfg -> cfg.getDanhMucSanPham().getDanhMucSanPhamId())
                .collect(Collectors.toList());

        model.addAttribute("sanPhamList", sanPhamService.search(null, 1, null));
        model.addAttribute("danhMucList", danhMucSanPhamService.getAll().stream()
                .filter(dm -> Integer.valueOf(1).equals(dm.getTrangThai()))
                .collect(Collectors.toList()));
        model.addAttribute("selectedHotIds", selectedHotIds);
        model.addAttribute("selectedCategoryIds", selectedCategoryIds);
        model.addAttribute("selectedHotCount", selectedHotIds.size());
        model.addAttribute("selectedCategoryCount", selectedCategoryIds.size());

        model.addAttribute("pageTitle", "Cấu hình trang chủ");
        model.addAttribute("pageHeading", "Cấu hình trang chủ");
        model.addAttribute("activeMenu", "trangchuconfig");
        model.addAttribute("content", "HomepageConfig/list");
        model.addAttribute("pageCss", "/css/homepage-config.css");
        return "layout";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "hotProductIds", required = false) List<Integer> hotProductIds,
                       @RequestParam(value = "featuredCategoryIds", required = false) List<Integer> featuredCategoryIds,
                       @RequestParam(value = "_hotSection", required = false) String hotSectionPresent,
                       @RequestParam(value = "_categorySection", required = false) String categorySectionPresent,
                       RedirectAttributes redirectAttributes) {
        // Hidden markers tell us whether each section was rendered in the form.
        // If the marker is present, the admin intentionally submitted that section
        // (even with nothing checked → means "clear this section").
        // If the marker is absent, the section wasn't part of this submit → don't touch it.
        boolean hotSubmitted = hotSectionPresent != null;
        boolean catSubmitted = categorySectionPresent != null;

        if (!hotSubmitted && !catSubmitted) {
            redirectAttributes.addFlashAttribute("error",
                    "Không có dữ liệu cấu hình nào được gửi. Cấu hình hiện tại được giữ nguyên.");
            return "redirect:/trang-chu-config";
        }

        if (hotSubmitted) {
            homepageConfigService.saveHotProducts(hotProductIds);
        }
        if (catSubmitted) {
            homepageConfigService.saveFeaturedCategories(featuredCategoryIds);
        }

        redirectAttributes.addFlashAttribute("success", "Đã lưu cấu hình trang chủ");
        return "redirect:/trang-chu-config";
    }
}
