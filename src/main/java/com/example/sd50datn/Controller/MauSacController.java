package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.MauSac;
import com.example.sd50datn.Service.MauSacService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mau-sac")
public class MauSacController {

    private final MauSacService service;

    public MauSacController(MauSacService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "openAdd", required = false, defaultValue = "0") int openAdd,
                       @RequestParam(value = "editId", required = false) Integer editId,
                       Model model) {

        try {
            model.addAttribute("list", service.search(q));
        } catch (Exception e) {
            model.addAttribute("list", java.util.Collections.emptyList());
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
        model.addAttribute("q", q == null ? "" : q);

        MauSac mauSac;
        if (editId != null) {
            try {
                mauSac = service.getById(editId);
            } catch (Exception e) {
                mauSac = null;
                model.addAttribute("error", "Lỗi khi tải màu sắc: " + e.getMessage());
            }
            if (mauSac == null) {
                mauSac = new MauSac();
                mauSac.setTrangThai(1);
                mauSac.setMaMauHex("#000000");
                model.addAttribute("openEdit", 0);
            } else {
                if (mauSac.getMaMauHex() == null || mauSac.getMaMauHex().isBlank()) {
                    mauSac.setMaMauHex("#000000");
                }
                model.addAttribute("openEdit", 1);
            }
        } else {
            mauSac = new MauSac();
            mauSac.setTrangThai(1);
            mauSac.setMaMauHex("#000000");
            model.addAttribute("openEdit", 0);
        }

        model.addAttribute("mauSac", mauSac);
        model.addAttribute("openAdd", openAdd);

        model.addAttribute("pageTitle", "Màu sắc");
        model.addAttribute("pageHeading", "Quản lý màu sắc");
        model.addAttribute("activeMenu", "mausac");
        model.addAttribute("content", "MauSac/list");
        model.addAttribute("pageCss", "/css/mausac.css");

        return "layout";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Integer id) {
        return "redirect:/mau-sac?editId=" + id;
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("mauSac") MauSac mauSac,
                       BindingResult result,
                       Model model,
                       @RequestParam(value = "continue", required = false, defaultValue = "0") int cont) {

        if (service.isTenMauExistsForSave(mauSac)) {
            result.rejectValue("tenMau", "error.mauSac", "Tên màu đã tồn tại");
        }

        if (result.hasErrors()) {
            try {
                model.addAttribute("list", service.getAll());
            } catch (Exception e) {
                model.addAttribute("list", java.util.Collections.emptyList());
            }

            boolean isEdit = mauSac.getMauSacId() != null;
            model.addAttribute("openAdd", isEdit ? 0 : 1);
            model.addAttribute("openEdit", isEdit ? 1 : 0);

            model.addAttribute("q", "");
            model.addAttribute("pageTitle", "Màu sắc");
            model.addAttribute("pageHeading", "Quản lý màu sắc");
            model.addAttribute("activeMenu", "mausac");
            model.addAttribute("content", "MauSac/list");
            model.addAttribute("pageCss", "/css/mausac.css");

            return "layout";
        }

        service.save(mauSac);

        if (cont == 1) {
            return "redirect:/mau-sac?openAdd=1";
        }

        return "redirect:/mau-sac";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/mau-sac";
    }
}