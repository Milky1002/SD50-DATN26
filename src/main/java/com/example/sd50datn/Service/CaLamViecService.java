package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.CaLamViec;
import com.example.sd50datn.Repository.CaLamViecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CaLamViecService {

    private final CaLamViecRepository caLamViecRepo;
    private final NhanVienHoatDongService nhanVienHoatDongService;

    /**
     * Bắt đầu ca làm việc.
     * Nếu nhân viên đang có ca mở → trả về ca đó (không tạo mới).
     */
    @Transactional
    public CaLamViec batDauCa(Integer nhanVienId, String hoTen) {
        // Kiểm tra ca đang mở
        Optional<CaLamViec> existing = caLamViecRepo.findByNhanVienIdAndTrangThai(nhanVienId, 1);
        if (existing.isPresent()) {
            return existing.get();
        }

        CaLamViec ca = new CaLamViec();
        ca.setNhanVienId(nhanVienId);
        ca.setHoTenNhanVien(hoTen);
        ca.setThoiGianBatDau(LocalDateTime.now());
        ca.setTrangThai(1);
        ca.setTongHoaDon(0);
        ca.setTongSanPham(0);
        ca.setTongTien(BigDecimal.ZERO);
        ca = caLamViecRepo.save(ca);

        // Log hoạt động
        nhanVienHoatDongService.log(nhanVienId, hoTen,
                "CA_BAT_DAU", "CA_LAM_VIEC", ca.getId(),
                "Bắt đầu ca làm việc", null);

        return ca;
    }

    /**
     * Kết thúc ca làm việc.
     * Tính toán thống kê từ HoaDon trong khoảng thời gian ca.
     */
    @Transactional
    public CaLamViec ketThucCa(Integer nhanVienId, String hoTen, String ghiChu) {
        Optional<CaLamViec> optCa = caLamViecRepo.findByNhanVienIdAndTrangThai(nhanVienId, 1);
        if (optCa.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy ca làm việc đang mở");
        }

        CaLamViec ca = optCa.get();
        LocalDateTime now = LocalDateTime.now();
        ca.setThoiGianKetThuc(now);
        ca.setTrangThai(2);
        if (ghiChu != null && !ghiChu.isBlank()) {
            ca.setGhiChu(ghiChu);
        }

        // Tính toán thống kê
        long soHoaDon = caLamViecRepo.countInvoicesInShift(
                nhanVienId, ca.getThoiGianBatDau(), now);
        BigDecimal tongTien = caLamViecRepo.sumRevenueInShift(
                nhanVienId, ca.getThoiGianBatDau(), now);
        long soSanPham = caLamViecRepo.sumProductsInShift(
                nhanVienId, ca.getThoiGianBatDau(), now);

        ca.setTongHoaDon((int) soHoaDon);
        ca.setTongTien(tongTien != null ? tongTien : BigDecimal.ZERO);
        ca.setTongSanPham((int) soSanPham);

        ca = caLamViecRepo.save(ca);

        // Log hoạt động
        nhanVienHoatDongService.log(nhanVienId, hoTen,
                "CA_KET_THUC", "CA_LAM_VIEC", ca.getId(),
                String.format("Kết thúc ca: %d hóa đơn, %d sản phẩm",
                        ca.getTongHoaDon(), ca.getTongSanPham()),
                ca.getTongTien());

        return ca;
    }

    /**
     * Lấy ca đang mở (nếu có) của nhân viên.
     */
    public Optional<CaLamViec> getCaDangMo(Integer nhanVienId) {
        return caLamViecRepo.findByNhanVienIdAndTrangThai(nhanVienId, 1);
    }

    /**
     * Lấy thống kê realtime của ca đang mở.
     */
    public Map<String, Object> getThongKeCaDangMo(CaLamViec ca) {
        LocalDateTime now = LocalDateTime.now();
        long soHoaDon = caLamViecRepo.countInvoicesInShift(
                ca.getNhanVienId(), ca.getThoiGianBatDau(), now);
        BigDecimal tongTien = caLamViecRepo.sumRevenueInShift(
                ca.getNhanVienId(), ca.getThoiGianBatDau(), now);
        long soSanPham = caLamViecRepo.sumProductsInShift(
                ca.getNhanVienId(), ca.getThoiGianBatDau(), now);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("tongHoaDon", soHoaDon);
        stats.put("tongSanPham", soSanPham);
        stats.put("tongTien", tongTien != null ? tongTien : BigDecimal.ZERO);
        stats.put("thoiGianBatDau", ca.getThoiGianBatDau().toString());
        return stats;
    }

    /**
     * Lấy tất cả ca theo khoảng ngày (cho trang chấm công).
     */
    public List<CaLamViec> getAllByDateRange(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);
        return caLamViecRepo.findByThoiGianBatDauBetweenOrderByThoiGianBatDauDesc(fromDt, toDt);
    }

    /**
     * Lấy ca của 1 nhân viên theo khoảng ngày.
     */
    public List<CaLamViec> getByNhanVienAndDateRange(Integer nhanVienId, LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);
        return caLamViecRepo.findByNhanVienIdAndThoiGianBatDauBetweenOrderByThoiGianBatDauDesc(
                nhanVienId, fromDt, toDt);
    }

    /**
     * Lấy tất cả ca (không lọc).
     */
    public List<CaLamViec> getAll() {
        return caLamViecRepo.findAllByOrderByThoiGianBatDauDesc();
    }

    /**
     * Lấy chi tiết 1 ca.
     */
    public Optional<CaLamViec> getById(Integer id) {
        return caLamViecRepo.findById(id);
    }
}
