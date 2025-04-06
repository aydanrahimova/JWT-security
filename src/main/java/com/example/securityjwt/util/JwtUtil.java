package com.example.securityjwt.util;

import com.example.securityjwt.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtUtil {
    @Value("${application.security.jwt.secret-key}")
    String secretKey;
    @Value("${application.security.jwt.expiration}")
    Long jwtExpirationTime;
    @Value("${application.security.jwt.refresh-token.expiration}")
    Long refreshExpirationTime;

    public String generateAccessToken(User user) {
        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", roles);
        claims.put("userId", user.getId());
        return buildToken(claims, user, jwtExpirationTime);
    }


    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpirationTime);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, Long expiration) {
        return Jwts
                .builder()
                .setSubject(user.getEmail())
                .addClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

//    public Claims getClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Return claims even for expired tokens
            return e.getClaims();
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = getClaims(token);
        return claimResolver.apply(claims);
    }

    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, User user) {
        final String userEmail = extractUsername(token);
        return userEmail.equals(user.getEmail()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException e) {
            return true;
        }
    }


}
