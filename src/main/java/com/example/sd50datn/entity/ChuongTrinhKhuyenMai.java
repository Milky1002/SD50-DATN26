package com.example.sd50datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Chuong_trinh_khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhKhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Chuong_trinh_khuyen_mai_id")
    private Integer id;

    @Column(name = "Ma_chuong_trinh", nullable = false, unique = true, length = 50)
    private String maChuongTrinh;

    @Column(name = "Ten_chuong_trinh", nullable = false, length = 255)
    private String tenChuongTrinh;

    @Column(name = "Mo_ta", columnDefinition = "nvarchar(max)")
    private String moTa;

    /**
     * Loại khuyến mại:
     * 1 = Giảm giá hóa đơn
     * 2 = Giảm giá sản phẩm
     * 3 = Tặng hàng
     * 4 = Đồng giá
     */
    @Column(name = "Loai_khuyen_mai", nullable = false)
    private Integer loaiKhuyenMai;

    /**
     * Loại giảm:
     * 1 = Theo phần trăm (%)
     * 2 = Theo tiền (VNĐ)
     */
    @Column(name = "Loai_giam", nullable = false)
    private Integer loaiGiam;

    @Column(name = "Gia_tri_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "Giam_toi_da", precision = 18, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "Don_hang_toi_thieu", precision = 18, scale = 2)
    private BigDecimal donHangToiThieu;

    @Column(name = "Ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "Ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "Gio_bat_dau")
    private LocalTime gioBatDau;

    @Column(name = "Gio_ket_thuc")
    private LocalTime gioKetThuc;

    @Column(name = "Ap_dung_cung_nhieu_ctkm", nullable = false)
    private Boolean apDungCungNhieuCtkm = false;

    @Column(name = "Tu_dong_ap_dung", nullable = false)
    private Boolean tuDongApDung = false;

    @Column(name = "Tong_lien_hoa_don_ap_dung", columnDefinition = "nvarchar(max)")
    private String tongLienHoaDonApDung;

    @Column(name = "Ngay_trong_tuan", length = 50)
    private String ngayTrongTuan;

    @Column(name = "Ngay_trong_thang", columnDefinition = "nvarchar(max)")
    private String ngayTrongThang;

    /**
     * Khách hàng áp dụng:
     * 1 = Tất cả khách hàng
     * 2 = Theo nhóm khách hàng
     * 3 = Theo khách hàng cụ thể
     */
    @Column(name = "Khach_hang_ap_dung")
    private Integer khachHangApDung;

    @Column(name = "Kenh_ban_ap_dung", columnDefinition = "nvarchar(max)")
    private String kenhBanApDung;

    /**
     * Trạng thái:
     * 0 = Ngừng hoạt động
     * 1 = Hoạt động
     * 2 = Sắp diễn ra
     * 3 = Đã kết thúc
     */
    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "chuongTrinhKhuyenMai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChuongTrinhKhuyenMaiChiTiet> chiTietList;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
