package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "PhieuXuatChiTiet")
@Getter
@Setter
@NoArgsConstructor
public class PhieuXuatChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Phieu_xuat_chi_tiet_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Phieu_xuat_id", nullable = false)
    private PhieuXuat phieuXuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "San_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "So_luong_xuat", nullable = false)
    private Integer soLuongXuat;

    @Column(name = "Don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "Ghi_chu", columnDefinition = "nvarchar(max)")
    private String ghiChu;
}
