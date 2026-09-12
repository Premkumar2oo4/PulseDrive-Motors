package com.pulsedrive.config;

import com.pulsedrive.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.DispatcherType;
import java.util.List;
import java.util.Arrays;

@Configuration
public class SecurityConfig {

        @Value("${app.cors.allowed-origins:http://localhost:5173}")
        private String allowedOrigins;

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                .dispatcherTypeMatchers(
                                                                DispatcherType.ERROR)
                                                .permitAll()
                                                // Authentication endpoints
                                                .requestMatchers(
                                                                "/api/auth/**")
                                                .permitAll()
                                                .requestMatchers("/error")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/health")
                                                .permitAll()

                                                // Public vehicle GET APIs
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/vehicles/**")
                                                .permitAll()

                                                // Public category GET APIs
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/categories/**")
                                                .permitAll()

                                                // Public image + feature GET APIs
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/vehicle-images/**",
                                                                "/api/vehicle-features/**")
                                                .permitAll()

                                                // ADMIN vehicle management
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/vehicles/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/vehicles/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/vehicles/**")
                                                .hasRole("ADMIN")

                                                // ADMIN category management
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/categories/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/categories/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/categories/**")
                                                .hasRole("ADMIN")

                                                // ADMIN image management
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/vehicle-images/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/vehicle-images/**")
                                                .hasRole("ADMIN")

                                                // ADMIN feature management
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/vehicle-features/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/vehicle-features/**")
                                                .hasRole("ADMIN")
                                                // .requestMatchers(
                                                // "/api/payments/**",
                                                // "/api/orders/**",
                                                // "/api/cart/**",
                                                // "/api/wishlist/**",
                                                // "/api/users/**")
                                                // .authenticated()
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/dealerships/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/dealerships/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/dealerships/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/dealerships/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/admin/test-drives/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/test-drives/**")
                                                .hasAnyRole("CUSTOMER", "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/reviews/**")
                                                .hasAnyRole("CUSTOMER", "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/reviews/**")
                                                .hasAnyRole("CUSTOMER", "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/reviews/**")
                                                .hasAnyRole("CUSTOMER", "ADMIN")
                                                .requestMatchers(
                                                                "/api/admin/coupons/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/admin/orders/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/orders/**")
                                                .hasAnyRole("CUSTOMER", "ADMIN")
                                                .requestMatchers(
                                                                "/api/admin/users/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/admin/dashboard/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/admin/inventory/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/admin/payments/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/notifications/**")
                                                .hasAnyRole("CUSTOMER", "ADMIN")
                                                .requestMatchers(
                                                                "/api/payments/**",
                                                                "/api/cart/**",
                                                                "/api/wishlist/**",
                                                                "/api/users/**")
                                                .authenticated()
                                                // Everything else requires login
                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                Arrays.stream(allowedOrigins.split(","))
                                                .map(String::trim)
                                                .filter(origin -> !origin.isEmpty())
                                                .toList());

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "PATCH",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of("*"));

                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }
}
