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

        String role = (String) session.getAttribute("role");
        // Accept both Unicode "Quản lý" and ASCII "Quan ly" for compatibility
        if (role == null || (!role.equalsIgnoreCase("Quản lý") && !role.equalsIgnoreCase("Quan ly"))) {
            response.sendRedirect("/dashboard?error=access_denied");
            return false;
        }

        return true;
    }
}
