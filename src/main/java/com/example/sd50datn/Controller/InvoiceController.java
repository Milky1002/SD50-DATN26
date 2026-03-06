package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/invoices")
    public String invoiceManagement(Model model) {
        model.addAttribute("pageTitle", "Quản lý hóa đơn");
        model.addAttribute("pageHeading", "Quản lý hóa đơn");
        model.addAttribute("stats", invoiceService.getStats());
        model.addAttribute("invoices", invoiceService.getInvoiceSummaries());
        return "invoice-management";
    }
}

