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

