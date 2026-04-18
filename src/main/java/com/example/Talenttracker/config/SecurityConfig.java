package com.example.Talenttracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * Chain 1: REST API (/api/**) — stateless JWT authentication.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()

                    // User management (ADMIN only)
                    .requestMatchers("/api/users/**").hasRole("ADMIN")

                    // Jobs
                    .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers(HttpMethod.PATCH, "/api/jobs/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers(HttpMethod.GET, "/api/jobs/**").authenticated()

                    // Candidates
                    .requestMatchers(HttpMethod.POST, "/api/candidates/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers(HttpMethod.PUT, "/api/candidates/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers(HttpMethod.DELETE, "/api/candidates/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/candidates/**").authenticated()

                    // Applications
                    .requestMatchers(HttpMethod.POST, "/api/applications/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers(HttpMethod.PATCH, "/api/applications/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers(HttpMethod.GET, "/api/applications/**").authenticated()

                    // Interviews
                    .requestMatchers(HttpMethod.POST, "/api/interviews/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers(HttpMethod.PATCH, "/api/interviews/**").hasAnyRole("ADMIN", "RECRUITER", "INTERVIEWER")
                    .requestMatchers(HttpMethod.GET, "/api/interviews/**").authenticated()

                    // Feedback
                    .requestMatchers(HttpMethod.POST, "/api/feedback/**").hasAnyRole("ADMIN", "INTERVIEWER")
                    .requestMatchers(HttpMethod.GET, "/api/feedback/**").authenticated()

                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Chain 2: Web UI — session-based form login for Thymeleaf pages.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**",
                                     "/swagger-ui/**", "/swagger-ui.html",
                                     "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                    .requestMatchers("/reports").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers("/users/**").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers("/candidates/*/delete", "/jobs/*/delete", "/applications/*/delete", "/interviews/*/delete", "/feedback/*/delete").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers("/jobs/new", "/candidates/new", "/applications/new", "/interviews/new").hasAnyRole("ADMIN", "RECRUITER")
                    .requestMatchers("/applications/*/status").authenticated()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/dashboard", true)
                    .failureUrl("/login?error")
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
