package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.PhieuXuat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuXuatRepository extends JpaRepository<PhieuXuat, Integer> {
    List<PhieuXuat> findAllByOrderByIdDesc();
    boolean existsByMaPhieuXuat(String maPhieuXuat);
}
