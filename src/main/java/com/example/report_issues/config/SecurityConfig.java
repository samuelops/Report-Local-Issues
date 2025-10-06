package com.example.report_issues.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("deprecation") // for NoOpPasswordEncoder in dev only
@Configuration
public class SecurityConfig {

    @Value("${app.admin.username}")
    private String adminUser;

    @Value("${app.admin.password}")
    private String adminPass;

    @Bean
    public UserDetailsService users() {
        UserDetails admin = User.withUsername(adminUser)
                .password("{noop}" + adminPass) // {noop} for plain text in dev
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // allow API POSTs without CSRF token
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/login", "/uploads/**", "/submit.html", "/api/complaints/**", "/health", "/css/**", "/js/**", "/favicon.ico", "/").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // admin via basic auth
                .formLogin(Customizer.withDefaults()) // optional form login page
                .logout(logout -> logout.logoutUrl("/logout").permitAll());

        return http.build();
    }
}
