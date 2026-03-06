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
    private final List<DashboardRevenuePointModel> currentRevenueSeries;
    private final List<DashboardRevenuePointModel> previousRevenueSeries;
    private final LocalDate dataReferenceDate;
    private final LocalDate currentFromDate;
    private final LocalDate currentToDate;
    private final LocalDate previousFromDate;
    private final LocalDate previousToDate;
}
