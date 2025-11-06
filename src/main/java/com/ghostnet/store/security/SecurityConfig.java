package com.ghostnet.store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import com.ghostnet.store.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;

/**
 * Security Konfiguration für Spring Security
 * Definiert Authentication, Authorization und Zugriffsrechte
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    private final UserService appUserService;

    /**
     * Konfiguriert den UserDetailsService für Authentication
     * @return UserService als UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }

    /**
     * Konfiguriert den Password Encoder
     * @return BCryptPasswordEncoder für Passwort-Verschlüsselung
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfiguriert den Authentication Manager
     * @param config Authentication Configuration
     * @return Authentication Manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Konfiguriert die Security Filter Chain
     * Definiert welche Routes öffentlich sind und welche Authentication benötigen
     * @param httpSecurity HttpSecurity Builder
     * @return Konfigurierte Security Filter Chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
            .userDetailsService(userDetailsService())
            .formLogin(httpForm -> {
                // Login-Konfiguration
                httpForm.loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login.html?error=true")
                        .permitAll();
            })
            .logout(logout -> {
                // Logout-Konfiguration
                logout.logoutUrl("/logout")
                      .logoutSuccessUrl("/login.html?logout=true")
                      .permitAll();
            })
            .authorizeHttpRequests(registry -> {
                // Öffentliche Routes (ohne Authentication)
                registry.requestMatchers("/", "/index.html", "/login.html", "/signup.html", "/req/signup", "/assets/**", "/styles.css", "/error", "/impressum", "/datenschutz", "/agb").permitAll();
                // Anonyme Geisternetz-Meldungen erlauben
                registry.requestMatchers(HttpMethod.POST, "/api/ghostnets").permitAll();
                // Geschützte Routes (benötigen Authentication)
                registry.requestMatchers("/dashboard", "/dashboard/**", "/dashboard.html", 
                                          "/meine-netze", "/meine-netze/**", "/meine-netze.html", 
                                          "/meldungen", "/meldungen/**", "/meldungen.html", 
                                          "/bergungen", "/bergungen/**", "/bergungen.html", 
                                          "/api/user/**", "/api/ghostnets/**", "/api/config/**", "/api/reassignment-requests/**").authenticated();
                // Alle anderen Requests benötigen Authentication
                registry.anyRequest().authenticated();
            })

            .build();
    }

}
