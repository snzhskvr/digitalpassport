package com.digitalpassport.config.security;

import com.digitalpassport.security.JwtFilter;
import com.digitalpassport.service.JwtService;
import com.digitalpassport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${application.security.auth.mode:jwt}")
    private String authMode;
    @Value("${application.security.csrf.enabled:true}")
    private boolean csrfEnabled;

    private final JwtService jwtService;
    private final UserService userService;
    private final JwtProperties jwtProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!csrfEnabled) {
            http.csrf(AbstractHttpConfigurer::disable);
        }

        if ("none".equalsIgnoreCase(authMode)) {
            http.authorizeHttpRequests(authorizeRequests ->
                    authorizeRequests.anyRequest().permitAll());
            return http.build();
        }

        if ("basic".equalsIgnoreCase(authMode)) {
            http.httpBasic(Customizer.withDefaults());
        } else {
            http.addFilterBefore(new JwtFilter(jwtService, userService, jwtProperties), UsernamePasswordAuthenticationFilter.class);
        }

        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/projects/**").hasRole("EDITOR")
                        .requestMatchers("/api/documents/**").hasRole("EDITOR")
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").hasRole("READER")
                        .requestMatchers(HttpMethod.GET, "/api/documents/**").hasRole("READER")
                        .anyRequest().authenticated()
        );

        return http.build();
    }
}
