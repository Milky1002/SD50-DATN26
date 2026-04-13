package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.DashboardService;
import com.example.sd50datn.Model.DashboardRevenuePointModel;
import com.example.sd50datn.Model.DashboardViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardViewModel dashboardView = dashboardService.getDashboardView();

        DateTimeFormatter dayLabelFormatter = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter rangeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        model.addAttribute("pageTitle", "Tổng quan");
        model.addAttribute("pageHeading", "Bàn làm việc");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("content", "dashboard");
        model.addAttribute("pageCss", "/css/dashboard.css");

        model.addAttribute("salesDateLabel",
                dashboardView.getCurrentFromDate7Days().format(rangeFormatter) + " - " +
                        dashboardView.getCurrentToDate7Days().format(rangeFormatter));

        model.addAttribute("salesOverview", dashboardView.getSalesOverview());
        model.addAttribute("operationOverview", dashboardView.getOperationOverview());
        model.addAttribute("salesOverview30", dashboardView.getSalesOverview30Days());
        model.addAttribute("operationOverview30", dashboardView.getOperationOverview30Days());

        model.addAttribute("currentRevenueLabels7Days", dashboardView.getCurrentRevenueSeries7Days().stream()
                .map(p -> p.getDate().format(dayLabelFormatter))
                .toList());
        model.addAttribute("previousRevenueLabels7Days", dashboardView.getPreviousRevenueSeries7Days().stream()
                .map(p -> p.getDate().format(dayLabelFormatter))
                .toList());
        model.addAttribute("currentRevenueValues7Days", toNumbers(dashboardView.getCurrentRevenueSeries7Days()));
        model.addAttribute("previousRevenueValues7Days", toNumbers(dashboardView.getPreviousRevenueSeries7Days()));
        model.addAttribute("currentRevenueLabels30Days", dashboardView.getCurrentRevenueSeries30Days().stream()
                .map(p -> p.getDate().format(dayLabelFormatter))
                .toList());
        model.addAttribute("previousRevenueLabels30Days", dashboardView.getPreviousRevenueSeries30Days().stream()
                .map(p -> p.getDate().format(dayLabelFormatter))
                .toList());
        model.addAttribute("currentRevenueValues30Days", toNumbers(dashboardView.getCurrentRevenueSeries30Days()));
        model.addAttribute("previousRevenueValues30Days", toNumbers(dashboardView.getPreviousRevenueSeries30Days()));

        model.addAttribute("currentRangeLabel7Days",
                dashboardView.getCurrentFromDate7Days().format(rangeFormatter) + " - " +
                        dashboardView.getCurrentToDate7Days().format(rangeFormatter));
        model.addAttribute("previousRangeLabel7Days",
                dashboardView.getPreviousFromDate7Days().format(rangeFormatter) + " - " +
                        dashboardView.getPreviousToDate7Days().format(rangeFormatter));
        model.addAttribute("currentRangeLabel30Days",
                dashboardView.getCurrentFromDate30Days().format(rangeFormatter) + " - " +
                        dashboardView.getCurrentToDate30Days().format(rangeFormatter));
        model.addAttribute("previousRangeLabel30Days",
                dashboardView.getPreviousFromDate30Days().format(rangeFormatter) + " - " +
                        dashboardView.getPreviousToDate30Days().format(rangeFormatter));
        model.addAttribute("sales7DayLabel",
                dashboardView.getCurrentFromDate7Days().format(rangeFormatter) + " - " +
                        dashboardView.getCurrentToDate7Days().format(rangeFormatter));
        model.addAttribute("sales30DayLabel",
                dashboardView.getCurrentFromDate30Days().format(rangeFormatter) + " - " +
                        dashboardView.getCurrentToDate30Days().format(rangeFormatter));

        return "layout";
    }

    private List<Double> toNumbers(List<DashboardRevenuePointModel> points) {
        return points.stream()
                .map(DashboardRevenuePointModel::getRevenue)
                .map(value -> value != null ? value.doubleValue() : 0d)
                .toList();
    }
}
