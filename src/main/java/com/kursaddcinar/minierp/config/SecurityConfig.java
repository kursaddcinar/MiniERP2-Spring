package com.kursaddcinar.minierp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF korumasını API'ler için kapatıyoruz (Stateless REST API olduğu için)
            .csrf(AbstractHttpConfigurer::disable)
            // CORS ayarlarını aktif ediyoruz (Frontend entegrasyonu için şimdiden hazır olsun)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Şimdilik tüm isteklere izin ver (Permit All)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // API endpointlerini aç
                .anyRequest().permitAll() // Diğer her şeyi de aç (Swagger vs. için)
            );

        return http.build();
    }

    // Frontend farklı porttan geleceği için CORS izni
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Şimdilik her yerden gelen isteğe izin ver
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}