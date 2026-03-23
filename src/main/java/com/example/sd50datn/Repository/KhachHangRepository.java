package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang,Integer> {

    List<KhachHang> findByTenKhachHangContaining(String ten);
    List<KhachHang> findTop10ByTrangThaiAndTenKhachHangContainingIgnoreCaseOrderByTenKhachHangAsc(Integer trangThai, String ten);
    List<KhachHang> findTop10ByTrangThaiAndSdtContainingOrderByTenKhachHangAsc(Integer trangThai, String sdt);
    List<KhachHang> findByTrangThai(Integer trangThai);

    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findBySdt(String sdt);
    Optional<KhachHang> findByTaiKhoanId(Integer taiKhoanId);
    @Query("SELECT kh FROM KhachHang kh LEFT JOIN FETCH kh.taiKhoan WHERE kh.khachHangId = :id")
    Optional<KhachHang> findByIdWithTaiKhoan(Integer id);
    @Query("SELECT kh FROM KhachHang kh LEFT JOIN FETCH kh.taiKhoan ORDER BY kh.khachHangId DESC")
    List<KhachHang> findAllWithTaiKhoan();
    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);
}
