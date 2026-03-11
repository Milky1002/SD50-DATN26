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
import java.util.Optional;

@Controller
@RequestMapping("/khachhang")
public class KhachHangController {

    @Autowired
    KhachHangRepository repo;

    //  HIỂN THỊ
    @GetMapping("/hienthi")
    public String hienThi(Model model){

        model.addAttribute("list", repo.findAll());

        model.addAttribute("pageTitle","Khách hàng");
        model.addAttribute("pageHeading","Quản lý khách hàng");

        model.addAttribute("activeMenu", "khachhang");
        model.addAttribute("content", "khach");
        model.addAttribute("pageCss", "/css/khach.css");
        return "layout";
    }

    // VIEW ADD
    @GetMapping("/add")
    public String viewAdd(Model model){

        model.addAttribute("kh", new KhachHang());

        model.addAttribute("pageTitle","Thêm khách hàng");
        model.addAttribute("pageHeading","Thêm khách hàng");
        model.addAttribute("content","add-khach");

        return "layout";
    }

    //
    // ADD
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("kh") KhachHang kh,
                      BindingResult result,
                      Model model){

        if(result.hasErrors()){
            model.addAttribute("pageTitle","Thêm khách hàng");
            model.addAttribute("pageHeading","Thêm khách hàng");
            model.addAttribute("content","add-khach");
            return "layout";
        }

        kh.setNgayTao(LocalDateTime.now());
        kh.setNgayCapNhat(LocalDateTime.now());

        repo.save(kh);

        return "redirect:/khachhang/hienthi";
    }

    // VIEW UPDATE
    @GetMapping("/update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model){

        Optional<KhachHang> optional = repo.findById(id);

        if(optional.isPresent()){
            model.addAttribute("kh", optional.get());
        }else{
            return "redirect:/khachhang/hienthi";
        }

        model.addAttribute("pageTitle","Cập nhật khách hàng");
        model.addAttribute("pageHeading","Cập nhật khách hàng");
        model.addAttribute("content","update-khach");

        return "layout";
    }

    //  UPDATE
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("kh") KhachHang kh,
                         BindingResult result,
                         Model model){

        if(result.hasErrors()){
            model.addAttribute("pageTitle","Cập nhật khách hàng");
            model.addAttribute("pageHeading","Cập nhật khách hàng");
            model.addAttribute("content","update-khach");
            return "layout";
        }

        Optional<KhachHang> optional = repo.findById(kh.getKhachHangId());

        if(optional.isPresent()){

            KhachHang old = optional.get();

            old.setTenKhachHang(kh.getTenKhachHang());
            old.setSdt(kh.getSdt());
            old.setEmail(kh.getEmail());
            old.setDiaChiKhachHang(kh.getDiaChiKhachHang());
            old.setTrangThai(kh.getTrangThai());

            // chỉ cập nhật ngày cập nhật
            old.setNgayCapNhat(LocalDateTime.now());

            repo.save(old);
        }

        return "redirect:/khachhang/hienthi";
    }

    //  DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){

        repo.deleteById(id);

        return "redirect:/khachhang/hienthi";
    }

    // tìm kiếm
    @GetMapping("/search")
    public String search(@RequestParam("ten") String ten,
                         Model model){

        model.addAttribute("list", repo.findByTenKhachHangContaining(ten));

        model.addAttribute("pageTitle","Khách hàng");
        model.addAttribute("pageHeading","Quản lý khách hàng");
        model.addAttribute("content","khach");

        return "layout";
    }

    // trang thai
    @GetMapping("/status")
    public String status(@RequestParam(required = false) Integer trangThai,
                         Model model){

        if(trangThai == null){
            model.addAttribute("list", repo.findAll());
        }else{
            model.addAttribute("list", repo.findByTrangThai(trangThai));
        }

        model.addAttribute("pageTitle","Khách hàng");
        model.addAttribute("pageHeading","Quản lý khách hàng");
        model.addAttribute("content","khach");

        return "layout";
    }

}