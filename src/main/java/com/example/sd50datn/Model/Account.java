package com.example.sd50datn.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
