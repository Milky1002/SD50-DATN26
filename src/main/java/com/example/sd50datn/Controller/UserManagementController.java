package com.example.sd50datn.Controller;

import com.example.sd50datn.Model.Account;
import com.example.sd50datn.Repository.AccountRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/quan-ly-tai-khoan")
@RequiredArgsConstructor
public class UserManagementController {

    private final AccountRepository accountRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final List<String> ALLOWED_ROLES = Arrays.asList("ADMIN", "STAFF", "USER");

    // ── List ──────────────────────────────────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("accounts", accountRepo.findAll());
        model.addAttribute("allowedRoles", ALLOWED_ROLES);
        model.addAttribute("pageTitle", "Quản lý tài khoản");
        model.addAttribute("pageHeading", "Quản lý tài khoản hệ thống");
        model.addAttribute("activeMenu", "quanlyuser");
        model.addAttribute("content", "UserManagement/list");
        return "layout";
    }

    // ── Create form ───────────────────────────────────────────────────────────
    @GetMapping("/them-moi")
    public String createForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("allowedRoles", ALLOWED_ROLES);
        model.addAttribute("pageTitle", "Thêm tài khoản");
        model.addAttribute("pageHeading", "Thêm tài khoản mới");
        model.addAttribute("activeMenu", "quanlyuser");
        model.addAttribute("content", "UserManagement/form");
        return "layout";
    }

    // ── Save new account ──────────────────────────────────────────────────────
    @PostMapping("/them-moi")
    public String save(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) String hoTen,
                       @RequestParam(required = false) String soDienThoai,
                       @RequestParam(defaultValue = "STAFF") String roleCode,
                       RedirectAttributes ra) {

        if (accountRepo.existsByUsername(username)) {
            ra.addFlashAttribute("error", "Tên đăng nhập đã tồn tại");
            return "redirect:/quan-ly-tai-khoan/them-moi";
        }
        if (email != null && !email.isBlank() && accountRepo.existsByEmail(email)) {
            ra.addFlashAttribute("error", "Email đã được sử dụng");
            return "redirect:/quan-ly-tai-khoan/them-moi";
        }
        if (!ALLOWED_ROLES.contains(roleCode)) {
            roleCode = "STAFF";
        }

        Account acc = Account.builder()
                .username(username.trim())
                .password(encoder.encode(password))
                .email(email != null && !email.isBlank() ? email.trim() : null)
                .hoTen(hoTen != null && !hoTen.isBlank() ? hoTen.trim() : null)
                .soDienThoai(soDienThoai != null && !soDienThoai.isBlank() ? soDienThoai.trim() : null)
                .roleCode(roleCode)
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build();
        accountRepo.save(acc);

        ra.addFlashAttribute("success", "Đã tạo tài khoản: " + username);
        return "redirect:/quan-ly-tai-khoan";
    }

    // ── Edit form ─────────────────────────────────────────────────────────────
    @GetMapping("/chinh-sua/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Optional<Account> opt = accountRepo.findById(id);
        if (opt.isEmpty()) return "redirect:/quan-ly-tai-khoan";

        model.addAttribute("account", opt.get());
        model.addAttribute("allowedRoles", ALLOWED_ROLES);
        model.addAttribute("pageTitle", "Chỉnh sửa tài khoản");
        model.addAttribute("pageHeading", "Chỉnh sửa tài khoản");
        model.addAttribute("activeMenu", "quanlyuser");
        model.addAttribute("content", "UserManagement/form");
        return "layout";
    }

    // ── Update account ────────────────────────────────────────────────────────
    @PostMapping("/chinh-sua/{id}")
    public String update(@PathVariable Integer id,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String hoTen,
                         @RequestParam(required = false) String soDienThoai,
                         @RequestParam(defaultValue = "STAFF") String roleCode,
                         @RequestParam Integer trangThai,
                         @RequestParam(required = false) String newPassword,
                         HttpSession session,
                         RedirectAttributes ra) {

        Optional<Account> opt = accountRepo.findById(id);
        if (opt.isEmpty()) return "redirect:/quan-ly-tai-khoan";

        Account acc = opt.get();

        // Guard: cannot downgrade current logged-in admin
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (id.equals(currentUserId) && !"ADMIN".equals(roleCode)) {
            ra.addFlashAttribute("error", "Không thể thay đổi vai trò của tài khoản đang đăng nhập");
            return "redirect:/quan-ly-tai-khoan/chinh-sua/" + id;
        }

        if (!ALLOWED_ROLES.contains(roleCode)) roleCode = acc.getRoleCode();

        // Email uniqueness check (excluding self)
        if (email != null && !email.isBlank() && !email.equalsIgnoreCase(acc.getEmail())) {
            if (accountRepo.existsByEmail(email)) {
                ra.addFlashAttribute("error", "Email đã được sử dụng bởi tài khoản khác");
                return "redirect:/quan-ly-tai-khoan/chinh-sua/" + id;
            }
        }

        acc.setEmail(email != null && !email.isBlank() ? email.trim() : null);
        acc.setHoTen(hoTen != null && !hoTen.isBlank() ? hoTen.trim() : null);
        acc.setSoDienThoai(soDienThoai != null && !soDienThoai.isBlank() ? soDienThoai.trim() : null);
        acc.setRoleCode(roleCode);
        acc.setTrangThai(trangThai);
        acc.setNgayCapNhat(LocalDateTime.now());

        if (newPassword != null && newPassword.length() >= 6) {
            acc.setPassword(encoder.encode(newPassword));
        }

        accountRepo.save(acc);
        ra.addFlashAttribute("success", "Đã cập nhật tài khoản: " + acc.getUsername());
        return "redirect:/quan-ly-tai-khoan";
    }

    // ── Toggle status ─────────────────────────────────────────────────────────
    @PostMapping("/doi-trang-thai/{id}")
    public String toggleStatus(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (id.equals(currentUserId)) {
            ra.addFlashAttribute("error", "Không thể khóa tài khoản đang đăng nhập");
            return "redirect:/quan-ly-tai-khoan";
        }

        accountRepo.findById(id).ifPresent(acc -> {
            acc.setTrangThai(acc.getTrangThai() == 1 ? 0 : 1);
            acc.setNgayCapNhat(LocalDateTime.now());
            accountRepo.save(acc);
        });

        ra.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản");
        return "redirect:/quan-ly-tai-khoan";
    }
}
