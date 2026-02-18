package com.openclassrooms.dataShare_api.service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * JwtService is an Entity that generates valid JSON Web Tokens using a user's email, whilst providing a set expiration date
 */
@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String SECRET;

    /**
     * Generates a token using a user's email, whilst providing a set expiration date
     * @param userDetails must contain at least an email as "userName"
     * @return the generated Jwt
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))   // lasts 30 minutes
            .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Creates a valid key to sign the Jwt with, using the app's Jwt Secret
     * @return the generated Key
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
