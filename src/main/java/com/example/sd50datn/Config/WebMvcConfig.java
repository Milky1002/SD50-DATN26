package com.example.sd50datn.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final StaffPosOnlyInterceptor staffPosOnlyInterceptor;
    private final ShopAuthInterceptor shopAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Order 1: Admin authentication check — must be logged in (staff)
        registry.addInterceptor(authInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/login/**",
                        "/",
                        "/san-pham/**",
                        "/cua-hang",
                        "/cua-hang/**",
                        "/gio-hang/**",
                        "/thanh-toan/**",
                        "/tai-khoan/**",
                        "/dang-nhap",
                        "/dang-ky",
                        "/khuyen-mai/**",
                        "/tim-kiem/**",
                        "/dat-hang-nhanh/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/Images/**",
                        "/shop/**",
                        "/fonts/**",
                        "/favicon.ico",
                        "/error"
                );

        // Order 2: Admin role check — must be "ADMIN" roleCode or "Quản lý"
        registry.addInterceptor(adminInterceptor)
                .order(2)
                .addPathPatterns("/nhan-vien/**", "/quan-ly-tai-khoan/**", "/lich-su-nhan-vien/**");

        registry.addInterceptor(staffPosOnlyInterceptor)
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/login/**",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/Images/**",
                        "/shop/**",
                        "/fonts/**",
                        "/favicon.ico",
                        "/error"
                );

        // Order 3: Shop customer auth — required for account & checkout pages
        registry.addInterceptor(shopAuthInterceptor)
                .order(3)
                .addPathPatterns(
                        "/tai-khoan/**",
                        "/thanh-toan/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
