package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "NhaCungCap")
@Getter
@Setter
@NoArgsConstructor
public class NhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Nha_cung_cap_id")
    private Integer id;

    @Column(name = "Ten_nha_cung_cap", nullable = false, length = 255)
    private String tenNhaCungCap;

    @Column(name = "Nguoi_lien_he", length = 255)
    private String nguoiLienHe;

    @Column(name = "SDT", length = 50)
    private String sdt;

    @Column(name = "Email", length = 255)
    private String email;

    @Column(name = "Dia_chi", columnDefinition = "nvarchar(max)")
    private String diaChi;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
