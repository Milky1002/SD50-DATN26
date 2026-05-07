package com.example.sd50datn.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class StaffPosOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        String roleCode = (String) session.getAttribute("roleCode");
        String legacyRole = (String) session.getAttribute("role");
        boolean isEmployee = "STAFF".equalsIgnoreCase(roleCode)
                || (legacyRole != null && (legacyRole.equalsIgnoreCase("Nhân viên") || legacyRole.equalsIgnoreCase("Nhan vien")));

        if (!isEmployee) {
            return true;
        }

        // Staff role is restricted to POS only — redirect any other admin page to POS
        // /cham-cong and /api/ca-lam-viec removed (attendance feature disabled)
        String uri = request.getRequestURI();
        boolean allowed = uri.startsWith("/ban-hang")
                || uri.equals("/logout")
                || uri.equals("/login");
        if (!allowed) {
            response.sendRedirect("/ban-hang?error=staff_pos_only");
            return false;
        }

        return true;
    }
}
