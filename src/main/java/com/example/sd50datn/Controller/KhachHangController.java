package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Repository.KhachHangRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/khachhang")
public class KhachHangController {

    @Autowired
    KhachHangRepository repo;

    @GetMapping("/hienthi")
    public String hienThi(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "trangThai", required = false) Integer trangThai,
                          Model model) {

        List<KhachHang> list;
        if (keyword != null && !keyword.trim().isEmpty() && trangThai != null) {
            list = repo.findByTenKhachHangContaining(keyword.trim());
            list = list.stream().filter(kh -> kh.getTrangThai().equals(trangThai)).toList();
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            list = repo.findByTenKhachHangContaining(keyword.trim());
        } else if (trangThai != null) {
            list = repo.findByTrangThai(trangThai);
        } else {
            list = repo.findAll();
        }

        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("kh", new KhachHang());

        model.addAttribute("pageTitle", "Khách hàng");
        model.addAttribute("pageHeading", "Quản lý khách hàng");
        model.addAttribute("activeMenu", "khachhang");
        model.addAttribute("content", "khach");
        model.addAttribute("pageCss", "/css/khach.css");
        return "layout";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("kh") KhachHang kh,
                       BindingResult result,
                       Model model) {

        if (result.hasErrors()) {
            model.addAttribute("list", repo.findAll());
            model.addAttribute("pageTitle", "Khách hàng");
            model.addAttribute("pageHeading", "Quản lý khách hàng");
            model.addAttribute("activeMenu", "khachhang");
            model.addAttribute("content", "khach");
            model.addAttribute("pageCss", "/css/khach.css");
            model.addAttribute("openModal", true);
            return "layout";
        }

        if (kh.getKhachHangId() != null) {
            Optional<KhachHang> opt = repo.findById(kh.getKhachHangId());
            if (opt.isPresent()) {
                KhachHang old = opt.get();
                old.setTenKhachHang(kh.getTenKhachHang());
                old.setSdt(kh.getSdt());
                old.setEmail(kh.getEmail());
                old.setDiaChiKhachHang(kh.getDiaChiKhachHang());
                old.setTrangThai(kh.getTrangThai());
                old.setNgayCapNhat(LocalDateTime.now());
                repo.save(old);
            }
        } else {
            kh.setNgayTao(LocalDateTime.now());
            kh.setNgayCapNhat(LocalDateTime.now());
            repo.save(kh);
        }

        return "redirect:/khachhang/hienthi";
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public KhachHang edit(@PathVariable Integer id) {
        return repo.findById(id).orElse(null);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return "redirect:/khachhang/hienthi";
    }

    // Keep old endpoints for backward compatibility
    @GetMapping("/search")
    public String search(@RequestParam("ten") String ten, Model model) {
        return "redirect:/khachhang/hienthi?keyword=" + ten;
    }

    @GetMapping("/status")
    public String status(@RequestParam(required = false) Integer trangThai, Model model) {
        if (trangThai == null) {
            return "redirect:/khachhang/hienthi";
        }
        return "redirect:/khachhang/hienthi?trangThai=" + trangThai;
    }
}
