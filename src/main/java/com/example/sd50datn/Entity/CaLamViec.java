package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Ca_lam_viec")
@Getter
@Setter
@NoArgsConstructor
public class CaLamViec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ca_lam_viec_id")
    private Integer id;

    @Column(name = "Nhan_vien_id", nullable = false)
    private Integer nhanVienId;

    @Column(name = "Ho_ten_nhan_vien")
    private String hoTenNhanVien;

    @Column(name = "Thoi_gian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "Thoi_gian_ket_thuc")
    private LocalDateTime thoiGianKetThuc;

    /** 1 = đang làm, 2 = đã kết thúc */
    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "Tong_hoa_don", nullable = false)
    private Integer tongHoaDon = 0;

    @Column(name = "Tong_san_pham", nullable = false)
    private Integer tongSanPham = 0;

    @Column(name = "Tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "Ghi_chu", columnDefinition = "nvarchar(max)")
    private String ghiChu;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
        if (thoiGianBatDau == null) thoiGianBatDau = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
