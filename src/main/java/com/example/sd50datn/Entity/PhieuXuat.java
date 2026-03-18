package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuXuat")
@Getter
@Setter
@NoArgsConstructor
public class PhieuXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Phieu_xuat_id")
    private Integer id;

    @Column(name = "Ma_phieu_xuat", nullable = false, unique = true, length = 50)
    private String maPhieuXuat;

    @Column(name = "Nhan_vien_id", nullable = false)
    private Integer nhanVienId;

    @Column(name = "Ngay_xuat", nullable = false)
    private LocalDateTime ngayXuat;

    @Column(name = "Tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 0;

    @Column(name = "Ly_do", columnDefinition = "nvarchar(max)")
    private String lyDo;

    @Column(name = "Ghi_chu", columnDefinition = "nvarchar(max)")
    private String ghiChu;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
        if (ngayXuat == null) ngayXuat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
