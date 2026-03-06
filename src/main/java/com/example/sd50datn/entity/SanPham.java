package com.example.sd50datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "San_pham_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Danh_muc_san_pham_id", nullable = false)
    private DanhMucSanPham danhMucSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Mau_sac_id")
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Anh_id")
    private Anh anh;

    @Column(name = "Ten_san_pham", nullable = false, length = 255)
    private String tenSanPham;

    @Column(name = "Ma_san_pham", nullable = false, unique = true, length = 50)
    private String maSanPham;

    @Column(name = "Sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "Gia_nhap", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaNhap;

    @Column(name = "Gia_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "So_luong_ton", nullable = false)
    private Integer soLuongTon = 0;

    @Column(name = "Don_vi_tinh", length = 50)
    private String donViTinh;

    @Column(name = "Mo_ta", columnDefinition = "nvarchar(max)")
    private String moTa;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "Ngay_sua")
    private LocalDateTime ngaySua;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngaySua = LocalDateTime.now();
    }
}
