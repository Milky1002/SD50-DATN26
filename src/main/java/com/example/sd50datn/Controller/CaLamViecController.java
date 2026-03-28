package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.CaLamViec;
import com.example.sd50datn.Service.CaLamViecService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class CaLamViecController {

    private final CaLamViecService caLamViecService;

    // ================================================================
    //  API JSON — check-in / check-out / trạng thái ca
    // ================================================================

    /** Lấy trạng thái ca hiện tại của nhân viên đang login */
    @GetMapping("/api/ca-lam-viec/trang-thai")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> trangThai(HttpSession session) {
        Integer nhanVienId = (Integer) session.getAttribute("nhanVienId");
        if (nhanVienId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        Optional<CaLamViec> optCa = caLamViecService.getCaDangMo(nhanVienId);
        Map<String, Object> result = new LinkedHashMap<>();

        if (optCa.isPresent()) {
            CaLamViec ca = optCa.get();
            result.put("dangLamViec", true);
            result.put("caLamViecId", ca.getId());
            result.put("thoiGianBatDau", ca.getThoiGianBatDau().toString());
            result.putAll(caLamViecService.getThongKeCaDangMo(ca));
        } else {
            result.put("dangLamViec", false);
        }

        return ResponseEntity.ok(result);
    }

    /** Bắt đầu ca làm việc (check-in) */
    @PostMapping("/api/ca-lam-viec/bat-dau")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> batDau(HttpSession session) {
        Integer nhanVienId = (Integer) session.getAttribute("nhanVienId");
        String hoTen = (String) session.getAttribute("hoTen");
        if (nhanVienId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        CaLamViec ca = caLamViecService.batDauCa(nhanVienId, hoTen);

        // Lưu ca vào session để tiện truy cập
        session.setAttribute("caLamViecId", ca.getId());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("caLamViecId", ca.getId());
        result.put("thoiGianBatDau", ca.getThoiGianBatDau().toString());
        return ResponseEntity.ok(result);
    }

    /** Kết thúc ca làm việc (check-out) */
    @PostMapping("/api/ca-lam-viec/ket-thuc")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ketThuc(
            @RequestBody(required = false) Map<String, Object> body,
            HttpSession session) {
        Integer nhanVienId = (Integer) session.getAttribute("nhanVienId");
        String hoTen = (String) session.getAttribute("hoTen");
        if (nhanVienId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        String ghiChu = null;
        if (body != null && body.get("ghiChu") != null) {
            ghiChu = body.get("ghiChu").toString();
        }

        try {
            CaLamViec ca = caLamViecService.ketThucCa(nhanVienId, hoTen, ghiChu);
            session.removeAttribute("caLamViecId");

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("caLamViecId", ca.getId());
            result.put("thoiGianBatDau", ca.getThoiGianBatDau().toString());
            result.put("thoiGianKetThuc", ca.getThoiGianKetThuc().toString());
            result.put("tongHoaDon", ca.getTongHoaDon());
            result.put("tongSanPham", ca.getTongSanPham());
            result.put("tongTien", ca.getTongTien());
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ================================================================
    //  Trang chấm công (MVC view) — Admin + Staff đều truy cập
    // ================================================================

    @GetMapping("/cham-cong")
    public String chamCong(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Integer nhanVienId,
            Model model, HttpSession session) {

        String roleCode = (String) session.getAttribute("roleCode");
        Integer currentNhanVienId = (Integer) session.getAttribute("nhanVienId");

        // Mặc định: 7 ngày gần nhất
        LocalDate dateTo = (to != null && !to.isBlank())
                ? LocalDate.parse(to, DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now();
        LocalDate dateFrom = (from != null && !from.isBlank())
                ? LocalDate.parse(from, DateTimeFormatter.ISO_LOCAL_DATE) : dateTo.minusDays(7);

        List<CaLamViec> danhSachCa;

        if ("ADMIN".equals(roleCode)) {
            // Admin xem tất cả hoặc lọc theo nhân viên
            if (nhanVienId != null) {
                danhSachCa = caLamViecService.getByNhanVienAndDateRange(nhanVienId, dateFrom, dateTo);
            } else {
                danhSachCa = caLamViecService.getAllByDateRange(dateFrom, dateTo);
            }
        } else {
            // Staff chỉ xem ca của chính mình
            danhSachCa = caLamViecService.getByNhanVienAndDateRange(currentNhanVienId, dateFrom, dateTo);
        }

        // Tính tổng thống kê
        int totalCa = danhSachCa.size();
        int totalHoaDon = danhSachCa.stream().mapToInt(c -> c.getTongHoaDon() != null ? c.getTongHoaDon() : 0).sum();
        int totalSanPham = danhSachCa.stream().mapToInt(c -> c.getTongSanPham() != null ? c.getTongSanPham() : 0).sum();
        java.math.BigDecimal totalTien = danhSachCa.stream()
                .map(c -> c.getTongTien() != null ? c.getTongTien() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("danhSachCa", danhSachCa);
        model.addAttribute("dateFrom", dateFrom.toString());
        model.addAttribute("dateTo", dateTo.toString());
        model.addAttribute("filterNhanVienId", nhanVienId);
        model.addAttribute("totalCa", totalCa);
        model.addAttribute("totalHoaDon", totalHoaDon);
        model.addAttribute("totalSanPham", totalSanPham);
        model.addAttribute("totalTien", totalTien);
        model.addAttribute("isAdmin", "ADMIN".equals(roleCode));

        model.addAttribute("pageTitle", "Chấm công");
        model.addAttribute("pageHeading", "Quản lý chấm công");
        model.addAttribute("activeMenu", "chamcong");
        model.addAttribute("content", "ChamCong/index");
        model.addAttribute("pageCss", "/css/chamcong.css");
        return "layout";
    }

    /** API lấy chi tiết 1 ca (cho modal) */
    @GetMapping("/api/ca-lam-viec/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chiTiet(@PathVariable Integer id) {
        Optional<CaLamViec> optCa = caLamViecService.getById(id);
        if (optCa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CaLamViec ca = optCa.get();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", ca.getId());
        result.put("nhanVienId", ca.getNhanVienId());
        result.put("hoTenNhanVien", ca.getHoTenNhanVien());
        result.put("thoiGianBatDau", ca.getThoiGianBatDau().toString());
        result.put("thoiGianKetThuc", ca.getThoiGianKetThuc() != null ? ca.getThoiGianKetThuc().toString() : null);
        result.put("trangThai", ca.getTrangThai());
        result.put("tongHoaDon", ca.getTongHoaDon());
        result.put("tongSanPham", ca.getTongSanPham());
        result.put("tongTien", ca.getTongTien());
        result.put("ghiChu", ca.getGhiChu());
        return ResponseEntity.ok(result);
    }
}
