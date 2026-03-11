package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    Optional<SanPham> findByMaSanPham(String maSanPham);

    Optional<SanPham> findBySku(String sku);

    List<SanPham> findByTrangThai(Integer trangThai);

    List<SanPham> findByDanhMucSanPham_DanhMucSanPhamId(Integer danhMucSanPhamId);

    boolean existsByMaSanPham(String maSanPham);

    boolean existsBySku(String sku);
}
