package org.serratec.trabalho_final_api.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authentication, JwtUtil jwtUtil) {

        this.authenticationManager = authentication;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication
}
