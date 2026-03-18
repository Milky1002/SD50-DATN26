package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Service.BanHangService;
import com.example.sd50datn.Service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ban-hang")
@RequiredArgsConstructor
public class BanHangController {

    private final SanPhamService sanPhamService;
    private final BanHangService banHangService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Bán hàng tại quầy");
        model.addAttribute("pageHeading", "Bán hàng tại quầy");
        model.addAttribute("activeMenu", "banhang");
        model.addAttribute("content", "BanHang/index");
        model.addAttribute("pageCss", "/css/banhang.css");
        return "layout";
    }

    @GetMapping("/api/tim-san-pham")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> timSanPham(
            @RequestParam(value = "q", required = false) String q) {
        List<SanPham> results = sanPhamService.search(q, 1, null);
        List<Map<String, Object>> data = results.stream().map(sp -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", sp.getId());
            m.put("maSanPham", sp.getMaSanPham());
            m.put("tenSanPham", sp.getTenSanPham());
            m.put("giaBan", sp.getGiaBan());
            m.put("soLuongTon", sp.getSoLuongTon());
            m.put("barcode", sp.getBarcode());
            m.put("sku", sp.getSku());
            m.put("danhMuc", sp.getDanhMucSanPham() != null ? sp.getDanhMucSanPham().getTenDanhMuc() : "");
            return m;
        }).toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Object> request) {
        try {
            String tenKhachHang = (String) request.get("tenKhachHang");
            String sdtKhachHang = (String) request.get("sdtKhachHang");
            String ghiChu = (String) request.get("ghiChu");
            String phuongThucThanhToan = (String) request.get("phuongThucThanhToan");
            int tienKhachDua = request.get("tienKhachDua") != null ? ((Number) request.get("tienKhachDua")).intValue() : 0;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            HoaDon hoaDon = banHangService.checkout(tenKhachHang, sdtKhachHang, ghiChu, phuongThucThanhToan, tienKhachDua, items);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("hoaDonId", hoaDon.getId());
            result.put("tongTien", hoaDon.getTongTienSauKhiGiam());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
