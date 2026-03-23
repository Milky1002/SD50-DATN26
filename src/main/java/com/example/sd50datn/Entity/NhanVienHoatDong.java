package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Lich_su_hoat_dong_nhan_vien")
@Getter
@Setter
@NoArgsConstructor
public class NhanVienHoatDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /** ID của nhân viên thực hiện hành động */
    @Column(name = "Nhan_vien_id", nullable = false)
    private Integer nhanVienId;

    /** Tên nhân viên tại thời điểm ghi log (snapshot) */
    @Column(name = "Ho_ten_nhan_vien")
    private String hoTenNhanVien;

    /** Loại hành động: SALE_OFFLINE | KH_TAO | KH_SUA */
    @Column(name = "Hanh_dong", nullable = false, length = 50)
    private String hanhDong;

    /** Loại đối tượng tác động: HOA_DON | KHACH_HANG */
    @Column(name = "Doi_tuong", length = 50)
    private String doiTuong;

    /** ID của đối tượng (hoaDonId / khachHangId) */
    @Column(name = "Doi_tuong_id")
    private Integer doiTuongId;

    /** Mô tả chi tiết */
    @Column(name = "Mo_ta", columnDefinition = "nvarchar(max)")
    private String moTa;

    /** Giá trị liên quan (tổng tiền hóa đơn, ...) */
    @Column(name = "Gia_tri", precision = 18, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "Thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    @PrePersist
    protected void onCreate() {
        if (thoiGian == null) {
            thoiGian = LocalDateTime.now();
        }
    }
}
