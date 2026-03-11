package com.example.sd50datn.Controller;

import com.example.sd50datn.Dto.StaffDTO;
import com.example.sd50datn.Model.Staff;
import com.example.sd50datn.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.List;

@Controller
@RequestMapping("/nhan-vien")
public class








StaffController {
    @Autowired private StaffService staffService;

    @GetMapping("")
    public String index(Model model,
                        @RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "status", required = false) Integer status) {

        model.addAttribute("listNhanVien", staffService.searchStaff(keyword, status));
        model.addAttribute("listPositions", staffService.getAllPositions());

        // Gửi lại giá trị tìm kiếm để hiển thị trên input/select sau khi load trang
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "QlyNhanVien/Staff";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute StaffDTO staffDTO) {
        staffService.saveOrUpdate(staffDTO);
        return "redirect:/nhan-vien";
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public Staff edit(@PathVariable Integer id) {
        // Trả về Entity để JS điền vào form
        return staffService.findEntityById(id);
    }
    @GetMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Integer id) {
        staffService.toggleStatus(id);
        return "redirect:/nhan-vien"; // Sau khi đổi xong thì quay lại trang danh sách
    }
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportExcel(@RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) Integer status) {
        // Lấy danh sách đã lọc (tận dụng hàm search có sẵn)
        List<StaffDTO> list = staffService.searchStaff(keyword, status);

        ByteArrayInputStream in = staffService.exportStaffToExcel(list);

        String filename = "Danh_sach_nhan_vien.xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}