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

/**
 * Spring Security Configuration.
 * 
 * Configures:
 * 1. Password Encoding: Uses BCrypt with work factor 12 for secure password hashing.
 * 2. CSRF Protection: CookieCsrfTokenRepository allows JavaScript frontend to read XSRF-TOKEN cookie.
 * 3. Role-Based Access Control (RBAC):
 *    - Public: Home, Browse books, Register, Login, Static assets (CSS/JS/HTML).
 *    - Admin: Inventory management (/api/books write, /api/admin/**, etc.).
 *    - Authenticated Member: Issue, Return, Reserve, Pay fines.
 * 4. Session-Based Authentication: Uses standard HttpSession (JSESSIONID).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Bean for hashing user passwords securely.
     * BCrypt adds a cryptographic salt automatically to resist rainbow table attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Exposes Spring's AuthenticationManager to handle programmatic login in AuthController.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the HTTP security filter chain for web requests.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. Configure CSRF with cookie repository accessible to JavaScript
        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                .csrfTokenRequestHandler(csrfHandler))
            // Ensure the CSRF token is loaded into the response cookie on every request
            .addFilterAfter(new org.springframework.web.filter.OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                                jakarta.servlet.http.HttpServletResponse response,
                                                jakarta.servlet.FilterChain filterChain)
                        throws jakarta.servlet.ServletException, java.io.IOException {
                    org.springframework.security.web.csrf.CsrfToken csrfToken =
                            (org.springframework.security.web.csrf.CsrfToken) request.getAttribute(
                                    org.springframework.security.web.csrf.CsrfToken.class.getName());
                    if (csrfToken != null) {
                        csrfToken.getToken(); // Forces token generation so cookie is written
                    }
                    filterChain.doFilter(request, response);
                }
            }, org.springframework.security.web.authentication.www.BasicAuthenticationFilter.class)
            
            // 2. Define URL access permissions
            .authorizeHttpRequests(auth -> auth
                // Allow public access to all static frontend pages and assets
                .requestMatchers(HttpMethod.GET,
                        "/", "/*.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Allow public access to authentication endpoints and contact queries
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/queries").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").permitAll()
                // Allow public browsing of books
                .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**").permitAll()
                // Require ADMIN role for admin management APIs
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // 3. Disable default form login redirect (we use JSON API for login)
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login-form")
                .disable())
            // 4. Configure session logout
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((req, res, auth) ->
                        res.setStatus(HttpServletResponse.SC_OK))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"))
            // 5. Custom error handling for 401 Unauthorized and 403 Forbidden
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .accessDeniedHandler((req, res, e) ->
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")));

        return http.build();
    }
}
