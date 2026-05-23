package org.serratec.trabalho_final_api.security;

import java.io.IOException;
import java.util.ArrayList;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.LoginDTO;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authentication, JwtUtil jwtUtil) {

        this.authenticationManager = authentication;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attempAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            LoginDTO usuario = new ObjectMapper()
                    .readValue(request.getInputStream(),
                            LoginDTO.class);

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usario.username(), usuario.)
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
