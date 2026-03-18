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
import java.util.List;
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
     * Authenticate user by username and password.
     * Returns a Map with user info if successful, null otherwise.
     */
    public Map<String, Object> authenticate(String username, String password) {
        Optional<Account> optAccount = accountRepo.findByUsername(username);
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

        // Find linked NhanVien
        List<Staff> staffList = staffRepo.findAll();
        Staff staff = staffList.stream()
                .filter(s -> account.getId().equals(s.getTaiKhoanId()))
                .findFirst()
                .orElse(null);

        if (staff == null) {
            return null; // No linked staff
        }

        // Check staff is active
        if (staff.getTrangThai() != null && staff.getTrangThai() != 1) {
            return null;
        }

        // Get position/role
        String tenChucVu = positionRepo.findById(staff.getChucVuId())
                .map(Position::getTenChucVu)
                .orElse("Nhân viên");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("accountId", account.getId());
        userInfo.put("nhanVienId", staff.getId());
        userInfo.put("username", account.getUsername());
        userInfo.put("hoTen", staff.getHoTen());
        userInfo.put("email", staff.getEmail());
        userInfo.put("chucVuId", staff.getChucVuId());
        userInfo.put("tenChucVu", tenChucVu);
        userInfo.put("role", tenChucVu);

        return userInfo;
    }

    /**
     * Store user info in session
     */
    public void setSession(HttpSession session, Map<String, Object> userInfo) {
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
     * Change password for an account
     */
    public boolean changePassword(Integer accountId, String oldPassword, String newPassword) {
        Optional<Account> optAccount = accountRepo.findById(accountId);
        if (optAccount.isEmpty()) return false;

        Account account = optAccount.get();

        // BCrypt check with plaintext fallback
        boolean oldMatch = false;
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
     * Check if user is logged in
     */
    public boolean isLoggedIn(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        return loggedIn != null && loggedIn;
    }
}
