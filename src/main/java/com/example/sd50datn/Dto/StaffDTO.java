package com.example.sd50datn.Dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffDTO {
    private Integer id;
    private String hoTen;
    private String gioiTinh;
    private String sdt;
    private String email;
    private String diaChi;
    private String ngaySinh; // yyyy-MM-dd
    private Integer chucVuId;
    private String tenChucVu;
    private Integer trangThai;
    private String username; // Thêm để tạo tài khoản
}