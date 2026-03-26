package com.example.sd50datn.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.sd50datn.Util.OrderStatusUtil;

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
        return OrderStatusUtil.getLabel(orderStatus);
    }

    public String getTabKey() {
        return OrderStatusUtil.getTabKey(orderStatus);
    }

    public List<Integer> getAllowedTransitions() {
        return OrderStatusUtil.getAllowedTransitions(orderStatus);
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

