package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Service.BanHangService;
import com.example.sd50datn.Service.CaLamViecService;
import com.example.sd50datn.Service.KhachHangService;
import com.example.sd50datn.Service.NhanVienHoatDongService;
import com.example.sd50datn.Service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/ban-hang")
@RequiredArgsConstructor
public class BanHangController {

    private final SanPhamService sanPhamService;
    private final BanHangService banHangService;
    private final KhachHangService khachHangService;
    private final NhanVienHoatDongService nhanVienHoatDongService;
    private final CaLamViecService caLamViecService;
    private final Validator validator;

    @GetMapping
    public String index(Model model, HttpSession session) {
        String roleCode = (String) session.getAttribute("roleCode");
        String legacyRole = (String) session.getAttribute("role");
        boolean isStaff = "STAFF".equalsIgnoreCase(roleCode)
                || (legacyRole != null && (legacyRole.equalsIgnoreCase("Nhân viên") || legacyRole.equalsIgnoreCase("Nhan vien")));
        if (isStaff) {
            Integer nhanVienId = (Integer) session.getAttribute("nhanVienId");
            if (nhanVienId == null || caLamViecService.getCaDangMo(nhanVienId).isEmpty()) {
                return "redirect:/cham-cong?error=chua_cham_cong";
            }
        }
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

    @GetMapping("/api/tim-khach-hang")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> timKhachHang(
            @RequestParam(value = "q", required = false) String q) {
        List<Map<String, Object>> data = khachHangService.searchForPos(q).stream()
                .map(this::toCustomerPayload)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/api/khach-hang")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> taoKhachHang(@RequestBody Map<String, Object> request,
                                                            HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        KhachHang kh = new KhachHang();
        kh.setTenKhachHang(readString(request.get("tenKhachHang")));
        kh.setSdt(readString(request.get("sdt")));
        kh.setEmail(readString(request.get("email")));
        kh.setDiaChiKhachHang(readString(request.get("diaChiKhachHang")));
        kh.setTrangThai(1);
        kh.setNgayTao(LocalDateTime.now());
        kh.setNgayCapNhat(LocalDateTime.now());

        Set<ConstraintViolation<KhachHang>> violations = validator.validate(kh);
        if (!violations.isEmpty()) {
            result.put("success", false);
            result.put("message", violations.iterator().next().getMessage());
            return ResponseEntity.badRequest().body(result);
        }

        if (khachHangService.existsByPhone(kh.getSdt())) {
            result.put("success", false);
            result.put("message", "Số điện thoại đã tồn tại trong hệ thống");
            return ResponseEntity.badRequest().body(result);
        }

        if (khachHangService.existsByEmail(kh.getEmail())) {
            result.put("success", false);
            result.put("message", "Email đã tồn tại trong hệ thống");
            return ResponseEntity.badRequest().body(result);
        }

        KhachHang saved = khachHangService.createForPos(kh);

        Integer nhanVienId = (Integer) session.getAttribute("nhanVienId");
        String hoTen = (String) session.getAttribute("hoTen");
        nhanVienHoatDongService.log(
                nhanVienId,
                hoTen,
                "KH_TAO",
                "KHACH_HANG",
                saved.getKhachHangId(),
                "Thêm khách hàng tại quầy: " + saved.getTenKhachHang() + " - " + saved.getSdt(),
                null
        );

        result.put("success", true);
        result.put("customer", toCustomerPayload(saved));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Object> request,
                                                        HttpSession session) {
        try {
            Integer nhanVienId = (Integer) session.getAttribute("nhanVienId");
            String roleCode = (String) session.getAttribute("roleCode");
            String legacyRole = (String) session.getAttribute("role");
            boolean isStaff = "STAFF".equalsIgnoreCase(roleCode)
                    || (legacyRole != null && (legacyRole.equalsIgnoreCase("Nhân viên") || legacyRole.equalsIgnoreCase("Nhan vien")));
            if (isStaff && (nhanVienId == null || caLamViecService.getCaDangMo(nhanVienId).isEmpty())) {
                Map<String, Object> notCheckedIn = new HashMap<>();
                notCheckedIn.put("success", false);
                notCheckedIn.put("needCheckin", true);
                notCheckedIn.put("redirect", "/cham-cong");
                notCheckedIn.put("message", "Vui lòng chấm công trước khi bán hàng.");
                return ResponseEntity.status(403).body(notCheckedIn);
            }

            String tenKhachHang = (String) request.get("tenKhachHang");
            String sdtKhachHang = (String) request.get("sdtKhachHang");
            String ghiChu = (String) request.get("ghiChu");
            String phuongThucThanhToan = (String) request.get("phuongThucThanhToan");
            int tienKhachDua = request.get("tienKhachDua") != null ? ((Number) request.get("tienKhachDua")).intValue() : 0;
            Integer promotionId = request.get("promotionId") != null ? ((Number) request.get("promotionId")).intValue() : null;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            String hoTenNhanVien = (String) session.getAttribute("hoTen");

            HoaDon hoaDon = banHangService.checkout(
                    tenKhachHang, sdtKhachHang, ghiChu, phuongThucThanhToan,
                    tienKhachDua, items, promotionId,
                    nhanVienId, hoTenNhanVien);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("hoaDonId", hoaDon.getId());
            result.put("tongTien", hoaDon.getTongTienSauKhiGiam());
            result.put("isTransfer", "transfer".equalsIgnoreCase(phuongThucThanhToan));
            result.put("vietQrUrl", "https://img.vietqr.io/image/vietinbank-101878509895-compact2.jpg?amount="
                    + hoaDon.getTongTienSauKhiGiam().longValue()
                    + "&addInfo=SEVQR%20POS%20" + hoaDon.getId()
                    + "&accountName=Vu%20Bao%20Linh");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{hoaDonId}/xac-nhan-chuyen-khoan")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmTransfer(@PathVariable Integer hoaDonId) {
        try {
            banHangService.confirmTransferPayment(hoaDonId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    private Map<String, Object> toCustomerPayload(KhachHang kh) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", kh.getKhachHangId());
        item.put("tenKhachHang", kh.getTenKhachHang());
        item.put("sdt", kh.getSdt());
        item.put("email", kh.getEmail());
        item.put("diaChiKhachHang", kh.getDiaChiKhachHang());
        return item;
    }

    private String readString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }
}
