package com.library.management.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Use CookieCsrfTokenRepository so JS can read the token
        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                .csrfTokenRequestHandler(csrfHandler))
            .authorizeHttpRequests(auth -> auth
                // Static assets
                .requestMatchers(HttpMethod.GET,
                        "/", "/index.html", "/login.html", "/register.html",
                        "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Public API
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                // Admin APIs
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Authenticated
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login-form")
                .disable())
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((req, res, auth) ->
                        res.setStatus(HttpServletResponse.SC_OK))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .accessDeniedHandler((req, res, e) ->
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")));

        return http.build();
    }
}
