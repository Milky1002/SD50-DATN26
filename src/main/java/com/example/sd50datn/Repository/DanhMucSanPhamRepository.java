package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.DanhMucSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DanhMucSanPhamRepository extends JpaRepository<DanhMucSanPham, Integer> {
    List<DanhMucSanPham> findByTenDanhMucContainingIgnoreCase(String tenDanhMuc);
    boolean existsByTenDanhMuc(String tenDanhMuc);

    boolean existsByTenDanhMucAndDanhMucSanPhamIdNot(String tenDanhMuc, Integer danhMucSanPhamId);
}