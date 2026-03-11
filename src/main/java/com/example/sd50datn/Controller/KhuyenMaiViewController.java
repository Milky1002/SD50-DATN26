package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.ChuongTrinhKhuyenMaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/khuyen-mai")
@RequiredArgsConstructor
public class KhuyenMaiViewController {

    private final ChuongTrinhKhuyenMaiService service;

    @GetMapping
    public String khuyenMaiPage(Model model) {
        model.addAttribute("pageTitle", "Quản Lý Khuyến Mại");
        model.addAttribute("pageHeading", "Nghiệp vụ khác");
        model.addAttribute("activeMenu", "khuyenmai");
        model.addAttribute("content", "khuyen-mai/index");
        model.addAttribute("pageCss", "/css/khuyen-mai.css");
        return "layout";
    }
}
