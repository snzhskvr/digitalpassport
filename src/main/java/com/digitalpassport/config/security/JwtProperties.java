package com.digitalpassport.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("application.security.jwt")
public class JwtProperties {
    private Long expiration;
    private Long cacheMaxSize;
    private Long cacheTtl;
}
