package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.PhieuXuatChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuXuatChiTietRepository extends JpaRepository<PhieuXuatChiTiet, Integer> {
    List<PhieuXuatChiTiet> findByPhieuXuatId(Integer phieuXuatId);
}
