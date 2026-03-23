package com.example.sd50datn.Config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicit multipart config to ensure upload limits are applied correctly in Spring Boot 4.
 * This bean overrides Spring Boot's auto-configured MultipartConfigElement (@ConditionalOnMissingBean),
 * which fixes MaxUploadSizeExceededException firing even when no file is uploaded via multipart/form-data forms.
 * Uses direct MultipartConfigElement constructor (Jakarta Servlet API) instead of the removed
 * Spring Boot MultipartConfigFactory.
 */
@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        long maxFileSize    = 50L * 1024 * 1024; // 50 MB
        long maxRequestSize = 60L * 1024 * 1024; // 60 MB
        return new MultipartConfigElement("", maxFileSize, maxRequestSize, 0);
    }
}
