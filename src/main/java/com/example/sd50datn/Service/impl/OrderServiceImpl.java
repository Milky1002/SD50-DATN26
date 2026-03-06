package com.example.sd50datn.Service.impl;

import com.example.sd50datn.Dto.OrderSummaryDTO;
import com.example.sd50datn.Repository.OrderRepository;
import com.example.sd50datn.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<OrderSummaryDTO> getOrderSummaries() {
        // #region agent log
        try (var out = java.nio.file.Files.newOutputStream(
                java.nio.file.Path.of("debug-c061a8.log"),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND)) {
            String json = "{\"sessionId\":\"c061a8\",\"runId\":\"pre-fix\",\"hypothesisId\":\"H2\",\"location\":\"OrderServiceImpl.java:18\",\"message\":\"before fetchOrderSummaries\",\"timestamp\":" +
                    System.currentTimeMillis() + "}";
            out.write((json + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
        // #endregion agent log

        try {
            List<OrderSummaryDTO> result = orderRepository.fetchOrderSummaries();

            // #region agent log
            try (var out = java.nio.file.Files.newOutputStream(
                    java.nio.file.Path.of("debug-c061a8.log"),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND)) {
                String json = "{\"sessionId\":\"c061a8\",\"runId\":\"pre-fix\",\"hypothesisId\":\"H3\",\"location\":\"OrderServiceImpl.java:27\",\"message\":\"after fetchOrderSummaries\",\"data\":{\"size\":" +
                        (result != null ? result.size() : -1) + "},\"timestamp\":" +
                        System.currentTimeMillis() + "}";
                out.write((json + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception ignored) {
            }
            // #endregion agent log

            return result;
        } catch (Exception ex) {
            // #region agent log
            try (var out = java.nio.file.Files.newOutputStream(
                    java.nio.file.Path.of("debug-c061a8.log"),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND)) {
                String json = "{\"sessionId\":\"c061a8\",\"runId\":\"pre-fix\",\"hypothesisId\":\"H4\",\"location\":\"OrderServiceImpl.java:38\",\"message\":\"fetchOrderSummaries exception\",\"data\":{\"exception\":\"" +
                        ex.getClass().getName() + "\",\"message\":\"" +
                        String.valueOf(ex.getMessage()).replace("\"", "'") +
                        "\"},\"timestamp\":" + System.currentTimeMillis() + "}";
                out.write((json + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception ignored) {
            }
            // #endregion agent log

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

