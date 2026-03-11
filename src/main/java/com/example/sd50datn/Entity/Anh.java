package com.example.sd50datn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Anh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Anh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Anh_id")
    private Integer id;

    @Column(name = "Anh_url", nullable = false, columnDefinition = "nvarchar(max)")
    private String anhUrl;

    @Column(name = "Mo_ta", columnDefinition = "nvarchar(max)")
    private String moTa;

    @Column(name = "Thu_tu", nullable = false)
    private Integer thuTu = 0;

    @Column(name = "Trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Column(name = "Ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
}
