package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.NhanVienHoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanVienHoatDongRepository extends JpaRepository<NhanVienHoatDong, Integer> {

    List<NhanVienHoatDong> findAllByOrderByThoiGianDesc();

    List<NhanVienHoatDong> findByNhanVienIdOrderByThoiGianDesc(Integer nhanVienId);

    List<NhanVienHoatDong> findByHanhDongOrderByThoiGianDesc(String hanhDong);
}
