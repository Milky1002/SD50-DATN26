package com.example.sd50datn.repository;

import com.example.sd50datn.entity.ChuongTrinhKhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChuongTrinhKhuyenMaiRepository extends JpaRepository<ChuongTrinhKhuyenMai, Integer> {

    Optional<ChuongTrinhKhuyenMai> findByMaChuongTrinh(String maChuongTrinh);

    List<ChuongTrinhKhuyenMai> findByTrangThai(Integer trangThai);

    List<ChuongTrinhKhuyenMai> findByLoaiKhuyenMai(Integer loaiKhuyenMai);

    @Query("SELECT c FROM ChuongTrinhKhuyenMai c WHERE c.trangThai = 1 " +
           "AND c.ngayBatDau <= :now AND c.ngayKetThuc >= :now")
    List<ChuongTrinhKhuyenMai> findActivePromotions(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM ChuongTrinhKhuyenMai c WHERE c.trangThai = 1 " +
           "AND c.ngayBatDau <= :now AND c.ngayKetThuc >= :now " +
           "AND c.loaiKhuyenMai = :loaiKhuyenMai")
    List<ChuongTrinhKhuyenMai> findActivePromotionsByType(
        @Param("now") LocalDateTime now,
        @Param("loaiKhuyenMai") Integer loaiKhuyenMai
    );

    @Query("SELECT c FROM ChuongTrinhKhuyenMai c WHERE c.trangThai = 1 " +
           "AND c.ngayBatDau <= :now AND c.ngayKetThuc >= :now " +
           "AND c.tuDongApDung = true")
    List<ChuongTrinhKhuyenMai> findAutoApplyPromotions(@Param("now") LocalDateTime now);

    boolean existsByMaChuongTrinh(String maChuongTrinh);
}
