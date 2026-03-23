package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.NhanVienHoatDong;
import com.example.sd50datn.Service.NhanVienHoatDongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/lich-su-nhan-vien")
@RequiredArgsConstructor
public class LichSuNhanVienController {

    private final NhanVienHoatDongService nhanVienHoatDongService;

    @GetMapping
    public String index(@RequestParam(required = false) Integer nhanVienId,
                        Model model) {
        List<NhanVienHoatDong> list = nhanVienId != null
                ? nhanVienHoatDongService.findByNhanVien(nhanVienId)
                : nhanVienHoatDongService.findAll();

        model.addAttribute("list", list);
        model.addAttribute("filterNhanVienId", nhanVienId);
        model.addAttribute("pageTitle", "Lịch sử hoạt động nhân viên");
        model.addAttribute("pageHeading", "Lịch sử hoạt động nhân viên");
        model.addAttribute("activeMenu", "lichsunhanvien");
        model.addAttribute("content", "LichSuNhanVien/index");
        return "layout";
    }
}
