package com.example.sd50datn.repository;

import com.example.sd50datn.entity.ChuongTrinhKhuyenMaiChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChuongTrinhKhuyenMaiChiTietRepository extends JpaRepository<ChuongTrinhKhuyenMaiChiTiet, Integer> {

    List<ChuongTrinhKhuyenMaiChiTiet> findByChuongTrinhKhuyenMaiId(Integer chuongTrinhKhuyenMaiId);

    List<ChuongTrinhKhuyenMaiChiTiet> findBySanPhamId(Integer sanPhamId);

    List<ChuongTrinhKhuyenMaiChiTiet> findByDanhMucSanPhamId(Integer danhMucSanPhamId);

    @Query("SELECT c FROM ChuongTrinhKhuyenMaiChiTiet c " +
           "WHERE c.chuongTrinhKhuyenMai.id = :chuongTrinhId " +
           "AND (c.sanPham.id = :sanPhamId OR c.danhMucSanPham.id IN " +
           "(SELECT sp.danhMucSanPham.id FROM SanPham sp WHERE sp.id = :sanPhamId))")
    List<ChuongTrinhKhuyenMaiChiTiet> findApplicableDetailsForProduct(
        @Param("chuongTrinhId") Integer chuongTrinhId,
        @Param("sanPhamId") Integer sanPhamId
    );

    void deleteByChuongTrinhKhuyenMaiId(Integer chuongTrinhKhuyenMaiId);
}
