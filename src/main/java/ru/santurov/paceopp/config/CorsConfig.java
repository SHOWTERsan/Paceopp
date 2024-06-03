package ru.santurov.paceopp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost") // Allow requests from the Docker Compose network
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH") // Allow all HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(false) // Do not allow credentials (cookies, authorization headers)
                .maxAge(3600); // Cache preflight requests for 1 hour
    }
}
