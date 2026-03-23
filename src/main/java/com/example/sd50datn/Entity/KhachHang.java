package com.example.sd50datn.Entity;

import com.example.sd50datn.Model.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "Khach_hang")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Khach_hang_id")
    private Integer khachHangId;

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Column(name = "Ten_khach_hang", nullable = false)
    private String tenKhachHang;

    @NotBlank(message = "SĐT không được để trống")
    @Pattern(regexp = "0[0-9]{9}", message = "SĐT phải 10 số")
    @Column(name = "SDT")
    private String sdt;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "Email")
    private String email;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(name = "Dia_chi_khach_hang")
    private String diaChiKhachHang;

    @Column(name = "Mat_khau")
    private String matKhau;

    @Column(name = "Tai_khoan_id")
    private Integer taiKhoanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Tai_khoan_id", insertable = false, updatable = false)
    private Account taiKhoan;

    public boolean hasLinkedAccount() {
        return taiKhoanId != null;
    }
}
