package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    Optional<SanPham> findByMaSanPham(String maSanPham);

    Optional<SanPham> findBySku(String sku);

    Optional<SanPham> findByBarcode(String barcode);

    List<SanPham> findByTrangThai(Integer trangThai);

    List<SanPham> findByDanhMucSanPham_DanhMucSanPhamId(Integer danhMucSanPhamId);

    boolean existsByMaSanPham(String maSanPham);

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);

    boolean existsByMaSanPhamAndIdNot(String maSanPham, Integer id);

    boolean existsBySkuAndIdNot(String sku, Integer id);

    boolean existsByBarcodeAndIdNot(String barcode, Integer id);

    @Query("SELECT sp FROM SanPham sp LEFT JOIN FETCH sp.danhMucSanPham LEFT JOIN FETCH sp.mauSac " +
           "WHERE (:keyword IS NULL OR :keyword = '' " +
           "OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(sp.sku) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(sp.barcode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:trangThai IS NULL OR sp.trangThai = :trangThai) " +
           "AND (:danhMucId IS NULL OR sp.danhMucSanPham.danhMucSanPhamId = :danhMucId) " +
           "ORDER BY sp.id DESC")
    List<SanPham> searchProducts(@Param("keyword") String keyword,
                                 @Param("trangThai") Integer trangThai,
                                 @Param("danhMucId") Integer danhMucId);

    @Query("SELECT sp FROM SanPham sp LEFT JOIN FETCH sp.danhMucSanPham LEFT JOIN FETCH sp.mauSac LEFT JOIN FETCH sp.anh " +
           "WHERE sp.trangThai = 1 AND sp.danhMucSanPham.danhMucSanPhamId = :danhMucId ORDER BY sp.id DESC")
    List<SanPham> findLatestActiveByCategory(@Param("danhMucId") Integer danhMucId);

    @Query("SELECT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.danhMucSanPham " +
           "LEFT JOIN FETCH sp.mauSac " +
           "LEFT JOIN FETCH sp.anh " +
           "WHERE sp.id = :id")
    Optional<SanPham> findByIdWithRelations(@Param("id") Integer id);
}
