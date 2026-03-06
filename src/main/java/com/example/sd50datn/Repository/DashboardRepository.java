package com.example.sd50datn.Repository;

import com.example.sd50datn.Entity.HoaDon;
import com.example.sd50datn.model.DashboardOperationModel;
import com.example.sd50datn.model.DashboardRevenuePointModel;
import com.example.sd50datn.model.DashboardSalesModel;
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

        Long getReturnedOrders();

        Long getCanceledOrders();
    }

    interface DailyRevenueProjection {
        LocalDate getReportDate();

        BigDecimal getRevenue();
    }

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
                    WHERE CAST(h.Ngay_tao AS date) = :reportDate
                    """,
            nativeQuery = true
    )
    SalesProjection findSalesByDate(@Param("reportDate") LocalDate reportDate);

    @Query(
            value = """
                    SELECT
                        SUM(CASE WHEN h.Trang_thai = 2 THEN 1 ELSE 0 END) AS completedOrders,
                        SUM(CASE WHEN h.Trang_thai IN (4, 5) THEN 1 ELSE 0 END) AS returnedOrders,
                        SUM(CASE WHEN h.Trang_thai = 3 THEN 1 ELSE 0 END) AS canceledOrders
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
                    GROUP BY CAST(h.Ngay_tao AS date)
                    ORDER BY CAST(h.Ngay_tao AS date)
                    """,
            nativeQuery = true
    )
    List<DailyRevenueProjection> findDailyRevenue(@Param("fromDate") LocalDate fromDate,
                                                  @Param("toDate") LocalDate toDate);

    default Optional<LocalDate> fetchLatestInvoiceDate() {
        return Optional.ofNullable(findLatestInvoiceDate());
    }

    default DashboardSalesModel fetchSalesByDate(LocalDate reportDate) {
        SalesProjection projection = findSalesByDate(reportDate);
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
        long returned = projection.getReturnedOrders() != null ? projection.getReturnedOrders() : 0;
        long canceled = projection.getCanceledOrders() != null ? projection.getCanceledOrders() : 0;
        return new DashboardOperationModel(completed, returned, canceled);
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
