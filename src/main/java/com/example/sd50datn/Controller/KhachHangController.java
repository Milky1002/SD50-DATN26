package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Model.Account;
import com.example.sd50datn.Repository.AccountRepository;
import com.example.sd50datn.Repository.KhachHangRepository;
import com.example.sd50datn.Service.NhanVienHoatDongService;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    NhanVienHoatDongService nhanVienHoatDongService;

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
            list = repo.findAllWithTaiKhoan();
        }

        model.addAttribute("list", list);
        model.addAttribute("linkedAccountCount", list.stream().filter(KhachHang::hasLinkedAccount).count());
        model.addAttribute("offlineCustomerCount", list.stream().filter(kh -> !kh.hasLinkedAccount()).count());
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
                       Model model,
                       HttpSession session) {

        if (result.hasErrors()) {
            List<KhachHang> customers = repo.findAllWithTaiKhoan();
            model.addAttribute("list", customers);
            model.addAttribute("linkedAccountCount", customers.stream().filter(KhachHang::hasLinkedAccount).count());
            model.addAttribute("offlineCustomerCount", customers.stream().filter(customer -> !customer.hasLinkedAccount()).count());
            model.addAttribute("pageTitle", "Khách hàng");
            model.addAttribute("pageHeading", "Quản lý khách hàng");
            model.addAttribute("activeMenu", "khachhang");
            model.addAttribute("content", "khach");
            model.addAttribute("pageCss", "/css/khach.css");
            model.addAttribute("openModal", true);
            return "layout";
        }

        Integer nhanVienId = (Integer) session.getAttribute("nhanVienId");
        String hoTen = (String) session.getAttribute("hoTen");

        if (kh.getKhachHangId() != null) {
            Optional<KhachHang> opt = repo.findById(kh.getKhachHangId());
            if (opt.isPresent()) {
                KhachHang old = opt.get();

                Account linkedByEmail = null;
                if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                    linkedByEmail = accountRepository.findByEmail(kh.getEmail().trim()).orElse(null);
                }

                old.setTenKhachHang(kh.getTenKhachHang());
                old.setSdt(kh.getSdt());
                old.setEmail(kh.getEmail());
                old.setDiaChiKhachHang(kh.getDiaChiKhachHang());
                old.setTrangThai(kh.getTrangThai());
                if (linkedByEmail != null) {
                    old.setTaiKhoanId(linkedByEmail.getId());
                }
                old.setNgayCapNhat(LocalDateTime.now());
                repo.save(old);

                nhanVienHoatDongService.log(
                        nhanVienId, hoTen,
                        "KH_SUA", "KHACH_HANG", old.getKhachHangId(),
                        "Sửa thông tin khách: " + old.getTenKhachHang(), null);
            }
        } else {
            if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                accountRepository.findByEmail(kh.getEmail().trim()).ifPresent(account -> kh.setTaiKhoanId(account.getId()));
            }
            kh.setNgayTao(LocalDateTime.now());
            kh.setNgayCapNhat(LocalDateTime.now());
            repo.save(kh);

            nhanVienHoatDongService.log(
                    nhanVienId, hoTen,
                    "KH_TAO", "KHACH_HANG", kh.getKhachHangId(),
                    "Thêm mới khách hàng: " + kh.getTenKhachHang(), null);
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
