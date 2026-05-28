package org.serratec.trabalho_final_api.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.serratec.trabalho_final_api.exception.ErroResposta;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        // seta o caminho do pedido de autendicacao
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getSenha(), new ArrayList<>());

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler os dados da requisição de login", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // pega os detalhes do usuário logado
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        String username = userDetails.getUsername();

        // extrai a role dele e converte para uma lista de string
        List<String> roles = userDetails.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList();

        // 3. Manda gerar o token passando o username E as roles!
        String token = jwtUtil.generateToken(username, roles);

        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"token\": \"Bearer %s\"}", token));
    }

    @Override // Autenticação do usuário
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Usuário ou senha inválidos.\"}");

        // Montando o corpo do erro
        List<String> erros = List.of("Usuário ou senha inválidos. Verifique suas credenciais.");

        ErroResposta erroResposta = new ErroResposta(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Falha na autenticação!",
                LocalDateTime.now(ZoneId.of(
                        "America/Sao_Paulo")),
                erros);

        // Converte o erroResposta em arquivo JSON
        ObjectMapper mapeia = new ObjectMapper();
        mapeia.registerModule(new JavaTimeModule());
        String jsonResposta = mapeia.writeValueAsString(erroResposta);

        // Retorna para ocorpo da requisição o arquivo convertido
        response.getWriter().write(jsonResposta);
    }

    // Método de requisição
    private static class LoginRequest {
        private String username;
        private String senha;

        public String getUsername() {
            return username;
        }

        public String getSenha() {
            return senha;
        }
    }
}