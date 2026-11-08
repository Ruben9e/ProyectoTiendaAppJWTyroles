package com.proyectotienda.tienda.service;

import com.proyectotienda.tienda.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms:600000}") long expirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        if (keyBytes.length < 32) throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes aleatorios en Base64");
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(Users user) {
        Date now = new Date();
        return Jwts.builder()
                .claims(Map.of("userId", user.getId(), "rolId", user.getRole().getId()))
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public Claims validateAndRead(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }
}
