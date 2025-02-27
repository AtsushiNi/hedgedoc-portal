package com.atsushini.hedgedocportal.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.atsushini.hedgedocportal.authentication.AuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors
            .configurationSource(request -> {
                var corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("http://localhost:8888", "http://localhost:3001"));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                corsConfiguration.setAllowCredentials(true);
                corsConfiguration.setExposedHeaders(List.of("*"));
                return corsConfiguration;
            }));

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(requests -> requests
            .requestMatchers("/api/v1/login").permitAll()
            .requestMatchers("/api/v1/admin/**").permitAll()
            .requestMatchers("/api/**").permitAll());

        http.addFilterBefore(new AuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers().frameOptions().disable();

        return http.build();
    }
}
