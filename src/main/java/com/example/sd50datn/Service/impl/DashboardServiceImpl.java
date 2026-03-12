package com.example.sd50datn.Service.impl;

import com.example.sd50datn.Repository.DashboardRepository;
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

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    @Override
    public DashboardViewModel getDashboardView() {
        LocalDate currentToDate = dashboardRepository.fetchLatestInvoiceDate().orElse(LocalDate.now());
        LocalDate currentFromDate = dashboardRepository.fetchEarliestInvoiceDate().orElse(currentToDate);
        if (currentFromDate.isAfter(currentToDate)) {
            currentFromDate = currentToDate;
        }

        long rangeDays = ChronoUnit.DAYS.between(currentFromDate, currentToDate);
        LocalDate previousToDate = currentFromDate.minusDays(1);
        LocalDate previousFromDate = previousToDate.minusDays(rangeDays);

        List<DashboardRevenuePointModel> currentSeries = fillMissingDays(
                dashboardRepository.fetchDailyRevenue(currentFromDate, currentToDate),
                currentFromDate,
                currentToDate
        );

        List<DashboardRevenuePointModel> previousSeries = fillMissingDays(
                dashboardRepository.fetchDailyRevenue(previousFromDate, previousToDate),
                previousFromDate,
                previousToDate
        );

        return new DashboardViewModel(
                dashboardRepository.fetchSalesByRange(currentFromDate, currentToDate),
                dashboardRepository.fetchOperationStats(currentFromDate, currentToDate),
                currentSeries,
                previousSeries,
                currentToDate,
                currentFromDate,
                currentToDate,
                previousFromDate,
                previousToDate
        );
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
