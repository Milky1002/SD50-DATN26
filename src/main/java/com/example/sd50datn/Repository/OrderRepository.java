package com.example.sd50datn.Repository;

import com.example.sd50datn.Dto.OrderSummaryDTO;
import com.example.sd50datn.Entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<HoaDon, Integer> {

    boolean existsByKhachHangId(Integer khachHangId);

    interface OrderRowProjection {
        Integer getId();

        String getCustomerName();

        String getCustomerPhone();

        java.time.LocalDateTime getCreatedAt();

        String getProductName();

        Integer getQuantity();

        java.math.BigDecimal getTotalAmount();

        Integer getPaymentStatus();

        Integer getOrderStatus();
    }

    @Query(
            value = """
                    SELECT TOP 50 
                        h.Hoa_don_id      AS id,
                        h.Ten_khach_hang  AS customerName,
                        h.Sdt_khach_hang  AS customerPhone,
                        h.Ngay_tao        AS createdAt,
                        sp.Ten_san_pham   AS productName,
                        c.So_luong_san_pham AS quantity,
                        h.Tong_tien_sau_khi_giam AS totalAmount,
                        ISNULL(tt.Trang_thai, 0) AS paymentStatus,
                        h.Trang_thai      AS orderStatus
                    FROM HoaDon h
                    LEFT JOIN HoaDonChiTiet c ON h.Hoa_don_id = c.Hoa_don_id
                    LEFT JOIN SanPham sp ON c.San_pham_id = sp.San_pham_id
                    LEFT JOIN ThanhToan tt ON tt.Hoa_don_id = h.Hoa_don_id
                    ORDER BY h.Ngay_tao DESC
                    """,
            nativeQuery = true
    )
    List<OrderRowProjection> findOrderSummaries();

    default List<OrderSummaryDTO> fetchOrderSummaries() {
        return findOrderSummaries().stream()
                .map(r -> new OrderSummaryDTO(
                        r.getId(),
                        r.getCustomerName(),
                        r.getCustomerPhone(),
                        r.getCreatedAt(),
                        r.getProductName(),
                        r.getQuantity(),
                        r.getTotalAmount(),
                        r.getPaymentStatus(),
                        r.getOrderStatus()
                ))
                .toList();
    }
}

