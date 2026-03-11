package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Danh_muc_san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhMucSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Danh_muc_san_pham_id")
    private Integer danhMucSanPhamId;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 255, message = "Tên danh mục phải từ 2 đến 255 ký tự")
    @Column(name = "Ten_danh_muc", nullable = false)
    private String tenDanhMuc;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai;

    @Column(name = "Ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDate ngayCapNhat;

    // Tự set ngày tạo khi insert
    @PrePersist
    public void prePersist() {
        this.ngayTao = LocalDate.now();
    }

    // Tự set ngày cập nhật khi update
    @PreUpdate
    public void preUpdate() {
        this.ngayCapNhat = LocalDate.now();
    }
}