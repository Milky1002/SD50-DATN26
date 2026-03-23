package com.example.sd50datn.Model;

import com.example.sd50datn.Entity.KhachHang;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TaiKhoan", schema = "dbo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tai_khoan_id")
    private Integer id;

    @Column(name = "User_name", unique = true, nullable = false)
    private String username;

    @Column(name = "Pass_word", nullable = false)
    private String password;

    @Column(name = "Trang_thai")
    private Integer trangThai = 1;

    /** USER | STAFF | ADMIN */
    @Column(name = "Role_code")
    private String roleCode = "STAFF";

    @Column(name = "Email")
    private String email;

    @Column(name = "Ho_ten")
    private String hoTen;

    @Column(name = "So_dien_thoai")
    private String soDienThoai;

    @Column(name = "Ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "Ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "taiKhoan", fetch = FetchType.LAZY)
    private List<KhachHang> khachHangs;
}
