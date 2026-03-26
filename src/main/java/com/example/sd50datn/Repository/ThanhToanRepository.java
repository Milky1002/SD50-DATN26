package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {
    Optional<ThanhToan> findByHoaDonId(Integer hoaDonId);
}
