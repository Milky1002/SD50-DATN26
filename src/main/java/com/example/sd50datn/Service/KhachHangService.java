package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    // Lấy tất cả
    public List<KhachHang> getAll() {
        return khachHangRepository.findAllWithTaiKhoan();
    }

    // Tìm theo id
    public KhachHang getById(Integer id) {
        Optional<KhachHang> kh = khachHangRepository.findByIdWithTaiKhoan(id);
        return kh.orElse(null);
    }

    // Thêm
    public KhachHang create(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    public KhachHang createForPos(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    public List<KhachHang> searchForPos(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalized = query.trim();
        List<KhachHang> byName = khachHangRepository.findTop10ByTrangThaiAndTenKhachHangContainingIgnoreCaseOrderByTenKhachHangAsc(1, normalized);
        List<KhachHang> byPhone = khachHangRepository.findTop10ByTrangThaiAndSdtContainingOrderByTenKhachHangAsc(1, normalized);

        return Stream.concat(byName.stream(), byPhone.stream())
                .distinct()
                .limit(10)
                .toList();
    }

    public boolean existsByPhone(String sdt) {
        return sdt != null && khachHangRepository.existsBySdt(sdt.trim());
    }

    public boolean existsByEmail(String email) {
        return email != null && !email.isBlank() && khachHangRepository.existsByEmail(email.trim());
    }

    // Cập nhật
    public KhachHang update(Integer id, KhachHang khachHang) {
        KhachHang kh = khachHangRepository.findById(id).orElse(null);

        if (kh != null) {
            kh.setTenKhachHang(khachHang.getTenKhachHang());
            kh.setSdt(khachHang.getSdt());
            kh.setEmail(khachHang.getEmail());
            kh.setTrangThai(khachHang.getTrangThai());
            kh.setNgayCapNhat(khachHang.getNgayCapNhat());
            kh.setDiaChiKhachHang(khachHang.getDiaChiKhachHang());

            return khachHangRepository.save(kh);
        }
        return null;
    }

    // Xóa
    public void delete(Integer id) {
        khachHangRepository.deleteById(id);
    }
}
