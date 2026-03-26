package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.NhaCungCap;
import com.example.sd50datn.Entity.PhieuNhap;
import com.example.sd50datn.Entity.PhieuNhapChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.NhaCungCapRepository;
import com.example.sd50datn.Repository.PhieuNhapChiTietRepository;
import com.example.sd50datn.Repository.PhieuNhapRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NhapKhoService {

    private final PhieuNhapRepository phieuNhapRepo;
    private final PhieuNhapChiTietRepository chiTietRepo;
    private final NhaCungCapRepository nhaCungCapRepo;
    private final SanPhamRepository sanPhamRepo;

    public List<PhieuNhap> getAll() {
        return phieuNhapRepo.findAllByOrderByIdDesc();
    }

    public PhieuNhap getById(Integer id) {
        return phieuNhapRepo.findById(id).orElse(null);
    }

    public List<PhieuNhapChiTiet> getChiTiet(Integer phieuNhapId) {
        return chiTietRepo.findByPhieuNhapId(phieuNhapId);
    }

    public String generateMaPhieu() {
        String prefix = "PN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = 1;
        while (phieuNhapRepo.existsByMaPhieuNhap(prefix + String.format("%03d", count))) {
            count++;
        }
        return prefix + String.format("%03d", count);
    }

    @Transactional
    public PhieuNhap createPhieuNhap(Integer nhaCungCapId,
                                     String ghiChu,
                                     List<Integer> sanPhamIds,
                                     List<Integer> soLuongs,
                                     List<BigDecimal> donGiaNhaps) {
        if (nhaCungCapId == null) {
            throw new IllegalArgumentException("Chưa chọn nhà cung cấp");
        }
        if (sanPhamIds == null || sanPhamIds.isEmpty()) {
            throw new IllegalArgumentException("Chưa chọn sản phẩm");
        }
        if (soLuongs == null || soLuongs.size() != sanPhamIds.size()) {
            throw new IllegalArgumentException("Dữ liệu số lượng không hợp lệ");
        }
        if (donGiaNhaps == null || donGiaNhaps.size() != sanPhamIds.size()) {
            throw new IllegalArgumentException("Dữ liệu giá nhập không hợp lệ");
        }

        NhaCungCap nhaCungCap = nhaCungCapRepo.findById(nhaCungCapId)
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại"));

        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setMaPhieuNhap(generateMaPhieu());
        phieuNhap.setNhaCungCap(nhaCungCap);
        phieuNhap.setNhanVienId(1);
        phieuNhap.setGhiChu(ghiChu);
        phieuNhap.setTrangThai(1);
        phieuNhap.setTongTien(BigDecimal.ZERO);
        phieuNhap = phieuNhapRepo.save(phieuNhap);

        BigDecimal tongTien = BigDecimal.ZERO;
        for (int i = 0; i < sanPhamIds.size(); i++) {
            Integer soLuong = soLuongs.get(i);
            BigDecimal donGiaNhap = donGiaNhaps.get(i);

            if (soLuong == null || soLuong <= 0) {
                throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0");
            }
            if (donGiaNhap == null || donGiaNhap.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Giá nhập không hợp lệ");
            }

            SanPham sanPham = sanPhamRepo.findById(sanPhamIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

            PhieuNhapChiTiet chiTiet = new PhieuNhapChiTiet();
            chiTiet.setPhieuNhap(phieuNhap);
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuongNhap(soLuong);
            chiTiet.setDonGiaNhap(donGiaNhap);
            chiTietRepo.save(chiTiet);

            tongTien = tongTien.add(donGiaNhap.multiply(BigDecimal.valueOf(soLuong)));
            sanPham.setSoLuongTon((sanPham.getSoLuongTon() != null ? sanPham.getSoLuongTon() : 0) + soLuong);
            sanPhamRepo.save(sanPham);
        }

        phieuNhap.setTongTien(tongTien);
        phieuNhapRepo.save(phieuNhap);
        return phieuNhap;
    }
}
