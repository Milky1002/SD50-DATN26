package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Hoa_don_id")
    private Integer id;

    @Column(name = "Ten_khach_hang", nullable = false)
    private String tenKhachHang;

    @Column(name = "Sdt_khach_hang")
    private String sdtKhachHang;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Tong_tien_sau_khi_giam", nullable = false)
    private BigDecimal tongTienSauKhiGiam;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai;
}

