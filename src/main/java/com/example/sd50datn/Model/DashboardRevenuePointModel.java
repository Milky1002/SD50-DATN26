package com.example.sd50datn.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DashboardRevenuePointModel {

    private final LocalDate date;
    private final BigDecimal revenue;
}
