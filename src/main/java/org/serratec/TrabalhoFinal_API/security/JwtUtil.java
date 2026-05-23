package org.serratec.TrabalhoFinal_API.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("{auth.jwt-secret}")
    private String jwtSecret;

    @Value("$auth.jwt-expiration-miliseg")
    private Long jwtExpiratioLong;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder().setSubject(username)
                .setExpiration(
                        new Date(System.currentTimeMillis() + this.jwtExpiratioLong))
                .signWith(secretKey)
                .compact();
    }

    public boolean isValidToken(String token) {
        Claims claims = getClaims(token);

        if (claims == null)
            return false;

        String username = claims.getSubject();
        Date expirationDate = claims.getExpiration();
        Date now = new Date(System.currentTimeMillis());

        if (username != null && expirationDate != null && now.before(expirationDate))
            return true;

        return false;
    }
}
