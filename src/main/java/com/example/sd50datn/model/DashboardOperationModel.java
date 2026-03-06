package com.example.sd50datn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardOperationModel {

    private final long completedOrders;
    private final long returnedOrders;
    private final long canceledOrders;

    public long getTotalOrders() {
        return completedOrders + returnedOrders + canceledOrders;
    }
}
