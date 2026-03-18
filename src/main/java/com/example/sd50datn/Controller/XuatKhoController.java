package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.PhieuXuat;
import com.example.sd50datn.Service.SanPhamService;
import com.example.sd50datn.Service.XuatKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/xuat-kho")
@RequiredArgsConstructor
public class XuatKhoController {

    private final XuatKhoService xuatKhoService;
    private final SanPhamService sanPhamService;

    @GetMapping
    public String list(@RequestParam(value = "detailId", required = false) Integer detailId,
                       Model model) {

        model.addAttribute("list", xuatKhoService.getAll());

        if (detailId != null) {
            PhieuXuat px = xuatKhoService.getById(detailId);
            model.addAttribute("detail", px);
            model.addAttribute("detailItems", xuatKhoService.getChiTiet(detailId));
            model.addAttribute("openDetail", 1);
        } else {
            model.addAttribute("openDetail", 0);
        }

        model.addAttribute("sanPhamList", sanPhamService.search(null, 1, null));

        model.addAttribute("pageTitle", "Xuat kho");
        model.addAttribute("pageHeading", "Quan ly xuat kho");
        model.addAttribute("activeMenu", "xuatkho");
        model.addAttribute("content", "XuatKho/list");
        model.addAttribute("pageCss", "/css/xuatkho.css");

        return "layout";
    }

    @PostMapping("/save")
    public String save(@RequestParam("sanPhamIds") List<Integer> sanPhamIds,
                       @RequestParam("soLuongs") List<Integer> soLuongs,
                       @RequestParam(value = "lyDo", required = false) String lyDo,
                       @RequestParam(value = "ghiChu", required = false) String ghiChu,
                       RedirectAttributes ra) {
        try {
            PhieuXuat px = xuatKhoService.createPhieuXuat(lyDo, ghiChu, sanPhamIds, soLuongs);
            ra.addFlashAttribute("success", "Tao phieu xuat " + px.getMaPhieuXuat() + " thanh cong");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/xuat-kho";
    }
}
