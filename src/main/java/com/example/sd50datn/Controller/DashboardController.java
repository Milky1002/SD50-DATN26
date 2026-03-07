package com.example.sd50datn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Bàn làm việc");
        model.addAttribute("pageHeading", "Bàn làm việc");
        //fragment cần replace trong layout
        model.addAttribute("content", "dashboard :: dashboardContent");
        return "layout";
    }
    @GetMapping("/staff")
    public String staff() {
        return "QlyNhanVien/Staff";
    }
}