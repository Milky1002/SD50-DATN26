package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.PhieuNhap;
import com.example.sd50datn.Repository.NhaCungCapRepository;
import com.example.sd50datn.Service.NhapKhoService;
import com.example.sd50datn.Service.SanPhamService;
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
@RequestMapping("/nhap-kho")
@RequiredArgsConstructor
public class NhapKhoController {

    private final NhapKhoService nhapKhoService;
    private final SanPhamService sanPhamService;
    private final NhaCungCapRepository nhaCungCapRepo;

    @GetMapping
    public String list(@RequestParam(value = "detailId", required = false) Integer detailId,
                       Model model) {
        model.addAttribute("list", nhapKhoService.getAll());

        if (detailId != null) {
            PhieuNhap pn = nhapKhoService.getById(detailId);
            model.addAttribute("detail", pn);
            model.addAttribute("detailItems", nhapKhoService.getChiTiet(detailId));
            model.addAttribute("openDetail", 1);
        } else {
            model.addAttribute("openDetail", 0);
        }

        model.addAttribute("sanPhamList", sanPhamService.search(null, 1, null));
        model.addAttribute("nhaCungCapList", nhaCungCapRepo.findByTrangThai(1));
        model.addAttribute("pageTitle", "Nhap kho");
        model.addAttribute("pageHeading", "Quan ly nhap kho");
        model.addAttribute("activeMenu", "nhapkho");
        model.addAttribute("content", "NhapKho/list");
        model.addAttribute("pageCss", "/css/nhapkho.css");
        return "layout";
    }

    @PostMapping("/save")
    public String save(@RequestParam("nhaCungCapId") Integer nhaCungCapId,
                       @RequestParam("sanPhamIds") List<Integer> sanPhamIds,
                       @RequestParam("soLuongs") List<Integer> soLuongs,
                       @RequestParam("donGiaNhaps") List<BigDecimal> donGiaNhaps,
                       @RequestParam(value = "ghiChu", required = false) String ghiChu,
                       RedirectAttributes ra) {
        try {
            PhieuNhap pn = nhapKhoService.createPhieuNhap(nhaCungCapId, ghiChu, sanPhamIds, soLuongs, donGiaNhaps);
            ra.addFlashAttribute("success", "Tạo phiếu nhập " + pn.getMaPhieuNhap() + " thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/nhap-kho";
    }
}
