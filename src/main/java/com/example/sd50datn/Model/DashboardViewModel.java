package com.example.sd50datn.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardViewModel {

    private final DashboardSalesModel salesOverview;
    private final DashboardOperationModel operationOverview;
    private final DashboardSalesModel salesOverview30Days;
    private final DashboardOperationModel operationOverview30Days;
    private final List<DashboardRevenuePointModel> currentRevenueSeries7Days;
    private final List<DashboardRevenuePointModel> previousRevenueSeries7Days;
    private final List<DashboardRevenuePointModel> currentRevenueSeries30Days;
    private final List<DashboardRevenuePointModel> previousRevenueSeries30Days;
    private final LocalDate dataReferenceDate;
    private final LocalDate currentFromDate7Days;
    private final LocalDate currentToDate7Days;
    private final LocalDate previousFromDate7Days;
    private final LocalDate previousToDate7Days;
    private final LocalDate currentFromDate30Days;
    private final LocalDate currentToDate30Days;
    private final LocalDate previousFromDate30Days;
    private final LocalDate previousToDate30Days;
}
