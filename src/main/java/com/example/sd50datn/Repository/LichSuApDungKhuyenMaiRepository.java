package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.LichSuApDungKhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LichSuApDungKhuyenMaiRepository extends JpaRepository<LichSuApDungKhuyenMai, Integer> {

    List<LichSuApDungKhuyenMai> findByChuongTrinhKhuyenMaiId(Integer chuongTrinhKhuyenMaiId);

    List<LichSuApDungKhuyenMai> findByHoaDonId(Integer hoaDonId);

    @Query("SELECT l FROM LichSuApDungKhuyenMai l " +
           "LEFT JOIN FETCH l.chuongTrinhKhuyenMai c " +
           "LEFT JOIN FETCH l.hoaDon h " +
           "WHERE (:promotionId IS NULL OR l.chuongTrinhKhuyenMaiId = :promotionId) " +
           "AND (:hoaDonId IS NULL OR l.hoaDonId = :hoaDonId) " +
           "AND (:fromDate IS NULL OR l.ngayApDung >= :fromDate) " +
           "AND (:toDate IS NULL OR l.ngayApDung <= :toDate) " +
           "ORDER BY l.ngayApDung DESC")
    List<LichSuApDungKhuyenMai> findWithFilters(
            @Param("promotionId") Integer promotionId,
            @Param("hoaDonId") Integer hoaDonId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query("SELECT l FROM LichSuApDungKhuyenMai l " +
           "LEFT JOIN FETCH l.chuongTrinhKhuyenMai c " +
           "LEFT JOIN FETCH l.hoaDon h " +
           "ORDER BY l.ngayApDung DESC")
    List<LichSuApDungKhuyenMai> findAllWithDetails();
}
