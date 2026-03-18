package com.example.sd50datn.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        boolean loggedIn = false;
        if (session != null) {
            Boolean isLoggedIn = (Boolean) session.getAttribute("loggedIn");
            loggedIn = isLoggedIn != null && isLoggedIn;
        }

        if (!loggedIn) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
