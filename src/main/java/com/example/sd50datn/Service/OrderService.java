package com.example.sd50datn.Service;

import com.example.sd50datn.dto.OrderSummaryDTO;

import java.util.List;

public interface OrderService {

    List<OrderSummaryDTO> getOrderSummaries();

    void deleteOrder(Integer id);

    void updateOrderStatus(Integer id, Integer status);
}

