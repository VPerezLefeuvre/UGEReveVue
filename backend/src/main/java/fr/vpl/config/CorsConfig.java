package fr.vpl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS configuration for the frontend origins allowed to call the API.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private final String[] allowedOrigins;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.allowedOrigins = StringUtils.commaDelimitedListToStringArray(allowedOrigins);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");

        registry.addMapping("/actuator/health")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "OPTIONS");
    }
}
