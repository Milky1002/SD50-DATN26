package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    Optional<GioHang> findByKhachHangId(Integer khachHangId);

    Optional<GioHang> findBySessionId(String sessionId);
}
