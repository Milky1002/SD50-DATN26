package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "PhieuNhapChiTiet")
@Getter
@Setter
@NoArgsConstructor
public class PhieuNhapChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Phieu_nhap_chi_tiet_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Phieu_nhap_id", nullable = false)
    private PhieuNhap phieuNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "San_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "So_luong_nhap", nullable = false)
    private Integer soLuongNhap;

    @Column(name = "Don_gia_nhap", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGiaNhap;

    @Column(name = "Ghi_chu", columnDefinition = "nvarchar(max)")
    private String ghiChu;
}
