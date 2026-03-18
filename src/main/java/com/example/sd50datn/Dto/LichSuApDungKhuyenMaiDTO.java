package com.example.sd50datn.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuApDungKhuyenMaiDTO {

    private Integer id;
    private Integer chuongTrinhKhuyenMaiId;
    private String maChuongTrinh;
    private String tenChuongTrinh;
    private Integer loaiKhuyenMai;
    private Integer hoaDonId;
    private String tenKhachHang;
    private BigDecimal tongTienHoaDon;
    private BigDecimal giaTriGiam;
    private LocalDateTime ngayApDung;
}
