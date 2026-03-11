package com.example.sd50datn.Dto;

import jakarta.validation.constraints.*;
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
public class ChuongTrinhKhuyenMaiRequest {

    @NotBlank(message = "Mã chương trình không được để trống")
    @Size(max = 50, message = "Mã chương trình không được vượt quá 50 ký tự")
    private String maChuongTrinh;

    @NotBlank(message = "Tên chương trình không được để trống")
    @Size(max = 255, message = "Tên chương trình không được vượt quá 255 ký tự")
    private String tenChuongTrinh;

    private String moTa;

    @NotNull(message = "Loại khuyến mại không được để trống")
    @Min(value = 1, message = "Loại khuyến mại không hợp lệ")
    @Max(value = 4, message = "Loại khuyến mại không hợp lệ")
    private Integer loaiKhuyenMai;

    @NotNull(message = "Loại giảm không được để trống")
    @Min(value = 1, message = "Loại giảm không hợp lệ")
    @Max(value = 2, message = "Loại giảm không hợp lệ")
    private Integer loaiGiam;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal giaTriGiam;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giảm tối đa phải lớn hơn 0")
    private BigDecimal giamToiDa;

    @DecimalMin(value = "0.0", message = "Đơn hàng tối thiểu phải >= 0")
    private BigDecimal donHangToiThieu;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime ngayKetThuc;

    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    private Boolean apDungCungNhieuCtkm = false;
    private Boolean tuDongApDung = false;
    private String tongLienHoaDonApDung;
    private String ngayTrongTuan;
    private String ngayTrongThang;
    private Integer khachHangApDung;
    private String kenhBanApDung;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

    private List<ChuongTrinhKhuyenMaiChiTietRequest> chiTietList;
}
