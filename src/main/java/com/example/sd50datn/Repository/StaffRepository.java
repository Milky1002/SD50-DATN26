package com.example.sd50datn.Repository;

import com.example.sd50datn.Model.Staff;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    // Bạn có thể thêm các hàm tìm kiếm tùy chỉnh tại đây
    List<Staff> findByTrangThai(Integer trangThai);
    java.util.Optional<Staff> findByTaiKhoanId(Integer taiKhoanId);

    @Query("SELECT s FROM Staff s WHERE " +
            "(:keyword IS NULL OR s.hoTen LIKE %:keyword% OR s.sdt LIKE %:keyword%) AND " +
            "(:status IS NULL OR s.trangThai = :status)")
    List<Staff> searchStaff(@Param("keyword") String keyword, @Param("status") Integer status);

    @Modifying
    @Transactional
    @Query("UPDATE Staff n SET n.trangThai = :status, n.ngayCapNhat = CURRENT_TIMESTAMP WHERE n.id = :id")
    void updateStatus(Integer id, Integer status);
}