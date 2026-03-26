package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.OrderService;
import com.example.sd50datn.Util.OrderStatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public String orderManagement(Model model) {
        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        model.addAttribute("pageHeading", "Quản lý đơn hàng");
        model.addAttribute("orders", orderService.getOrderSummaries());
        model.addAttribute("statusLabels", OrderStatusUtil.getAllStatuses());

        model.addAttribute("activeMenu", "donhang");
        model.addAttribute("content", "order-management");
        model.addAttribute("pageCss", "/css/order-management.css");
        return "layout";
    }

    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable("id") Integer id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") Integer id,
                                    @RequestParam("status") Integer status,
                                    RedirectAttributes ra) {
        try {
            orderService.updateOrderStatus(id, status);
            ra.addFlashAttribute("success", "Cập nhật trạng thái thành công");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }
}

