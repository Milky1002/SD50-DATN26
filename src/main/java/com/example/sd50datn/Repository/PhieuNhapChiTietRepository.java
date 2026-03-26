package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.PhieuNhapChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuNhapChiTietRepository extends JpaRepository<PhieuNhapChiTiet, Integer> {
    List<PhieuNhapChiTiet> findByPhieuNhapId(Integer phieuNhapId);
}
