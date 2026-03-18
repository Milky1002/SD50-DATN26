package com.example.sd50datn.Service;

import com.example.sd50datn.Dto.StaffDTO;
import com.example.sd50datn.Model.*;
import com.example.sd50datn.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {
    @Autowired private StaffRepository staffRepo;
    @Autowired private PositionRepository posRepo;
    @Autowired private AccountRepository accRepo;

    // Lấy danh sách chức vụ cho Dropdown
    public List<Position> getAllPositions() {
        return posRepo.findAll();
    }

    // Chuyển đổi Entity sang DTO để hiển thị
    public List<StaffDTO> getAllStaffDTO() {
        return staffRepo.findAll().stream().map(s -> {
            // Tìm tên chức vụ (giả định bạn có bảng ChucVu)
            String tenCV = posRepo.findById(s.getChucVuId())
                    .map(Position::getTenChucVu)
                    .orElse("N/A");
            return StaffDTO.builder()
                    .id(s.getId())
                    .hoTen(s.getHoTen())
                    .gioiTinh(s.getGioiTinh())
                    .sdt(s.getSdt())
                    .email(s.getEmail())
                    .ngaySinh(s.getNgaySinh() != null ? s.getNgaySinh().toString() : "")
                    .trangThai(s.getTrangThai())
                    .tenChucVu(tenCV)
                    .chucVuId(s.getChucVuId())
                    .diaChi(s.getDiaChi())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void saveOrUpdate(StaffDTO dto) {
        Staff staff;
        if (dto.getId() != null) {
            // LÀM MỚI: Lấy nhân viên hiện tại từ DB để giữ lại các trường không sửa (như Ngay_tao)
            staff = staffRepo.findById(dto.getId()).orElse(new Staff());
        } else {
            staff = new Staff();
            // XỬ LÝ TÀI KHOẢN: Chỉ tạo khi thêm mới và có nhập Username
            if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
                Account acc = Account.builder()
                        .username(dto.getUsername())
                        .password("123456") // Mật khẩu mặc định
                        .trangThai(1)
                        .build();
                acc = accRepo.save(acc);
                staff.setTaiKhoanId(acc.getId());
            }
            // Mặc định trạng thái khi thêm mới là 1 (Đang làm)
            staff.setTrangThai(1);
        }

        // Cập nhật thông tin từ DTO sang Entity
        staff.setHoTen(dto.getHoTen());
        staff.setGioiTinh(dto.getGioiTinh());
        staff.setSdt(dto.getSdt());
        staff.setEmail(dto.getEmail());
        staff.setDiaChi(dto.getDiaChi());
        if (dto.getNgaySinh() != null && !dto.getNgaySinh().isEmpty()) {
            staff.setNgaySinh(LocalDate.parse(dto.getNgaySinh()));
        }
        staff.setChucVuId(dto.getChucVuId());

        // FIX LỖI 515: Nếu DTO có gửi trạng thái về thì lấy, nếu không (khi sửa) thì giữ nguyên cũ
        if (dto.getTrangThai() != null) {
            staff.setTrangThai(dto.getTrangThai());
        }

        staffRepo.save(staff);
    }

    public Staff findEntityById(Integer id) {
        return staffRepo.findById(id).orElse(null);
    }

    @Transactional
    public void toggleStatus(Integer id) {
        Staff s = staffRepo.findById(id).orElse(null);
        if (s != null) {
            s.setTrangThai(s.getTrangThai() == 1 ? 0 : 1);
            staffRepo.save(s);
        }
    }
    public List<StaffDTO> searchStaff(String keyword, Integer status) {
        // Nếu keyword rỗng thì coi như là null để Query hoạt động đúng
        String searchKey = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        List<Staff> list = staffRepo.searchStaff(searchKey, status);

        return list.stream().map(s -> {
            String tenCV = posRepo.findById(s.getChucVuId())
                    .map(Position::getTenChucVu).orElse("N/A");
            return StaffDTO.builder()
                    .id(s.getId())
                    .hoTen(s.getHoTen())
                    .sdt(s.getSdt())
                    .email(s.getEmail())
                    .trangThai(s.getTrangThai())
                    .tenChucVu(tenCV)
                    .chucVuId(s.getChucVuId())
                    .build();
        }).collect(Collectors.toList());
    }

    public ByteArrayInputStream exportStaffToExcel(List<StaffDTO> list) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh sách nhân viên");

            // 1. Tạo Header
            String[] headers = {"ID", "Họ tên", "Giới tính", "Số điện thoại", "Địa chỉ", "Email", "Chức vụ", "Trạng thái"};
            Row headerRow = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. Dữ liệu
            int rowIdx = 1;
            for (StaffDTO nv : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nv.getId());
                row.createCell(1).setCellValue(nv.getHoTen() != null ? nv.getHoTen() : "");
                row.createCell(2).setCellValue(nv.getGioiTinh() != null ? nv.getGioiTinh() : "");
                row.createCell(3).setCellValue(nv.getSdt() != null ? nv.getSdt() : "");
                row.createCell(4).setCellValue(nv.getDiaChi() != null ? nv.getDiaChi() : "");
                row.createCell(5).setCellValue(nv.getEmail() != null ? nv.getEmail() : "");
                row.createCell(6).setCellValue(nv.getTenChucVu() != null ? nv.getTenChucVu() : "");
                row.createCell(7).setCellValue(nv.getTrangThai() != null && nv.getTrangThai() == 1 ? "Đang làm" : "Nghỉ việc");
            }

            // Tự động căn chỉnh độ rộng cột
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }
}