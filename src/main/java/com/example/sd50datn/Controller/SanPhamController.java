package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.NhaCungCapRepository;
import com.example.sd50datn.Service.DanhMucSanPhamService;
import com.example.sd50datn.Service.MauSacService;
import com.example.sd50datn.Service.NhapKhoService;
import com.example.sd50datn.Service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final DanhMucSanPhamService danhMucService;
    private final MauSacService mauSacService;
    private final NhapKhoService nhapKhoService;
    private final NhaCungCapRepository nhaCungCapRepo;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "trangThai", required = false) Integer trangThai,
                       @RequestParam(value = "danhMucId", required = false) Integer danhMucId,
                       @RequestParam(value = "editId", required = false) Integer editId,
                       @RequestParam(value = "openAdd", required = false, defaultValue = "0") int openAdd,
                       Model model) {

        List<SanPham> list = sanPhamService.search(q, trangThai, danhMucId);
        model.addAttribute("list", list);
        model.addAttribute("danhMucList", danhMucService.getAll());
        model.addAttribute("mauSacList", mauSacService.getAll());

        SanPham sp;
        if (editId != null) {
            sp = sanPhamService.getById(editId);
            model.addAttribute("openEdit", 1);
        } else {
            sp = new SanPham();
            sp.setTrangThai(1);
            model.addAttribute("openEdit", 0);
        }

        model.addAttribute("sp", sp);
        model.addAttribute("openAdd", openAdd);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("filterTrangThai", trangThai);
        model.addAttribute("filterDanhMucId", danhMucId);

        model.addAttribute("nhapKhoList", nhapKhoService.getAll());
        model.addAttribute("nhaCungCapList", nhaCungCapRepo.findByTrangThai(1));

        model.addAttribute("pageTitle", "Hàng hóa");
        model.addAttribute("pageHeading", "Hàng hóa");
        model.addAttribute("activeMenu", "sanpham");
        model.addAttribute("content", "SanPham/list");
        model.addAttribute("pageCss", "/css/sanpham.css");

        return "layout";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("sp") SanPham sp,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       @RequestParam(value = "imageUrl", required = false) String imageUrl,
                       @RequestParam(value = "saveAndNew", required = false, defaultValue = "0") int saveAndNew,
                       RedirectAttributes ra) {

        String error = sanPhamService.validateForSave(sp);
        if (error != null) {
            ra.addFlashAttribute("error", error);
            if (sp.getId() != null) {
                return "redirect:/san-pham?editId=" + sp.getId();
            }
            return "redirect:/san-pham?openAdd=1";
        }

        sanPhamService.saveWithImage(sp, imageFile, imageUrl);
        ra.addFlashAttribute("success", "Lưu hàng hóa thành công");

        if (saveAndNew == 1) {
            return "redirect:/san-pham?openAdd=1";
        }
        return "redirect:/san-pham";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        sanPhamService.delete(id);
        ra.addFlashAttribute("success", "Xóa hàng hóa thành công");
        return "redirect:/san-pham";
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportExcel(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "trangThai", required = false) Integer trangThai,
            @RequestParam(value = "danhMucId", required = false) Integer danhMucId) {

        List<SanPham> list = sanPhamService.search(q, trangThai, danhMucId);
        ByteArrayInputStream in = sanPhamService.exportToExcel(list);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Danh_sach_hang_hoa.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<SanPham>> apiSearch(
            @RequestParam(value = "q", required = false) String q) {
        List<SanPham> results = sanPhamService.search(q, null, null);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/generate-code")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateCode() {
        Map<String, String> result = new HashMap<>();
        result.put("maSanPham", sanPhamService.generateNextMaSanPham());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/generate-barcode")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateBarcode() {
        Map<String, String> result = new HashMap<>();
        result.put("barcode", sanPhamService.generateBarcode());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/generate-sku")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateSku(
            @RequestParam(value = "maSanPham", required = false) String maSanPham) {
        Map<String, String> result = new HashMap<>();
        result.put("sku", sanPhamService.generateSku(maSanPham));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/check-ma")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkMa(
            @RequestParam("ma") String ma,
            @RequestParam(value = "excludeId", required = false) Integer excludeId) {
        Map<String, Object> result = new HashMap<>();
        boolean exists;
        if (excludeId != null) {
            exists = sanPhamService.existsByMaSanPhamExcluding(ma, excludeId);
        } else {
            exists = sanPhamService.existsByMaSanPham(ma);
        }
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/check-barcode")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkBarcode(
            @RequestParam("barcode") String barcode,
            @RequestParam(value = "excludeId", required = false) Integer excludeId) {
        Map<String, Object> result = new HashMap<>();
        boolean exists;
        if (excludeId != null) {
            exists = sanPhamService.existsByBarcodeExcluding(barcode, excludeId);
        } else {
            exists = sanPhamService.existsByBarcode(barcode);
        }
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }
}
