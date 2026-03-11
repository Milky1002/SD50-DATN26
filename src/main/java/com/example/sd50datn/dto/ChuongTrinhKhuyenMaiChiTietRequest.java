package com.example.sd50datn.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhKhuyenMaiChiTietRequest {

    private Integer sanPhamId;
    private Integer danhMucSanPhamId;
    private Integer soLuongToiThieu;
    private Integer soLuongToiDa;
    private BigDecimal giaTriGiam;
    private Integer trangThai = 1;
}
