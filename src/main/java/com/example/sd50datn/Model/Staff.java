package com.example.sd50datn.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "NhanVien", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Nhan_vien_id")
    private Integer id;

    @Column(name = "Ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "Gioi_tinh")
    private String gioiTinh;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "Email")
    private String email;

    @Column(name = "Dia_chi", columnDefinition = "NVARCHAR(MAX)")
    private String diaChi;

    @Column(name = "Ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "Chuc_vu_id", nullable = false)
    private Integer chucVuId;

    @Column(name = "Tai_khoan_id")
    private Integer taiKhoanId;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1; // Giá trị mặc định là 1

    @Column(name = "Ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        if (this.trangThai == null) this.trangThai = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}