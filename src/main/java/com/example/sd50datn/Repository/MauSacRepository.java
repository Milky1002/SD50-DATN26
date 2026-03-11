package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MauSacRepository extends JpaRepository<MauSac, Integer> {
    List<MauSac> findByTenMauContainingIgnoreCase(String q);
    boolean existsByTenMau(String tenMau);
    boolean existsByTenMauAndMauSacIdNot(String tenMau, Integer mauSacId);
}