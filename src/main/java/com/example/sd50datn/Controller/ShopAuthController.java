package com.example.sd50datn.Controller;

import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Service.ShopAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ShopAuthController {

    private final ShopAuthService shopAuthService;

    @GetMapping("/dang-nhap")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect,
                            HttpSession session, Model model) {
        if (shopAuthService.isLoggedIn(session)) {
            return "redirect:" + (redirect != null ? redirect : "/tai-khoan");
        }
        model.addAttribute("redirect", redirect);
        model.addAttribute("pageTitle", "Đăng nhập — Yonex Store");
        model.addAttribute("pageCss", "/shop/css/shop-auth.css");
        model.addAttribute("content", "shop/auth/login");
        model.addAttribute("cartItemCount", 0);
        return "shop/shop-layout";
    }

    @PostMapping("/dang-nhap")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam(value = "redirect", required = false) String redirect,
                              HttpSession session, RedirectAttributes ra) {
        KhachHang kh = shopAuthService.authenticate(email, password);
        if (kh == null) {
            ra.addFlashAttribute("error", "Email hoặc mật khẩu không đúng");
            ra.addFlashAttribute("emailValue", email);
            return "redirect:/dang-nhap" + (redirect != null ? "?redirect=" + redirect : "");
        }
        shopAuthService.setShopSession(session, kh);
        return "redirect:" + (redirect != null && !redirect.isBlank() ? redirect : "/");
    }

    @GetMapping("/dang-ky")
    public String registerPage(HttpSession session, Model model) {
        if (shopAuthService.isLoggedIn(session)) {
            return "redirect:/tai-khoan";
        }
        model.addAttribute("pageTitle", "Đăng ký — Yonex Store");
        model.addAttribute("pageCss", "/shop/css/shop-auth.css");
        model.addAttribute("content", "shop/auth/register");
        model.addAttribute("cartItemCount", 0);
        return "shop/shop-layout";
    }

    @PostMapping("/dang-ky")
    public String registerSubmit(@RequestParam String tenKhachHang,
                                 @RequestParam String email,
                                 @RequestParam String sdt,
                                 @RequestParam String diaChi,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 HttpSession session, RedirectAttributes ra) {
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
            ra.addFlashAttribute("form", java.util.Map.of("tenKhachHang", tenKhachHang, "email", email, "sdt", sdt, "diaChi", diaChi));
            return "redirect:/dang-ky";
        }
        if (password.length() < 6) {
            ra.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
            ra.addFlashAttribute("form", java.util.Map.of("tenKhachHang", tenKhachHang, "email", email, "sdt", sdt, "diaChi", diaChi));
            return "redirect:/dang-ky";
        }

        ShopAuthService.RegistrationResult result = shopAuthService.registerDetailed(tenKhachHang, email, sdt, diaChi, password);
        if (!result.isSuccess()) {
            ra.addFlashAttribute("error", result.errorMessage());
            ra.addFlashAttribute("form", java.util.Map.of("tenKhachHang", tenKhachHang, "email", email, "sdt", sdt, "diaChi", diaChi));
            return "redirect:/dang-ky";
        }

        KhachHang kh = shopAuthService.authenticate(email, password);
        if (kh != null) {
            shopAuthService.setShopSession(session, kh);
        }
        ra.addFlashAttribute("success", result.displayMessage());
        if (result.outcomeType() == ShopAuthService.RegistrationOutcomeType.CLAIMED_EXISTING_CUSTOMER) {
            ra.addFlashAttribute("info", "Hệ thống đã nhận diện hồ sơ mua tại quầy trước đó và tự động liên kết với tài khoản mới của bạn.");
        }
        return "redirect:/";
    }

    @GetMapping("/dang-xuat")
    public String logout(HttpSession session) {
        shopAuthService.clearShopSession(session);
        return "redirect:/";
    }
}
