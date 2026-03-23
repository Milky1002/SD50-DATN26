package com.example.sd50datn.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for customer-facing pages that require authentication.
 * Checks for "shopLoggedIn" session attribute (separate from admin auth).
 */
@Component
public class ShopAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        boolean loggedIn = false;
        if (session != null) {
            Boolean isLoggedIn = (Boolean) session.getAttribute("shopLoggedIn");
            loggedIn = isLoggedIn != null && isLoggedIn;
        }

        if (!loggedIn) {
            String redirectUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                redirectUrl += "?" + queryString;
            }
            response.sendRedirect("/dang-nhap?redirect=" + java.net.URLEncoder.encode(redirectUrl, "UTF-8"));
            return false;
        }

        return true;
    }
}
