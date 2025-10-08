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

    @Value("${app.admin.username:admin}")
    private String adminUser;

    @Value("${app.admin.password:admin123}")
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
                // keep API POSTs free of CSRF if desired (we left this earlier)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // login page should be accessible
                        .requestMatchers("/admin/login", "/admin/login/**").permitAll()
                        // static resources allowed
                        .requestMatchers("/uploads/**", "/static/**", "/css/**", "/js/**", "/images/**", "/", "/submit.html", "/track.html", "/recent-map.html", "/").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/complaints/**").permitAll()
                        .anyRequest().permitAll()
                )
                // form-based login for admin
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")    // POST URL for credentials
                        .defaultSuccessUrl("/admin/complaints", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults()); // keep basic for APIs/tools if you want
        return http.build();
    }
}
