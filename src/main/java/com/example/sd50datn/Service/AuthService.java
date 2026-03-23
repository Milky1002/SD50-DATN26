package com.example.sd50datn.Service;

import com.example.sd50datn.Model.Account;
import com.example.sd50datn.Model.Staff;
import com.example.sd50datn.Model.Position;
import com.example.sd50datn.Repository.AccountRepository;
import com.example.sd50datn.Repository.StaffRepository;
import com.example.sd50datn.Repository.PositionRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepo;
    private final StaffRepository staffRepo;
    private final PositionRepository positionRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Authenticate admin/staff by username or email.
     * Returns a Map with user info if successful, null otherwise.
     */
    public Map<String, Object> authenticate(String usernameOrEmail, String password) {
        // Try username first, then email
        Optional<Account> optAccount = accountRepo.findByUsername(usernameOrEmail);
        if (optAccount.isEmpty()) {
            optAccount = accountRepo.findByEmail(usernameOrEmail);
        }
        if (optAccount.isEmpty()) {
            return null;
        }

        Account account = optAccount.get();

        // BCrypt-first with plaintext fallback for migration
        boolean passwordMatch = false;
        if (account.getPassword().startsWith("$2")) {
            passwordMatch = encoder.matches(password, account.getPassword());
        } else {
            // Plaintext fallback — auto-migrate to BCrypt on success
            if (account.getPassword().equals(password)) {
                account.setPassword(encoder.encode(password));
                accountRepo.save(account);
                passwordMatch = true;
            }
        }
        if (!passwordMatch) {
            return null;
        }

        // Check account status
        if (account.getTrangThai() != null && account.getTrangThai() != 1) {
            return null;
        }

        // Find linked NhanVien via direct lookup (no stream scan)
        Optional<Staff> staffOpt = staffRepo.findByTaiKhoanId(account.getId());
        Staff staff = staffOpt.orElse(null);

        if (staff == null) {
            return null; // No linked staff
        }

        // Check staff is active
        if (staff.getTrangThai() != null && staff.getTrangThai() != 1) {
            return null;
        }

        // Get position/role display label
        String tenChucVu = positionRepo.findById(staff.getChucVuId())
                .map(Position::getTenChucVu)
                .orElse("Nhân viên");

        // Canonical role code: derive from account.roleCode or fallback from position
        String roleCode = account.getRoleCode();
        if (roleCode == null || roleCode.isBlank()) {
            // Fallback: derive from position for legacy accounts not yet migrated
            roleCode = tenChucVu.contains("Quản lý") || tenChucVu.contains("Quan ly")
                    ? "ADMIN" : "STAFF";
        }

        Map<String, Object> userInfo = new HashMap<>();
        // Canonical keys (new)
        userInfo.put("userId", account.getId());
        userInfo.put("roleCode", roleCode);
        userInfo.put("displayName", staff.getHoTen());
        userInfo.put("authenticated", true);
        // Legacy keys (preserved for backward compat)
        userInfo.put("accountId", account.getId());
        userInfo.put("nhanVienId", staff.getId());
        userInfo.put("username", account.getUsername());
        userInfo.put("hoTen", staff.getHoTen());
        userInfo.put("email", staff.getEmail());
        userInfo.put("chucVuId", staff.getChucVuId());
        userInfo.put("tenChucVu", tenChucVu);
        userInfo.put("role", tenChucVu); // legacy string role

        return userInfo;
    }

    /**
     * Store user info in session — writes both new canonical and legacy keys.
     */
    public void setSession(HttpSession session, Map<String, Object> userInfo) {
        // Canonical keys
        session.setAttribute("userId", userInfo.get("userId"));
        session.setAttribute("roleCode", userInfo.get("roleCode"));
        session.setAttribute("displayName", userInfo.get("displayName"));
        session.setAttribute("authenticated", true);
        // Legacy keys
        session.setAttribute("loggedIn", true);
        session.setAttribute("accountId", userInfo.get("accountId"));
        session.setAttribute("nhanVienId", userInfo.get("nhanVienId"));
        session.setAttribute("username", userInfo.get("username"));
        session.setAttribute("hoTen", userInfo.get("hoTen"));
        session.setAttribute("email", userInfo.get("email"));
        session.setAttribute("chucVuId", userInfo.get("chucVuId"));
        session.setAttribute("tenChucVu", userInfo.get("tenChucVu"));
        session.setAttribute("role", userInfo.get("role"));
        session.setMaxInactiveInterval(3600); // 1 hour timeout
    }

    /**
     * Clear all session attributes (both canonical and legacy).
     */
    public void clearSession(HttpSession session) {
        // Canonical
        session.removeAttribute("userId");
        session.removeAttribute("roleCode");
        session.removeAttribute("displayName");
        session.removeAttribute("authenticated");
        // Legacy admin
        session.removeAttribute("loggedIn");
        session.removeAttribute("accountId");
        session.removeAttribute("nhanVienId");
        session.removeAttribute("username");
        session.removeAttribute("hoTen");
        session.removeAttribute("email");
        session.removeAttribute("chucVuId");
        session.removeAttribute("tenChucVu");
        session.removeAttribute("role");
        // Legacy shop (in case of cross-login)
        session.removeAttribute("shopLoggedIn");
        session.removeAttribute("shopCustomerId");
        session.removeAttribute("shopCustomerName");
        session.removeAttribute("shopCustomerEmail");
    }

    /**
     * Change password for an account
     */
    public boolean changePassword(Integer accountId, String oldPassword, String newPassword) {
        Optional<Account> optAccount = accountRepo.findById(accountId);
        if (optAccount.isEmpty()) return false;

        Account account = optAccount.get();

        // BCrypt check with plaintext fallback
        boolean oldMatch;
        if (account.getPassword().startsWith("$2")) {
            oldMatch = encoder.matches(oldPassword, account.getPassword());
        } else {
            oldMatch = account.getPassword().equals(oldPassword);
        }
        if (!oldMatch) {
            return false;
        }

        account.setPassword(encoder.encode(newPassword));
        accountRepo.save(account);
        return true;
    }

    /**
     * Check if admin/staff user is logged in
     */
    public boolean isLoggedIn(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        return loggedIn != null && loggedIn;
    }
}
