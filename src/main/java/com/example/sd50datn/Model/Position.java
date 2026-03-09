package com.example.sd50datn.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ChucVu", schema = "dbo")
@Data @NoArgsConstructor @AllArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Chuc_vu_id")
    private Integer id;

    @Column(name = "Ten_chuc_vu", nullable = false)
    private String tenChucVu;
}
