package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.Model.DashboardOperationModel;
import com.example.sd50datn.Model.DashboardRevenuePointModel;
import com.example.sd50datn.Model.DashboardSalesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DashboardRepository extends JpaRepository<HoaDon, Integer> {

    interface SalesProjection {
        Long getTotalOrders();

        BigDecimal getTotalRevenue();
    }

    interface OperationProjection {
        Long getCompletedOrders();

        Long getShippingOrders();

        Long getCanceledOrders();
    }

    interface DailyRevenueProjection {
        LocalDate getReportDate();

        BigDecimal getRevenue();
    }

    @Query(
            value = """
                    SELECT MIN(CAST(h.Ngay_tao AS date))
                    FROM HoaDon h
                    """,
            nativeQuery = true
    )
    LocalDate findEarliestInvoiceDate();

    @Query(
            value = """
                    SELECT MAX(CAST(h.Ngay_tao AS date))
                    FROM HoaDon h
                    """,
            nativeQuery = true
    )
    LocalDate findLatestInvoiceDate();

    @Query(
            value = """
                    SELECT
                        COUNT(*) AS totalOrders,
                        COALESCE(SUM(h.Tong_tien_sau_khi_giam), 0) AS totalRevenue
                    FROM HoaDon h
                    WHERE CAST(h.Ngay_tao AS date) BETWEEN :fromDate AND :toDate
                      AND h.Trang_thai = 3
                      AND EXISTS (
                          SELECT 1
                          FROM ThanhToan tt
                          WHERE tt.Hoa_don_id = h.Hoa_don_id
                            AND tt.Trang_thai = 1
                      )
                    """,
            nativeQuery = true
    )
    SalesProjection findSalesByRange(@Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate);

    @Query(
            value = """
                    SELECT
                        SUM(CASE WHEN h.Trang_thai = 3 THEN 1 ELSE 0 END) AS completedOrders,
                        SUM(CASE WHEN h.Trang_thai = 2 THEN 1 ELSE 0 END) AS shippingOrders,
                        SUM(CASE WHEN h.Trang_thai = 4 THEN 1 ELSE 0 END) AS canceledOrders
                    FROM HoaDon h
                    WHERE CAST(h.Ngay_tao AS date) BETWEEN :fromDate AND :toDate
                    """,
            nativeQuery = true
    )
    OperationProjection findOperationStats(@Param("fromDate") LocalDate fromDate,
                                           @Param("toDate") LocalDate toDate);

    @Query(
            value = """
                    SELECT
                        CAST(h.Ngay_tao AS date) AS reportDate,
                        COALESCE(SUM(h.Tong_tien_sau_khi_giam), 0) AS revenue
                    FROM HoaDon h
                    WHERE CAST(h.Ngay_tao AS date) BETWEEN :fromDate AND :toDate
                      AND h.Trang_thai = 3
                      AND EXISTS (
                          SELECT 1
                          FROM ThanhToan tt
                          WHERE tt.Hoa_don_id = h.Hoa_don_id
                            AND tt.Trang_thai = 1
                      )
                    GROUP BY CAST(h.Ngay_tao AS date)
                    ORDER BY CAST(h.Ngay_tao AS date)
                    """,
            nativeQuery = true
    )
    List<DailyRevenueProjection> findDailyRevenue(@Param("fromDate") LocalDate fromDate,
                                                  @Param("toDate") LocalDate toDate);

    // Top 5 best-selling products by quantity sold (HOAN_TAT = 3)
    interface TopSellingProjection {
        Integer getSanPhamId();
        String getTenSanPham();
        Long getTotalQty();
        BigDecimal getTotalRevenue();
    }

    @Query(
            value = """
                    SELECT TOP 5
                        ct.San_pham_id AS sanPhamId,
                        sp.Ten_san_pham AS tenSanPham,
                        SUM(ct.So_luong_san_pham) AS totalQty,
                        SUM(ct.So_luong_san_pham * ct.Gia) AS totalRevenue
                    FROM HoaDonChiTiet ct
                    JOIN SanPham sp ON sp.San_pham_id = ct.San_pham_id
                    JOIN HoaDon h ON h.Hoa_don_id = ct.Hoa_don_id
                    WHERE h.Trang_thai = 3
                      AND CAST(h.Ngay_tao AS date) BETWEEN :fromDate AND :toDate
                    GROUP BY ct.San_pham_id, sp.Ten_san_pham
                    ORDER BY SUM(ct.So_luong_san_pham) DESC
                    """,
            nativeQuery = true
    )
    List<TopSellingProjection> findTopSellingProducts(@Param("fromDate") LocalDate fromDate,
                                                      @Param("toDate") LocalDate toDate);

    default Optional<LocalDate> fetchLatestInvoiceDate() {
        return Optional.ofNullable(findLatestInvoiceDate());
    }

    default Optional<LocalDate> fetchEarliestInvoiceDate() {
        return Optional.ofNullable(findEarliestInvoiceDate());
    }

    default DashboardSalesModel fetchSalesByRange(LocalDate fromDate, LocalDate toDate) {
        SalesProjection projection = findSalesByRange(fromDate, toDate);
        if (projection == null) {
            return new DashboardSalesModel(0, BigDecimal.ZERO);
        }

        long totalOrders = projection.getTotalOrders() != null ? projection.getTotalOrders() : 0;
        BigDecimal totalRevenue = projection.getTotalRevenue() != null ? projection.getTotalRevenue() : BigDecimal.ZERO;
        return new DashboardSalesModel(totalOrders, totalRevenue);
    }

    default DashboardOperationModel fetchOperationStats(LocalDate fromDate, LocalDate toDate) {
        OperationProjection projection = findOperationStats(fromDate, toDate);
        if (projection == null) {
            return new DashboardOperationModel(0, 0, 0);
        }

        long completed = projection.getCompletedOrders() != null ? projection.getCompletedOrders() : 0;
        long shipping = projection.getShippingOrders() != null ? projection.getShippingOrders() : 0;
        long canceled = projection.getCanceledOrders() != null ? projection.getCanceledOrders() : 0;
        return new DashboardOperationModel(completed, shipping, canceled);
    }

    default List<DashboardRevenuePointModel> fetchDailyRevenue(LocalDate fromDate, LocalDate toDate) {
        return findDailyRevenue(fromDate, toDate).stream()
                .map(r -> new DashboardRevenuePointModel(
                        r.getReportDate(),
                        r.getRevenue() != null ? r.getRevenue() : BigDecimal.ZERO
                ))
                .toList();
    }
}
