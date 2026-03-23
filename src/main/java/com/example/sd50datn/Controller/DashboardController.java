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

        model.addAttribute("salesOverview", dashboardView.getSalesOverview());
        model.addAttribute("operationOverview", dashboardView.getOperationOverview());

        model.addAttribute("currentRevenueLabels", dashboardView.getCurrentRevenueSeries().stream()
                .map(p -> p.getDate().format(dayLabelFormatter))
                .toList());
        model.addAttribute("currentRevenueValues", toNumbers(dashboardView.getCurrentRevenueSeries()));
        model.addAttribute("previousRevenueValues", toNumbers(dashboardView.getPreviousRevenueSeries()));

        model.addAttribute("currentRangeLabel",
                dashboardView.getCurrentFromDate().format(rangeFormatter) + " - " +
                        dashboardView.getCurrentToDate().format(rangeFormatter));
        model.addAttribute("previousRangeLabel",
                dashboardView.getPreviousFromDate().format(rangeFormatter) + " - " +
                        dashboardView.getPreviousToDate().format(rangeFormatter));
        model.addAttribute("salesDateLabel",
                dashboardView.getCurrentFromDate().format(rangeFormatter) + " - " +
                        dashboardView.getCurrentToDate().format(rangeFormatter));

        return "layout";
    }

    private List<Double> toNumbers(List<DashboardRevenuePointModel> points) {
        return points.stream()
                .map(DashboardRevenuePointModel::getRevenue)
                .map(value -> value != null ? value.doubleValue() : 0d)
                .toList();
    }
}
