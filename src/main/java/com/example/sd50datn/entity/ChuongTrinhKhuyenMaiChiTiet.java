package com.example.sd50datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Chuong_trinh_khuyen_mai_chi_tiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhKhuyenMaiChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Chuong_trinh_khuyen_mai_chi_tiet_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Chuong_trinh_khuyen_mai_id", nullable = false)
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "San_pham_id")
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Danh_muc_san_pham_id")
    private DanhMucSanPham danhMucSanPham;

    @Column(name = "So_luong_toi_thieu")
    private Integer soLuongToiThieu;

    @Column(name = "So_luong_toi_da")
    private Integer soLuongToiDa;

    @Column(name = "Gia_tri_giam", precision = 18, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;
}
