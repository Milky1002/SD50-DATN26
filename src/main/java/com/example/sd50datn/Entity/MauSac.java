package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Mau_sac")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Mau_sac_id")
    private Integer mauSacId;

    @Column(name = "Ten_mau", length = 255, nullable = false)
    private String tenMau;

    @Column(name = "Ma_mau_hex", length = 50)
    private String maMauHex;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai;

    @Column(name = "Ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = 1;
        }
        if (this.maMauHex == null || this.maMauHex.isBlank()) {
            this.maMauHex = "#000000";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}
