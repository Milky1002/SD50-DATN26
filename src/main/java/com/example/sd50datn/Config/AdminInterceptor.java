package com.example.sd50datn.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Check canonical roleCode first, fall back to legacy "role" string for transition period
        String roleCode = (String) session.getAttribute("roleCode");
        String legacyRole = (String) session.getAttribute("role");

        boolean isAdmin = "ADMIN".equals(roleCode)
                || (legacyRole != null && (legacyRole.equalsIgnoreCase("Quản lý") || legacyRole.equalsIgnoreCase("Quan ly")));

        if (!isAdmin) {
            response.sendRedirect("/dashboard?error=access_denied");
            return false;
        }

        return true;
    }
}
