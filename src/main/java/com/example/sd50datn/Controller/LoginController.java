package com.example.sd50datn.Controller;

import com.example.sd50datn.Service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        // If already logged in, redirect to dashboard
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        Map<String, Object> userInfo = authService.authenticate(username, password);

        if (userInfo == null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            model.addAttribute("username", username);
            return "login";
        }

        authService.setSession(session, userInfo);
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/api/change-password")
    @ResponseBody
    public Map<String, Object> changePassword(@RequestBody Map<String, String> request,
                                               HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return Map.of("success", false, "message", "Chưa đăng nhập");
        }

        Integer accountId = (Integer) session.getAttribute("accountId");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            return Map.of("success", false, "message", "Vui lòng nhập đầy đủ thông tin");
        }

        if (newPassword.length() < 6) {
            return Map.of("success", false, "message", "Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        boolean success = authService.changePassword(accountId, oldPassword, newPassword);
        if (success) {
            return Map.of("success", true, "message", "Đổi mật khẩu thành công");
        } else {
            return Map.of("success", false, "message", "Mật khẩu cũ không đúng");
        }
    }
}
