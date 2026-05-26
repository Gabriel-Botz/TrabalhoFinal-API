package org.serratec.trabalho_final_api.config;

import java.util.Arrays;

import org.serratec.trabalho_final_api.security.JwtAuthenticationFilter;
import org.serratec.trabalho_final_api.security.JwtAuthorizationFilter; // Certifique-se de importar
import org.serratec.trabalho_final_api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfigSeguranca {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authManager = authenticationConfiguration.getAuthenticationManager();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authManager, jwtUtil);

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // =================================================================
                        // 1. ACESSO PÚBLICO (ALL: Com ou Sem Cadastro)
                        // =================================================================
                        // Permitir registrar (POST /usuarios) sem estar logado
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // Permitir acesso anônimo para listagem e buscas de filmes e séries
                        .requestMatchers(HttpMethod.GET, "/filmes", "/filmes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/series", "/series/**").permitAll()

                        // Swagger e OpenAPI Docs
                        .requestMatchers("/api-docs", "/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources", "/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // =================================================================
                        // 2. REGRAS COMPARTILHADAS (ROLE_USER e ROLE_ADMIN)// =============
                        // Operações de escrita em filmes/séries e gerenciamento de conta exigem autenticação
                        .requestMatchers(HttpMethod.POST, "/filmes", "/series").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/{id}", "/filmes/{id}", "/series/{id}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/{id}", "/filmes/{id}", "/series/{id}").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuarios/id").hasAnyRole("ADMIN")

                        // =================================================================
                        // 3. REGRAS EXCLUSIVAS ADMIN
                        // =================================================================
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("ADMIN")
                        .requestMatchers("/usuarios/salvar-lista").hasRole("ADMIN")
                        .requestMatchers("/categorias/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterBefore(new JwtAuthorizationFilter(authManager, jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
            return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:6000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}