package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan")
@Getter
@Setter
@NoArgsConstructor
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Thanh_toan_id")
    private Integer id;

    @Column(name = "Hinh_thuc_thanh_toan_id", nullable = false)
    private Integer hinhThucThanhToanId;

    @Column(name = "Hoa_don_id", nullable = false)
    private Integer hoaDonId;

    @Column(name = "So_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTien;

    @Column(name = "Paid_at")
    private LocalDateTime paidAt;

    @Column(name = "Ma_giao_dich", length = 255)
    private String maGiaoDich;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 0;
}
