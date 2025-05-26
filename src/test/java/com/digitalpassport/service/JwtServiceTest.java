package com.digitalpassport.service;

import com.digitalpassport.BaseIntegrationTest;
import com.digitalpassport.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtServiceTest extends BaseIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Test
    public void testJwt() {
        User user = new User();
        user.setUsername("user");
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);
        assertEquals(user.getUsername(), username);
    }
}
