package com.example.sd50datn.Service.impl;

import com.example.sd50datn.Entity.SanPham;
import com.example.sd50datn.Repository.DashboardRepository;
import com.example.sd50datn.Repository.SanPhamRepository;
import com.example.sd50datn.Service.DashboardService;
import com.example.sd50datn.Model.DashboardRevenuePointModel;
import com.example.sd50datn.Model.DashboardViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final SanPhamRepository sanPhamRepository;

    @Override
    public DashboardViewModel getDashboardView() {
        LocalDate dataReferenceDate = dashboardRepository.fetchLatestInvoiceDate().orElse(LocalDate.now());

        LocalDate currentFromDate7Days = dataReferenceDate.minusDays(6);
        LocalDate currentToDate7Days = dataReferenceDate;
        long rangeDays7 = ChronoUnit.DAYS.between(currentFromDate7Days, currentToDate7Days);
        LocalDate previousToDate7Days = currentFromDate7Days.minusDays(1);
        LocalDate previousFromDate7Days = previousToDate7Days.minusDays(rangeDays7);

        LocalDate currentFromDate30Days = dataReferenceDate.minusDays(29);
        LocalDate currentToDate30Days = dataReferenceDate;
        long rangeDays30 = ChronoUnit.DAYS.between(currentFromDate30Days, currentToDate30Days);
        LocalDate previousToDate30Days = currentFromDate30Days.minusDays(1);
        LocalDate previousFromDate30Days = previousToDate30Days.minusDays(rangeDays30);

        List<DashboardRevenuePointModel> currentSeries7Days = fillMissingDays(
                dashboardRepository.fetchDailyRevenue(currentFromDate7Days, currentToDate7Days),
                currentFromDate7Days,
                currentToDate7Days
        );

        List<DashboardRevenuePointModel> previousSeries7Days = fillMissingDays(
                dashboardRepository.fetchDailyRevenue(previousFromDate7Days, previousToDate7Days),
                previousFromDate7Days,
                previousToDate7Days
        );

        List<DashboardRevenuePointModel> currentSeries30Days = fillMissingDays(
                dashboardRepository.fetchDailyRevenue(currentFromDate30Days, currentToDate30Days),
                currentFromDate30Days,
                currentToDate30Days
        );

        List<DashboardRevenuePointModel> previousSeries30Days = fillMissingDays(
                dashboardRepository.fetchDailyRevenue(previousFromDate30Days, previousToDate30Days),
                previousFromDate30Days,
                previousToDate30Days
        );

        return new DashboardViewModel(
                dashboardRepository.fetchSalesByRange(currentFromDate7Days, currentToDate7Days),
                dashboardRepository.fetchOperationStats(currentFromDate7Days, currentToDate7Days),
                dashboardRepository.fetchSalesByRange(currentFromDate30Days, currentToDate30Days),
                dashboardRepository.fetchOperationStats(currentFromDate30Days, currentToDate30Days),
                currentSeries7Days,
                previousSeries7Days,
                currentSeries30Days,
                previousSeries30Days,
                dataReferenceDate,
                currentFromDate7Days,
                currentToDate7Days,
                previousFromDate7Days,
                previousToDate7Days,
                currentFromDate30Days,
                currentToDate30Days,
                previousFromDate30Days,
                previousToDate30Days
        );
    }

    @Override
    public List<DashboardRepository.TopSellingProjection> getTopSellingProducts(LocalDate from, LocalDate to) {
        return dashboardRepository.findTopSellingProducts(from, to);
    }

    @Override
    public List<SanPham> getLowStockProducts(int threshold) {
        return sanPhamRepository.findAll().stream()
                .filter(sp -> sp.getSoLuongTon() != null && sp.getSoLuongTon() <= threshold && sp.getTrangThai() != null && sp.getTrangThai() == 1)
                .collect(Collectors.toList());
    }

    private List<DashboardRevenuePointModel> fillMissingDays(List<DashboardRevenuePointModel> raw,
                                                             LocalDate fromDate,
                                                             LocalDate toDate) {
        Map<LocalDate, BigDecimal> revenueByDate = new HashMap<>();
        for (DashboardRevenuePointModel point : raw) {
            if (point.getDate() != null) {
                revenueByDate.put(point.getDate(), point.getRevenue() != null ? point.getRevenue() : BigDecimal.ZERO);
            }
        }

        List<DashboardRevenuePointModel> result = new ArrayList<>();
        LocalDate cursor = fromDate;
        while (!cursor.isAfter(toDate)) {
            result.add(new DashboardRevenuePointModel(cursor, revenueByDate.getOrDefault(cursor, BigDecimal.ZERO)));
            cursor = cursor.plusDays(1);
        }
        return result;
    }
}
