package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    List<GioHangChiTiet> findByGioHangId(Integer gioHangId);

    Optional<GioHangChiTiet> findByGioHangIdAndSanPhamId(Integer gioHangId, Integer sanPhamId);

    void deleteByGioHangId(Integer gioHangId);
}
