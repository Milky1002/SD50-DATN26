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

    @Column(name = "Nhan_vien_id", nullable = false)
    private Integer nhanVienId;

    @Column(name = "Voucher_id")
    private Integer voucherId;

    @Column(name = "Khach_hang_id")
    private Integer khachHangId;

    @Column(name = "Hinh_thuc_thanh_toan_id")
    private Integer hinhThucThanhToanId;

    @Column(name = "Dia_chi_id")
    private Integer diaChiId;

    @Column(name = "Ten_khach_hang", nullable = false)
    private String tenKhachHang;

    @Column(name = "Sdt_khach_hang")
    private String sdtKhachHang;

    @Column(name = "Email_khach_hang")
    private String emailKhachHang;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_nhan_hang")
    private LocalDateTime ngayNhanHang;

    @Column(name = "Tong_tien_sau_khi_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTienSauKhiGiam;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 0;

    @Column(name = "Loai_hoa_don")
    private String loaiHoaDon;

    @Column(name = "Ghi_chu", columnDefinition = "nvarchar(max)")
    private String ghiChu;

    @Column(name = "Dia_chi_khach_hang", columnDefinition = "nvarchar(max)")
    private String diaChiKhachHang;

    @Column(name = "Thong_tin_voucher", columnDefinition = "nvarchar(max)")
    private String thongTinVoucher;

    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
