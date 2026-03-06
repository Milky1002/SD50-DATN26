package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public String orderManagement(Model model) {
        // #region agent log
        try (var out = java.nio.file.Files.newOutputStream(
                java.nio.file.Path.of("debug-c061a8.log"),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND)) {
            String json = "{\"sessionId\":\"c061a8\",\"runId\":\"pre-fix\",\"hypothesisId\":\"H1\",\"location\":\"OrderController.java:16\",\"message\":\"enter /orders controller\",\"timestamp\":" +
                    System.currentTimeMillis() + "}";
            out.write((json + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
        // #endregion agent log

        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        model.addAttribute("pageHeading", "Quản lý đơn hàng");
        model.addAttribute("orders", orderService.getOrderSummaries());
        return "order-management";
    }

    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable("id") Integer id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") Integer id,
                                    @RequestParam("status") Integer status) {
        orderService.updateOrderStatus(id, status);
        return "redirect:/orders";
    }
}

