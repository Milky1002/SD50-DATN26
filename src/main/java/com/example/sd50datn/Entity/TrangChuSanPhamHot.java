package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Trang_chu_san_pham_hot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrangChuSanPhamHot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Trang_chu_san_pham_hot_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "San_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "Thu_tu", nullable = false)
    private Integer thuTu = 0;

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
