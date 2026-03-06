package com.example.sd50datn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvoiceStatsDTO {

    private final long totalInvoices;
    private final long waitingPayment;
    private final long completed;

    public int getCompletedRate() {
        if (totalInvoices <= 0) {
            return 0;
        }
        return (int) Math.round((completed * 100.0) / totalInvoices);
    }
}

