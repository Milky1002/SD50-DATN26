package com.example.sd50datn.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderSummaryDTO {

    private final Integer id;
    private final String customerName;
    private final String customerPhone;
    private final LocalDateTime createdAt;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal totalAmount;
    private final Integer paymentStatus; // raw int from DB
    private final Integer orderStatus;   // raw int from DB

    public String getCode() {
        if (id == null) {
            return "#DH00000";
        }
        return String.format("#DH%05d", id);
    }

    public String getDisplayPaymentStatus() {
        if (paymentStatus == null) {
            return "Chưa thanh toán";
        }
        return switch (paymentStatus) {
            case 1 -> "Đã thanh toán";
            case 2 -> "Chờ thanh toán";
            default -> "Chưa thanh toán";
        };
    }

    public String getDisplayShippingStatus() {
        if (orderStatus == null) {
            return "Chờ xử lý";
        }
        return switch (orderStatus) {
            case 1 -> "Đang giao";
            case 2 -> "Hoàn tất";
            case 3 -> "Đã hủy";
            default -> "Chờ xử lý";
        };
    }

    public String getTabKey() {
        if (orderStatus == null) {
            return "waiting";
        }
        return switch (orderStatus) {
            case 1 -> "shipping";
            case 2 -> "done";
            case 3 -> "cancelled";
            default -> "waiting";
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

