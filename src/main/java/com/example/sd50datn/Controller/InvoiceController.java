package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    @GetMapping("/invoices")
    public String invoiceManagement(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("pageTitle", "Quản lý hóa đơn");
        model.addAttribute("pageHeading", "Quản lý hóa đơn");
        model.addAttribute("activeMenu", "hoadon");

        model.addAttribute("stats", invoiceService.getStats());
        model.addAttribute("invoices", invoiceService.getInvoiceSummaries(q));
        model.addAttribute("searchQuery", q != null ? q.trim() : "");

        model.addAttribute("content", "invoice-management");
        model.addAttribute("pageCss", "/css/invoice-management.css");

        return "layout";
    }

    @GetMapping("/invoices/api/detail/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getInvoiceDetail(@PathVariable Integer id) {
        try {
            HoaDon hoaDon = invoiceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn #" + id));

            List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonId(id);

            Map<String, Object> result = new HashMap<>();
            result.put("id", hoaDon.getId());
            result.put("code", String.format("#INV-%05d", hoaDon.getId()));
            result.put("tenKhachHang", hoaDon.getTenKhachHang());
            result.put("sdtKhachHang", hoaDon.getSdtKhachHang());
            result.put("emailKhachHang", hoaDon.getEmailKhachHang());
            result.put("ngayTao", hoaDon.getNgayTao() != null ? hoaDon.getNgayTao().toString() : null);
            result.put("tongTien", hoaDon.getTongTienSauKhiGiam());
            result.put("trangThai", hoaDon.getTrangThai());
            result.put("loaiHoaDon", hoaDon.getLoaiHoaDon());
            result.put("ghiChu", hoaDon.getGhiChu());
            result.put("diaChiKhachHang", hoaDon.getDiaChiKhachHang());
            result.put("thongTinVoucher", hoaDon.getThongTinVoucher());

            List<Map<String, Object>> items = chiTietList.stream().map(ct -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", ct.getId());
                m.put("tenSanPham", ct.getSanPham() != null ? ct.getSanPham().getTenSanPham() : "N/A");
                m.put("maSanPham", ct.getSanPham() != null ? ct.getSanPham().getMaSanPham() : "");
                m.put("soLuong", ct.getSoLuongSanPham());
                m.put("gia", ct.getGia());
                m.put("thanhTien", ct.getGia() != null ? ct.getGia().multiply(java.math.BigDecimal.valueOf(ct.getSoLuongSanPham())) : java.math.BigDecimal.ZERO);
                return m;
            }).toList();
            result.put("items", items);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
