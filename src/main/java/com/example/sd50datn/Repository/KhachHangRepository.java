package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang,Integer> {

    List<KhachHang> findByTenKhachHangContaining(String ten);
    List<KhachHang> findByTrangThai(Integer trangThai);
}
