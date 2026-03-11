package com.example.sd50datn.Controller;

import com.example.sd50datn.Dto.ApiResponse;
import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiDTO;
import com.example.sd50datn.Dto.ChuongTrinhKhuyenMaiRequest;
import com.example.sd50datn.Service.ChuongTrinhKhuyenMaiService;
import com.example.sd50datn.Dto.*;
import com.example.sd50datn.Service.ChuongTrinhKhuyenMaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chuong-trinh-khuyen-mai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChuongTrinhKhuyenMaiController {

    private final ChuongTrinhKhuyenMaiService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChuongTrinhKhuyenMaiDTO>>> getAllPromotions() {
        try {
            List<ChuongTrinhKhuyenMaiDTO> promotions = service.getAllPromotions();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách chương trình khuyến mại thành công", promotions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách: " + e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ChuongTrinhKhuyenMaiDTO>>> getActivePromotions() {
        try {
            List<ChuongTrinhKhuyenMaiDTO> promotions = service.getActivePromotions();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách chương trình đang hoạt động thành công", promotions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{loaiKhuyenMai}")
    public ResponseEntity<ApiResponse<List<ChuongTrinhKhuyenMaiDTO>>> getPromotionsByType(
            @PathVariable Integer loaiKhuyenMai) {
        try {
            List<ChuongTrinhKhuyenMaiDTO> promotions = service.getPromotionsByType(loaiKhuyenMai);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách theo loại thành công", promotions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChuongTrinhKhuyenMaiDTO>> getPromotionById(@PathVariable Integer id) {
        try {
            ChuongTrinhKhuyenMaiDTO promotion = service.getPromotionById(id);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin chương trình thành công", promotion));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin: " + e.getMessage()));
        }
    }

    @GetMapping("/code/{maChuongTrinh}")
    public ResponseEntity<ApiResponse<ChuongTrinhKhuyenMaiDTO>> getPromotionByCode(
            @PathVariable String maChuongTrinh) {
        try {
            ChuongTrinhKhuyenMaiDTO promotion = service.getPromotionByCode(maChuongTrinh);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin chương trình thành công", promotion));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChuongTrinhKhuyenMaiDTO>> createPromotion(
            @Valid @RequestBody ChuongTrinhKhuyenMaiRequest request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ: " + errors));
        }

        try {
            ChuongTrinhKhuyenMaiDTO promotion = service.createPromotion(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo chương trình khuyến mại thành công", promotion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo chương trình: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChuongTrinhKhuyenMaiDTO>> updatePromotion(
            @PathVariable Integer id,
            @Valid @RequestBody ChuongTrinhKhuyenMaiRequest request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ: " + errors));
        }

        try {
            ChuongTrinhKhuyenMaiDTO promotion = service.updatePromotion(id, request);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật chương trình thành công", promotion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable Integer id) {
        try {
            service.deletePromotion(id);
            return ResponseEntity.ok(ApiResponse.success("Xóa chương trình thành công", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xóa: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ChuongTrinhKhuyenMaiDTO>> updatePromotionStatus(
            @PathVariable Integer id,
            @RequestParam Integer trangThai) {
        try {
            ChuongTrinhKhuyenMaiDTO promotion = service.updatePromotionStatus(id, trangThai);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", promotion));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật trạng thái: " + e.getMessage()));
        }
    }

    @GetMapping("/applicable/invoice")
    public ResponseEntity<ApiResponse<List<ChuongTrinhKhuyenMaiDTO>>> getApplicablePromotionsForInvoice(
            @RequestParam BigDecimal orderTotal) {
        try {
            List<ChuongTrinhKhuyenMaiDTO> promotions = service.getApplicablePromotionsForInvoice(orderTotal);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khuyến mại áp dụng cho hóa đơn thành công", promotions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách: " + e.getMessage()));
        }
    }

    @GetMapping("/applicable/product/{sanPhamId}")
    public ResponseEntity<ApiResponse<List<ChuongTrinhKhuyenMaiDTO>>> getApplicablePromotionsForProduct(
            @PathVariable Integer sanPhamId) {
        try {
            List<ChuongTrinhKhuyenMaiDTO> promotions = service.getApplicablePromotionsForProduct(sanPhamId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khuyến mại áp dụng cho sản phẩm thành công", promotions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách: " + e.getMessage()));
        }
    }

    @GetMapping("/{promotionId}/calculate-discount")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateDiscountForInvoice(
            @PathVariable Integer promotionId,
            @RequestParam BigDecimal orderTotal) {
        try {
            BigDecimal discount = service.calculateDiscountForInvoice(promotionId, orderTotal);
            return ResponseEntity.ok(ApiResponse.success("Tính toán giảm giá thành công", discount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tính toán: " + e.getMessage()));
        }
    }
}
