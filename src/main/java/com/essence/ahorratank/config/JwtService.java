package com.essence.ahorratank.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final String SECRET_KEY = "95967fb15ab686235c2720414c15e2d2174b6bb79cf80c3a217164d63b85ef15";

    public JwtService() {
    }

    public String generateToken(UserDetails userDetails) {
        return this.generateToken(new HashMap(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).claim("authorities", userDetails.getAuthorities()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 86400000L)).signWith(this.getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateResetToken(String email, long minutes) {
        return Jwts.builder().setSubject(email).claim("purpose", "RESET_PASSWORD").setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + minutes * 60L * 1000L)).signWith(this.getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    public String getUserName(String jwt) {
        return (String)this.getClaim(jwt, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = this.getUserName(token);
        return username.equals(userDetails.getUsername()) && !this.isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return this.getExpiration(token).before(new Date());
    }

    private Date getExpiration(String token) {
        return (Date)this.getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.getAllClaims(token);
        return (T)claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return (Claims)Jwts.parserBuilder().setSigningKey(this.getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = (byte[])Decoders.BASE64.decode("95967fb15ab686235c2720414c15e2d2174b6bb79cf80c3a217164d63b85ef15");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}