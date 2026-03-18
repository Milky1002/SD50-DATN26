package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "HoaDonChiTiet")
@Getter
@Setter
@NoArgsConstructor
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Hoa_don_chi_tiet_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Hoa_don_id", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "San_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "So_luong_san_pham", nullable = false)
    private Integer soLuongSanPham;

    @Column(name = "Gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal gia;
}
