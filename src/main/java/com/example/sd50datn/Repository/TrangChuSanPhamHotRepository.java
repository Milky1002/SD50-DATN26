package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.TrangChuSanPhamHot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrangChuSanPhamHotRepository extends JpaRepository<TrangChuSanPhamHot, Integer> {
    List<TrangChuSanPhamHot> findByTrangThaiOrderByThuTuAscIdAsc(Integer trangThai);
    boolean existsBySanPham_Id(Integer sanPhamId);
    void deleteBySanPham_Id(Integer sanPhamId);

    @Query("SELECT h FROM TrangChuSanPhamHot h " +
           "LEFT JOIN FETCH h.sanPham sp " +
           "LEFT JOIN FETCH sp.danhMucSanPham " +
           "LEFT JOIN FETCH sp.mauSac " +
           "LEFT JOIN FETCH sp.anh " +
           "WHERE h.trangThai = :trangThai " +
           "ORDER BY h.thuTu ASC, h.id ASC")
    List<TrangChuSanPhamHot> findActiveWithSanPham(@Param("trangThai") Integer trangThai);
}
