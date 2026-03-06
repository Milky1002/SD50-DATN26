package com.example.sd50datn.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InvoiceSummaryDTO {

    private final Integer id;
    private final String customerName;
    private final String customerPhone;
    private final LocalDateTime createdAt;
    private final BigDecimal totalAmount;
    private final Integer paymentStatus;

    public String getCode() {
        if (id == null) {
            return "#INV-00000";
        }
        return String.format("#INV-%05d", id);
    }

    public String getDisplayPaymentStatus() {
        if (paymentStatus == null) {
            return "Chưa thanh toán";
        }
        return switch (paymentStatus) {
            case 1 -> "Đã thanh toán";
            case 2 -> "Chưa thanh toán";
            case 3 -> "T.toán một phần";
            default -> "Chưa thanh toán";
        };
    }

    public String getInitials() {
        if (customerName == null || customerName.isBlank()) {
            return "--";
        }
        String[] parts = customerName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        String first = parts[0];
        String last = parts[parts.length - 1];
        return (first.substring(0, 1) + last.substring(0, 1)).toUpperCase();
    }
}

