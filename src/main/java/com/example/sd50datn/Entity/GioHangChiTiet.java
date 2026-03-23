package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Gio_hang_chi_tiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Gio_hang_chi_tiet_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Gio_hang_id", nullable = false)
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "San_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "So_luong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "Gia_tai_thoi_diem", precision = 18, scale = 2)
    private BigDecimal giaTaiThoiDiem;
}
