package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.TrangChuDanhMucNoiBat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrangChuDanhMucNoiBatRepository extends JpaRepository<TrangChuDanhMucNoiBat, Integer> {
    List<TrangChuDanhMucNoiBat> findByTrangThaiOrderByThuTuAscIdAsc(Integer trangThai);
    boolean existsByDanhMucSanPham_DanhMucSanPhamId(Integer danhMucId);
    void deleteByDanhMucSanPham_DanhMucSanPhamId(Integer danhMucId);

    @Query("SELECT d FROM TrangChuDanhMucNoiBat d " +
           "LEFT JOIN FETCH d.danhMucSanPham " +
           "WHERE d.trangThai = :trangThai " +
           "ORDER BY d.thuTu ASC, d.id ASC")
    List<TrangChuDanhMucNoiBat> findActiveWithDanhMuc(@Param("trangThai") Integer trangThai);
}
