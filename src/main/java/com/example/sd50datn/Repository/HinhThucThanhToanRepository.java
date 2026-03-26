package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan, Integer> {
    Optional<HinhThucThanhToan> findByTenHinhThuc(String tenHinhThuc);
    List<HinhThucThanhToan> findByTenHinhThucIn(Collection<String> tenHinhThucs);
}
