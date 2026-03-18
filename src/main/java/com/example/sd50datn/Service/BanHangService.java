package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Entity.HoaDonChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.HoaDonChiTietRepository;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BanHangService {

    private final InvoiceRepository hoaDonRepo;
    private final HoaDonChiTietRepository hoaDonChiTietRepo;
    private final SanPhamRepository sanPhamRepo;

    @Transactional
    public HoaDon checkout(String tenKhachHang, String sdtKhachHang,
                           String ghiChu, String phuongThucThanhToan,
                           int tienKhachDua,
                           List<Map<String, Object>> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Gio hang trong");
        }

        BigDecimal tongTien = BigDecimal.ZERO;

        for (Map<String, Object> item : items) {
            int spId = ((Number) item.get("sanPhamId")).intValue();
            int soLuong = ((Number) item.get("soLuong")).intValue();
            SanPham sp = sanPhamRepo.findById(spId)
                    .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai: " + spId));
            if (sp.getSoLuongTon() < soLuong) {
                throw new IllegalArgumentException("San pham " + sp.getTenSanPham() + " khong du ton kho");
            }
        }

        // Map payment method to hinhThucThanhToanId
        Integer hinhThucThanhToanId = null;
        if (phuongThucThanhToan != null) {
            switch (phuongThucThanhToan) {
                case "cash": hinhThucThanhToanId = 1; break;
                case "transfer": hinhThucThanhToanId = 2; break;
                case "card": hinhThucThanhToanId = 3; break;
                case "ewallet": hinhThucThanhToanId = 4; break;
                default: hinhThucThanhToanId = 1; break;
            }
        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setNhanVienId(1);
        hoaDon.setTenKhachHang(tenKhachHang != null && !tenKhachHang.isBlank() ? tenKhachHang : "Khach le");
        hoaDon.setSdtKhachHang(sdtKhachHang);
        hoaDon.setGhiChu(ghiChu);
        hoaDon.setHinhThucThanhToanId(hinhThucThanhToanId);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTrangThai(1);
        hoaDon.setLoaiHoaDon("TAI_QUAY");
        hoaDon.setTongTienSauKhiGiam(BigDecimal.ZERO);

        hoaDon = hoaDonRepo.save(hoaDon);

        for (Map<String, Object> item : items) {
            int spId = ((Number) item.get("sanPhamId")).intValue();
            int soLuong = ((Number) item.get("soLuong")).intValue();

            SanPham sp = sanPhamRepo.findById(spId).orElseThrow();

            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setSanPham(sp);
            chiTiet.setSoLuongSanPham(soLuong);
            chiTiet.setGia(sp.getGiaBan());
            hoaDonChiTietRepo.save(chiTiet);

            BigDecimal lineTotal = sp.getGiaBan().multiply(BigDecimal.valueOf(soLuong));
            tongTien = tongTien.add(lineTotal);

            sp.setSoLuongTon(sp.getSoLuongTon() - soLuong);
            sanPhamRepo.save(sp);
        }

        hoaDon.setTongTienSauKhiGiam(tongTien);
        hoaDonRepo.save(hoaDon);

        return hoaDon;
    }
}
