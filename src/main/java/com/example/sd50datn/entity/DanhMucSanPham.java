package com.example.sd50datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Danh_muc_san_pham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhMucSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Danh_muc_san_pham_id")
    private Integer id;

    @Column(name = "Ten_danh_muc", nullable = false, unique = true, length = 255)
    private String tenDanhMuc;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;
}
