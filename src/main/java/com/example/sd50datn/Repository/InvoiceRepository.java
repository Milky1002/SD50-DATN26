package com.example.sd50datn.Repository;

import com.example.sd50datn.Dto.InvoiceStatsDTO;
import com.example.sd50datn.Dto.InvoiceSummaryDTO;
import com.example.sd50datn.Entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
        Long getTotalInvoices();

        Long getWaitingPayment();

        Long getCompleted();
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
                    SELECT TOP 100
                        h.Hoa_don_id      AS id,
                        h.Ten_khach_hang  AS customerName,
                        h.Sdt_khach_hang  AS customerPhone,
                        h.Ngay_tao        AS createdAt,
                        h.Tong_tien_sau_khi_giam AS totalAmount,
                        ISNULL(tt.Trang_thai, 0) AS paymentStatus
                    FROM HoaDon h
                    LEFT JOIN ThanhToan tt ON tt.Hoa_don_id = h.Hoa_don_id
                    WHERE
                        (:keyword IS NULL OR :keyword = '' OR h.Ten_khach_hang LIKE N'%' + :keyword + '%'
                         OR h.Sdt_khach_hang LIKE N'%' + :keyword + '%'
                         OR (:idKeyword IS NOT NULL AND CAST(h.Hoa_don_id AS NVARCHAR(20)) LIKE '%' + :idKeyword + '%'))
                    ORDER BY h.Ngay_tao DESC
                    """,
            nativeQuery = true
    )
    List<InvoiceRowProjection> findInvoiceSummariesByKeyword(@Param("keyword") String keyword,
                                                             @Param("idKeyword") String idKeyword);

    @Query(
            value = """
                    SELECT 
                        COUNT(*) AS totalInvoices,
                        ISNULL(SUM(CASE WHEN ISNULL(tt.Trang_thai, 0) = 0 THEN 1 ELSE 0 END), 0) AS waitingPayment,
                        ISNULL(SUM(CASE WHEN ISNULL(tt.Trang_thai, 0) = 1 THEN 1 ELSE 0 END), 0) AS completed
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

    default List<InvoiceSummaryDTO> fetchInvoiceSummariesByKeyword(String keyword, String idKeyword) {
        return findInvoiceSummariesByKeyword(keyword, idKeyword).stream()
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
        return new InvoiceStatsDTO(
                p.getTotalInvoices() != null ? p.getTotalInvoices() : 0,
                p.getWaitingPayment() != null ? p.getWaitingPayment() : 0,
                p.getCompleted() != null ? p.getCompleted() : 0
        );
    }
}

