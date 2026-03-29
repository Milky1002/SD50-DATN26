package com.example.sd50datn.Service;

import com.example.sd50datn.Dto.OrderSummaryDTO;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface OrderService {

    List<OrderSummaryDTO> getOrderSummaries();

    void deleteOrder(Integer id);

    void updateOrderStatus(Integer id, Integer status);

    ByteArrayInputStream exportOrdersPdf();
}

