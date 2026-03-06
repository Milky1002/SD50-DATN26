package com.example.sd50datn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DashboardSalesModel {

    private final long totalOrders;
    private final BigDecimal totalRevenue;
}
