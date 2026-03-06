package com.example.sd50datn.Repository;

import com.example.sd50datn.Dto.InvoiceStatsDTO;
import com.example.sd50datn.Dto.InvoiceSummaryDTO;
import com.example.sd50datn.Entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<HoaDon, Integer> {

    interface InvoiceRowProjection {
        Integer getId();

        String getCustomerName();

        String getCustomerPhone();

        java.time.LocalDateTime getCreatedAt();

        java.math.BigDecimal getTotalAmount();

        Integer getPaymentStatus();
    }

    interface InvoiceStatsProjection {
        long getTotalInvoices();

        long getWaitingPayment();

        long getCompleted();
    }

    @Query(
            value = """
                    SELECT TOP 100
                        h.Hoa_don_id      AS id,
                        h.Ten_khach_hang  AS customerName,
                        h.Sdt_khach_hang  AS customerPhone,
                        h.Ngay_tao        AS createdAt,
                        h.Tong_tien_sau_khi_giam AS totalAmount,
                        ISNULL(tt.Trang_thai, 0) AS paymentStatus
                    FROM HoaDon h
                    LEFT JOIN ThanhToan tt ON tt.Hoa_don_id = h.Hoa_don_id
                    ORDER BY h.Ngay_tao DESC
                    """,
            nativeQuery = true
    )
    List<InvoiceRowProjection> findInvoiceSummaries();

    @Query(
            value = """
                    SELECT 
                        COUNT(*) AS totalInvoices,
                        SUM(CASE WHEN ISNULL(tt.Trang_thai, 0) = 0 THEN 1 ELSE 0 END) AS waitingPayment,
                        SUM(CASE WHEN ISNULL(tt.Trang_thai, 0) = 1 THEN 1 ELSE 0 END) AS completed
                    FROM HoaDon h
                    LEFT JOIN ThanhToan tt ON tt.Hoa_don_id = h.Hoa_don_id
                    """,
            nativeQuery = true
    )
    InvoiceStatsProjection findInvoiceStats();

    default List<InvoiceSummaryDTO> fetchInvoiceSummaries() {
        return findInvoiceSummaries().stream()
                .map(r -> new InvoiceSummaryDTO(
                        r.getId(),
                        r.getCustomerName(),
                        r.getCustomerPhone(),
                        r.getCreatedAt(),
                        r.getTotalAmount(),
                        r.getPaymentStatus()
                ))
                .toList();
    }

    default InvoiceStatsDTO fetchInvoiceStats() {
        InvoiceStatsProjection p = findInvoiceStats();
        if (p == null) {
            return new InvoiceStatsDTO(0, 0, 0);
        }
        return new InvoiceStatsDTO(p.getTotalInvoices(), p.getWaitingPayment(), p.getCompleted());
    }
}

