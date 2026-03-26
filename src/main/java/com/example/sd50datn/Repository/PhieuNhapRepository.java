package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.PhieuNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Integer> {
    List<PhieuNhap> findAllByOrderByIdDesc();
    boolean existsByMaPhieuNhap(String maPhieuNhap);
}
