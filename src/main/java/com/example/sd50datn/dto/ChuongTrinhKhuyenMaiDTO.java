package com.example.sd50datn.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhKhuyenMaiDTO {

    private Integer id;
    private String maChuongTrinh;
    private String tenChuongTrinh;
    private String moTa;
    private Integer loaiKhuyenMai; // 1: Giảm giá hóa đơn, 2: Giảm giá sản phẩm
    private Integer loaiGiam; // 1: Theo %, 2: Theo tiền
    private BigDecimal giaTriGiam;
    private BigDecimal giamToiDa;
    private BigDecimal donHangToiThieu;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private Boolean apDungCungNhieuCtkm;
    private Boolean tuDongApDung;
    private String tongLienHoaDonApDung;
    private String ngayTrongTuan;
    private String ngayTrongThang;
    private Integer khachHangApDung;
    private String kenhBanApDung;
    private Integer trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private List<ChuongTrinhKhuyenMaiChiTietDTO> chiTietList;
}
