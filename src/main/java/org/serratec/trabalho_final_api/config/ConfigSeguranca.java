package org.serratec.trabalho_final_api.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfigSeguranca {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/usuarios").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/lista-favoritos/filmes/publica").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/lista-favoritos/series/publica").permitAll();
                    request.anyRequest().authenticated();
                }).httpBasic(Customizer.withDefaults());
        return http.build();
    }

    // @Bean
    // public InMemoryUserDetailsManager userDetailsService() {
    // // Usuario padrão teste
    // UserDetails user = User.builder()
    // .username("usuario_comum")
    // .password(bCryptPasswordEncoder().encode("123456"))
    // .roles("USER")
    // .build();

    // // Usuario admin teste
    // UserDetails admin = User.builder()
    // .username("admin")
    // .password(bCryptPasswordEncoder().encode("admin123"))
    // .roles("ADMIN")
    // .build();

    // return new InMemoryUserDetailsManager(user, admin);
    // }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:6000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Importante adicionar se usar cabeçalhos customizados

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}