package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.PhieuNhap;
import com.example.sd50datn.Service.NhapKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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

    // GET /nhap-kho is now merged into /san-pham — redirect there
    @GetMapping
    public String list() {
        return "redirect:/san-pham";
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
        return "redirect:/san-pham";
    }
}
