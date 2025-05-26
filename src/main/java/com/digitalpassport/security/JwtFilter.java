package com.digitalpassport.security;

import com.digitalpassport.config.security.JwtProperties;
import com.digitalpassport.service.JwtService;
import com.digitalpassport.service.UserService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final LoadingCache<String, UserDetails> userCache;

    public JwtFilter(JwtService jwtService, UserService userService, JwtProperties jwtProperties) {
        this.jwtService = jwtService;
        this.userCache = CacheBuilder.newBuilder()
                .maximumSize(jwtProperties.getCacheMaxSize())
                .expireAfterWrite(jwtProperties.getCacheTtl(), TimeUnit.MILLISECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public UserDetails load(String username) {
                        return userService.loadUserByUsername(username);
                    }
                });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            final String authorizationHeader = request.getHeader("Authorization");

            String jwtToken;
            String username = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7);

                try {
                    username = jwtService.extractUsername(jwtToken);
                } catch (ExpiredJwtException ex) {
                    log.warn("JWT token is expired. Token: {}", jwtToken);
                } catch (SignatureException ex) {
                    log.warn("Invalid JWT signature. Token: {}", jwtToken);
                }
            }

            if (username != null) {
                UserDetails user = userCache.getUnchecked(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
