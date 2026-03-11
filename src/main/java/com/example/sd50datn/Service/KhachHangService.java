package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    // Lấy tất cả
    public List<KhachHang> getAll() {
        return khachHangRepository.findAll();
    }

    // Tìm theo id
    public KhachHang getById(Integer id) {
        Optional<KhachHang> kh = khachHangRepository.findById(id);
        return kh.orElse(null);
    }

    // Thêm
    public KhachHang create(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
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
