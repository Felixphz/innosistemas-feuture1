package com.udea.sistemas.innosistemas.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir solicitudes desde el frontend (puerto 3004)
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3004", "http://127.0.0.1:3004", "https://frontend-innosistemas-sprint2-2hos-822xwsx0r.vercel.app",
            "https://frontend-innosistemas-sprint2.vercel.app", "https://frontend-innosistemas-sprint3.vercel.app"));
        
        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Permitir todos los headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permitir cookies y credentials
        configuration.setAllowCredentials(true);
        
        // Configurar para todos los endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
