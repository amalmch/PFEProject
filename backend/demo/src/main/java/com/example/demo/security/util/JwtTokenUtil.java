package com.example.demo.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;  // The secret key for signing and validating JWT

    @Value("${jwt.expiration:86400000}")
    private long expirationTime;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Expiry time
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();  // Extract the username (subject) from the token
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();  // Extract the expiration date from the token
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // Check if the token is expired
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));  // Validate the token
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Use the secret key for parsing
                .build()
                .parseClaimsJws(token)  // Parse the JWT
                .getBody();  // Get the body (claims) of the token
    }
}
