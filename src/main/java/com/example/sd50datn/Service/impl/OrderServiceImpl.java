package com.example.sd50datn.Service.impl;


import com.example.sd50datn.Dto.OrderSummaryDTO;
import com.example.sd50datn.Repository.OrderRepository;
import com.example.sd50datn.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<OrderSummaryDTO> getOrderSummaries() {
        log.info("Bắt đầu lấy danh sách đơn hàng"); // Log đơn giản, hiệu quả
        try {
            List<OrderSummaryDTO> result = orderRepository.fetchOrderSummaries();
            log.info("Lấy thành công, kích thước: {}", result != null ? result.size() : 0);
            return result;
        } catch (Exception ex) {
            log.error("Lỗi khi lấy danh sách đơn hàng: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteOrder(Integer id) {
        if (id == null) {
            return;
        }
        orderRepository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(Integer id, Integer status) {
        if (id == null || status == null) {
            return;
        }
        orderRepository.findById(id).ifPresent(order -> {
            order.setTrangThai(status);
            orderRepository.save(order);
        });
    }
}

