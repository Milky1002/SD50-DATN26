package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Trang_chu_danh_muc_noi_bat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrangChuDanhMucNoiBat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Trang_chu_danh_muc_noi_bat_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Danh_muc_san_pham_id", nullable = false)
    private DanhMucSanPham danhMucSanPham;

    @Column(name = "Thu_tu", nullable = false)
    private Integer thuTu = 0;

    @Column(name = "So_luong_hien_thi", nullable = false)
    private Integer soLuongHienThi = 20;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
