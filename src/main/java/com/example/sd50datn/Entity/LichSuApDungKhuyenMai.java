package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Lich_su_ap_dung_khuyen_mai")
@Getter
@Setter
@NoArgsConstructor
public class LichSuApDungKhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Lich_su_id")
    private Integer id;

    @Column(name = "Chuong_trinh_khuyen_mai_id", nullable = false, insertable = false, updatable = false)
    private Integer chuongTrinhKhuyenMaiId;

    @Column(name = "Hoa_don_id", nullable = false, insertable = false, updatable = false)
    private Integer hoaDonId;

    @Column(name = "Gia_tri_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "Ngay_ap_dung", nullable = false)
    private LocalDateTime ngayApDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Chuong_trinh_khuyen_mai_id", nullable = false)
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Hoa_don_id", nullable = false)
    private HoaDon hoaDon;

    @PrePersist
    protected void onCreate() {
        if (ngayApDung == null) ngayApDung = LocalDateTime.now();
    }
}
