package com.example.sd50datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhKhuyenMaiChiTietDTO {

    private Integer id;
    private Integer chuongTrinhKhuyenMaiId;
    private Integer sanPhamId;
    private String tenSanPham;
    private String maSanPham;
    private Integer danhMucSanPhamId;
    private String tenDanhMuc;
    private Integer soLuongToiThieu;
    private Integer soLuongToiDa;
    private BigDecimal giaTriGiam;
    private Integer trangThai;
}
