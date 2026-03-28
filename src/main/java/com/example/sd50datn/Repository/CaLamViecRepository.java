package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.CaLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaLamViecRepository extends JpaRepository<CaLamViec, Integer> {

    /** Tìm ca đang mở (trang_thai = 1) của một nhân viên */
    Optional<CaLamViec> findByNhanVienIdAndTrangThai(Integer nhanVienId, Integer trangThai);

    /** Lịch sử ca làm của 1 nhân viên, mới nhất trước */
    List<CaLamViec> findByNhanVienIdOrderByThoiGianBatDauDesc(Integer nhanVienId);

    /** Tất cả ca trong khoảng thời gian, mới nhất trước */
    List<CaLamViec> findByThoiGianBatDauBetweenOrderByThoiGianBatDauDesc(
            LocalDateTime from, LocalDateTime to);

    /** Ca của 1 nhân viên trong khoảng thời gian */
    List<CaLamViec> findByNhanVienIdAndThoiGianBatDauBetweenOrderByThoiGianBatDauDesc(
            Integer nhanVienId, LocalDateTime from, LocalDateTime to);

    /** Tất cả ca, mới nhất trước */
    List<CaLamViec> findAllByOrderByThoiGianBatDauDesc();

    /** Đếm hóa đơn trong ca (dùng cho tính toán khi kết thúc ca) */
    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.nhanVienId = :nhanVienId " +
           "AND h.ngayTao BETWEEN :from AND :to")
    long countInvoicesInShift(@Param("nhanVienId") Integer nhanVienId,
                              @Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);

    /** Tổng tiền bán trong ca */
    @Query("SELECT COALESCE(SUM(h.tongTienSauKhiGiam), 0) FROM HoaDon h " +
           "WHERE h.nhanVienId = :nhanVienId AND h.ngayTao BETWEEN :from AND :to")
    java.math.BigDecimal sumRevenueInShift(@Param("nhanVienId") Integer nhanVienId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);

    /** Tổng sản phẩm bán trong ca */
    @Query("SELECT COALESCE(SUM(c.soLuongSanPham), 0) FROM HoaDonChiTiet c " +
           "WHERE c.hoaDon.nhanVienId = :nhanVienId " +
           "AND c.hoaDon.ngayTao BETWEEN :from AND :to")
    long sumProductsInShift(@Param("nhanVienId") Integer nhanVienId,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to);
}
