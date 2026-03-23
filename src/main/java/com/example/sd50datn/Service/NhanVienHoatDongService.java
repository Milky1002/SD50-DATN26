package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.NhanVienHoatDong;
import com.example.sd50datn.Repository.NhanVienHoatDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NhanVienHoatDongService {

    private final NhanVienHoatDongRepository repo;

    /**
     * Ghi một bản ghi hoạt động nhân viên.
     *
     * @param nhanVienId  ID nhân viên (từ session)
     * @param hoTen       Họ tên nhân viên (snapshot từ session)
     * @param hanhDong    Loại hành động: SALE_OFFLINE | KH_TAO | KH_SUA
     * @param doiTuong    Đối tượng tác động: HOA_DON | KHACH_HANG
     * @param doiTuongId  ID của đối tượng
     * @param moTa        Mô tả chi tiết
     * @param giaTri      Giá trị liên quan (tổng tiền, ...)
     */
    public void log(Integer nhanVienId, String hoTen,
                    String hanhDong, String doiTuong, Integer doiTuongId,
                    String moTa, BigDecimal giaTri) {
        try {
            NhanVienHoatDong entry = new NhanVienHoatDong();
            entry.setNhanVienId(nhanVienId != null ? nhanVienId : 0);
            entry.setHoTenNhanVien(hoTen);
            entry.setHanhDong(hanhDong);
            entry.setDoiTuong(doiTuong);
            entry.setDoiTuongId(doiTuongId);
            entry.setMoTa(moTa);
            entry.setGiaTri(giaTri);
            repo.save(entry);
        } catch (Exception e) {
            // Log failures must never break the main transaction
        }
    }

    public List<NhanVienHoatDong> findAll() {
        return repo.findAllByOrderByThoiGianDesc();
    }

    public List<NhanVienHoatDong> findByNhanVien(Integer nhanVienId) {
        return repo.findByNhanVienIdOrderByThoiGianDesc(nhanVienId);
    }
}
