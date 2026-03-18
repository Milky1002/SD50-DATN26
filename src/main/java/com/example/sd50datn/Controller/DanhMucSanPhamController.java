package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.DanhMucSanPham;
import com.example.sd50datn.Service.DanhMucSanPhamService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/danh-muc")
public class DanhMucSanPhamController {

    private final DanhMucSanPhamService service;

    public DanhMucSanPhamController(DanhMucSanPhamService service) {
        this.service = service;
    }

    // LIST
    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "openAdd", required = false, defaultValue = "0") int openAdd,
                       @RequestParam(value = "editId", required = false) Integer editId,
                       Model model) {

        DanhMucSanPham dm;
        model.addAttribute("list", service.search(q));
        if (editId != null) {
            dm = service.getById(editId);
            model.addAttribute("openEdit", 1);
        } else {
            dm = new DanhMucSanPham();
            dm.setTrangThai(1);
            model.addAttribute("openEdit", 0);
        }

        model.addAttribute("dm", dm);

        model.addAttribute("q", q == null ? "" : q);

        model.addAttribute("pageTitle", "Danh mục sản phẩm");
        model.addAttribute("pageHeading", "Quản lý danh mục sản phẩm");
        model.addAttribute("activeMenu", "danhmuc");// active ở đây nhé mấy con gà


        model.addAttribute("content", "DanhMuc/list");

        model.addAttribute("pageCss", "/css/danhmuc.css");

        return "layout";
    }
    // SHOW FORM ADD
    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("dm") DanhMucSanPham dm,
            BindingResult result,
            Model model,
            @RequestParam(value = "continue", required = false, defaultValue = "0") int cont) {

        if (service.isTenDanhMucExistsForSave(dm)) {
            result.rejectValue("tenDanhMuc", "error.dm", "Tên danh mục đã tồn tại");
        }

        if (result.hasErrors()) {
            model.addAttribute("list", service.getAll());

            boolean isEdit = dm.getDanhMucSanPhamId() != null;
            model.addAttribute("openAdd", isEdit ? 0 : 1);
            model.addAttribute("openEdit", isEdit ? 1 : 0);

            model.addAttribute("q", "");
            model.addAttribute("pageTitle", "Danh mục sản phẩm");
            model.addAttribute("pageHeading", "Quản lý danh mục sản phẩm");
            model.addAttribute("activeMenu", "danhmuc");
            model.addAttribute("content", "DanhMuc/list");
            model.addAttribute("pageCss", "/css/danhmuc.css");

            return "layout";
        }

        service.save(dm);

        if (cont == 1) {
            return "redirect:/danh-muc?openAdd=1";
        }

        return "redirect:/danh-muc";
    }
    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/danh-muc";
    }
}