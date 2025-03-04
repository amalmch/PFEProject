package com.example.demo.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/roles/get")
                .allowedOrigins("http://localhost:4200")  // Allow requests from your frontend
                .allowedMethods("GET")
                .allowCredentials(true);
    }
}

