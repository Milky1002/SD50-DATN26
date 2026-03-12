package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/invoices")
    public String invoiceManagement(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("pageTitle", "Quản lý hóa đơn");
        model.addAttribute("pageHeading", "Quản lý hóa đơn");
        model.addAttribute("activeMenu", "hoadon");

        model.addAttribute("stats", invoiceService.getStats());
        model.addAttribute("invoices", invoiceService.getInvoiceSummaries(q));
        model.addAttribute("searchQuery", q != null ? q.trim() : "");

        model.addAttribute("content", "invoice-management");
        model.addAttribute("pageCss", "/css/invoice-management.css");

        return "layout";
    }
}
