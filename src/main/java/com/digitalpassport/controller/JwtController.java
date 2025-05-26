package com.digitalpassport.controller;

import com.digitalpassport.exception.InvalidUsernameOrPasswordException;
import com.digitalpassport.model.jwt.JwtRequest;
import com.digitalpassport.model.jwt.JwtResponse;
import com.digitalpassport.service.JwtService;
import com.digitalpassport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class JwtController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public JwtResponse auth(@RequestBody JwtRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new InvalidUsernameOrPasswordException();
        }

        final UserDetails user = userService.loadUserByUsername(request.getUsername());
        final String token = jwtService.generateToken(user);
        return new JwtResponse(token);
    }
}
