package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.PhieuXuat;
import com.example.sd50datn.Entity.PhieuXuatChiTiet;
import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.PhieuXuatChiTietRepository;
import com.example.sd50datn.Repository.PhieuXuatRepository;
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
public class XuatKhoService {

    private final PhieuXuatRepository phieuXuatRepo;
    private final PhieuXuatChiTietRepository chiTietRepo;
    private final SanPhamRepository sanPhamRepo;

    public List<PhieuXuat> getAll() {
        return phieuXuatRepo.findAllByOrderByIdDesc();
    }

    public PhieuXuat getById(Integer id) {
        return phieuXuatRepo.findById(id).orElse(null);
    }

    public List<PhieuXuatChiTiet> getChiTiet(Integer phieuXuatId) {
        return chiTietRepo.findByPhieuXuatId(phieuXuatId);
    }

    public String generateMaPhieu() {
        String prefix = "PX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = 1;
        while (phieuXuatRepo.existsByMaPhieuXuat(prefix + String.format("%03d", count))) {
            count++;
        }
        return prefix + String.format("%03d", count);
    }

    @Transactional
    public PhieuXuat createPhieuXuat(String lyDo, String ghiChu,
                                     List<Integer> sanPhamIds, List<Integer> soLuongs) {

        if (sanPhamIds == null || sanPhamIds.isEmpty()) {
            throw new IllegalArgumentException("Chua chon san pham");
        }
        if (soLuongs == null || soLuongs.size() != sanPhamIds.size()) {
            throw new IllegalArgumentException("Du lieu so luong khong hop le");
        }

        for (int i = 0; i < sanPhamIds.size(); i++) {
            SanPham sp = sanPhamRepo.findById(sanPhamIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException("San pham khong ton tai"));
            if (sp.getSoLuongTon() < soLuongs.get(i)) {
                throw new IllegalArgumentException("San pham " + sp.getTenSanPham() + " khong du ton kho (con " + sp.getSoLuongTon() + ")");
            }
        }

        PhieuXuat px = new PhieuXuat();
        px.setMaPhieuXuat(generateMaPhieu());
        px.setNhanVienId(1);
        px.setLyDo(lyDo);
        px.setGhiChu(ghiChu);
        px.setTrangThai(1);
        px.setTongTien(BigDecimal.ZERO);
        px = phieuXuatRepo.save(px);

        BigDecimal tongTien = BigDecimal.ZERO;
        for (int i = 0; i < sanPhamIds.size(); i++) {
            SanPham sp = sanPhamRepo.findById(sanPhamIds.get(i)).orElseThrow();
            int soLuong = soLuongs.get(i);

            PhieuXuatChiTiet ct = new PhieuXuatChiTiet();
            ct.setPhieuXuat(px);
            ct.setSanPham(sp);
            ct.setSoLuongXuat(soLuong);
            ct.setDonGia(sp.getGiaBan());
            chiTietRepo.save(ct);

            tongTien = tongTien.add(sp.getGiaBan().multiply(BigDecimal.valueOf(soLuong)));

            sp.setSoLuongTon(sp.getSoLuongTon() - soLuong);
            sanPhamRepo.save(sp);
        }

        px.setTongTien(tongTien);
        phieuXuatRepo.save(px);

        return px;
    }
}
