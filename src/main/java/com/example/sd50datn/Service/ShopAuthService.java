package com.example.sd50datn.Service;

import com.example.sd50datn.Entity.KhachHang;
import com.example.sd50datn.Model.Account;
import com.example.sd50datn.Repository.AccountRepository;
import com.example.sd50datn.Repository.KhachHangRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopAuthService {

    public enum RegistrationOutcomeType {
        CREATED_NEW_ACCOUNT,
        CLAIMED_EXISTING_CUSTOMER
    }

    public record RegistrationResult(String errorMessage, RegistrationOutcomeType outcomeType, String displayMessage) {
        public boolean isSuccess() {
            return errorMessage == null;
        }
    }

    private final KhachHangRepository khachHangRepo;
    private final AccountRepository accountRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Authenticate a customer by email.
     * Phase 1: tries TaiKhoan.Email first; falls back to KhachHang.Mat_khau for legacy rows.
     */
    public KhachHang authenticate(String email, String password) {
        // ── Path A: unified account exists with this email ──────────────────────
        Optional<Account> accountOpt = accountRepo.findByEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getTrangThai() != null && account.getTrangThai() != 1) return null;

            boolean match;
            if (account.getPassword().startsWith("$2")) {
                match = encoder.matches(password, account.getPassword());
            } else {
                match = account.getPassword().equals(password);
                if (match) {
                    account.setPassword(encoder.encode(password));
                    accountRepo.save(account);
                }
            }
            if (!match) return null;

            // Load KhachHang profile via taiKhoanId link
            Optional<KhachHang> khOpt = khachHangRepo.findByTaiKhoanId(account.getId());
            if (khOpt.isPresent()) {
                KhachHang kh = khOpt.get();
                if (kh.getTrangThai() != null && kh.getTrangThai() != 1) return null;
                return kh;
            }
            // Account exists but no KhachHang profile — shouldn't happen after migration
            return null;
        }

        // ── Path B: legacy KhachHang.Mat_khau (not yet migrated to TaiKhoan) ───
        Optional<KhachHang> opt = khachHangRepo.findByEmail(email);
        if (opt.isEmpty()) return null;

        KhachHang kh = opt.get();
        if (kh.getMatKhau() == null) return null;
        if (kh.getTrangThai() != null && kh.getTrangThai() != 1) return null;

        boolean match;
        if (kh.getMatKhau().startsWith("$2")) {
            match = encoder.matches(password, kh.getMatKhau());
        } else {
            match = kh.getMatKhau().equals(password);
            if (match) {
                kh.setMatKhau(encoder.encode(password));
                khachHangRepo.save(kh);
            }
        }
        return match ? kh : null;
    }

    /**
     * Register a new customer — creates TaiKhoan (USER) + KhachHang in one transaction.
     *
     * If a KhachHang already exists with the same email (POS customer, no linked account),
     * the flow creates a TaiKhoan and links it to the existing KhachHang instead of rejecting.
     *
     * Returns error message, or null on success.
     */
    @Transactional
    public RegistrationResult registerDetailed(String tenKhachHang, String email, String sdt, String diaChi, String password) {
        // 1. If a TaiKhoan with this email already exists → truly duplicate, reject
        if (accountRepo.existsByEmail(email)) {
            return new RegistrationResult("Email đã được sử dụng", null, null);
        }

        // 2. Check if a KhachHang with this email exists (e.g. POS customer)
        Optional<KhachHang> existingByEmail = khachHangRepo.findByEmail(email);

        if (existingByEmail.isPresent()) {
            KhachHang existing = existingByEmail.get();

            // Already linked to an account → reject
            if (existing.getTaiKhoanId() != null) {
                return new RegistrationResult("Email đã được sử dụng", null, null);
            }

            Optional<Account> accountByPhone = accountRepo.findBySoDienThoai(sdt);
            if (accountByPhone.isPresent()) {
                return new RegistrationResult("Số điện thoại đã được liên kết với tài khoản khác", null, null);
            }

            // POS customer without account → claim: create account and link
            String hashedPassword = encoder.encode(password);

            Account account = Account.builder()
                    .username("kh_" + email.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase())
                    .password(hashedPassword)
                    .email(email)
                    .hoTen(tenKhachHang)
                    .soDienThoai(sdt)
                    .roleCode("USER")
                    .trangThai(1)
                    .ngayTao(LocalDateTime.now())
                    .build();
            account = accountRepo.save(account);

            // Link existing KhachHang to the new account
            existing.setTaiKhoanId(account.getId());
            existing.setMatKhau(hashedPassword);
            if (diaChi != null && !diaChi.isBlank()) {
                existing.setDiaChiKhachHang(diaChi);
            }
            existing.setNgayCapNhat(LocalDateTime.now());
            khachHangRepo.save(existing);

            return new RegistrationResult(
                    null,
                    RegistrationOutcomeType.CLAIMED_EXISTING_CUSTOMER,
                    "Đã kích hoạt tài khoản online từ hồ sơ khách hàng có sẵn của bạn."
            );
        }

        // 3. Check phone: if a KhachHang with this phone exists and already has an account → reject
        Optional<KhachHang> existingByPhone = khachHangRepo.findBySdt(sdt);
        if (existingByPhone.isPresent() && existingByPhone.get().getTaiKhoanId() != null) {
            return new RegistrationResult("Số điện thoại đã được sử dụng", null, null);
        }
        if (accountRepo.existsBySoDienThoai(sdt)) {
            return new RegistrationResult("Số điện thoại đã được liên kết với tài khoản khác", null, null);
        }

        // 4. Fresh registration — create both Account and KhachHang
        String hashedPassword = encoder.encode(password);

        Account account = Account.builder()
                .username("kh_" + email.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase())
                .password(hashedPassword)
                .email(email)
                .hoTen(tenKhachHang)
                .soDienThoai(sdt)
                .roleCode("USER")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build();
        account = accountRepo.save(account);

        // If a POS KhachHang exists by phone (no account), link it
        if (existingByPhone.isPresent()) {
            KhachHang existingPhone = existingByPhone.get();
            existingPhone.setTaiKhoanId(account.getId());
            existingPhone.setEmail(email);
            existingPhone.setMatKhau(hashedPassword);
            if (diaChi != null && !diaChi.isBlank()) {
                existingPhone.setDiaChiKhachHang(diaChi);
            }
            existingPhone.setNgayCapNhat(LocalDateTime.now());
            khachHangRepo.save(existingPhone);
        } else {
            // Fully new customer
            KhachHang kh = KhachHang.builder()
                    .tenKhachHang(tenKhachHang)
                    .email(email)
                    .sdt(sdt)
                    .diaChiKhachHang(diaChi)
                    .matKhau(hashedPassword)
                    .taiKhoanId(account.getId())
                    .trangThai(1)
                    .ngayTao(LocalDateTime.now())
                    .build();
            khachHangRepo.save(kh);
        }

        return new RegistrationResult(
                null,
                RegistrationOutcomeType.CREATED_NEW_ACCOUNT,
                "Đăng ký thành công! Tài khoản của bạn đã sẵn sàng để mua sắm."
        );
    }

    @Transactional
    public String register(String tenKhachHang, String email, String sdt, String diaChi, String password) {
        return registerDetailed(tenKhachHang, email, sdt, diaChi, password).errorMessage();
    }

    /**
     * Set shop session — writes both shop-specific and canonical keys.
     */
    public void setShopSession(HttpSession session, KhachHang kh) {
        // Legacy shop keys
        session.setAttribute("shopLoggedIn", true);
        session.setAttribute("shopCustomerId", kh.getKhachHangId());
        session.setAttribute("shopCustomerName", kh.getTenKhachHang());
        session.setAttribute("shopCustomerEmail", kh.getEmail());
        // Canonical keys
        Integer userId = kh.getTaiKhoanId() != null ? kh.getTaiKhoanId() : kh.getKhachHangId();
        session.setAttribute("userId", userId);
        session.setAttribute("roleCode", "USER");
        session.setAttribute("displayName", kh.getTenKhachHang());
        session.setAttribute("authenticated", true);
        session.setMaxInactiveInterval(7200);
    }

    public void clearShopSession(HttpSession session) {
        session.removeAttribute("shopLoggedIn");
        session.removeAttribute("shopCustomerId");
        session.removeAttribute("shopCustomerName");
        session.removeAttribute("shopCustomerEmail");
        session.removeAttribute("userId");
        session.removeAttribute("roleCode");
        session.removeAttribute("displayName");
        session.removeAttribute("authenticated");
    }

    public boolean isLoggedIn(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute("shopLoggedIn");
        return loggedIn != null && loggedIn;
    }
}
