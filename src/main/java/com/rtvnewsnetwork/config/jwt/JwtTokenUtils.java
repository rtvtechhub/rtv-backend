package com.rtvnewsnetwork.config.jwt;
import com.rtvnewsnetwork.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenUtils {

    @Value("${rtv.jwt.lifetime}")
    private long jwtLifeDuration;

    @Value("${rtv.refreshToken.lifetime}")
    private long refreshTokenLifeDuration;

    @Value("${rtv.jwt.secret}")
    private String secretKey;

    @Value("${rtv.refreshToken.secret}")
    private String refreshTokenSecretKey;

    public String generateToken(User user) {
        return Jwts.builder()
                .claim("username", user.getUsername())
                .subject(user.getId())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(jwtLifeDuration)))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .claim("type", "Refresh Token")
                .subject(user.getId())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(refreshTokenLifeDuration)))
                .signWith(getRefreshTokenKey())
                .compact();
    }

    public SecretKey getKey() {
        return new SecretKeySpec(
                Base64.getDecoder().decode(secretKey),
                "HmacSHA256"
        );
    }

    private SecretKey getRefreshTokenKey() {
        return new SecretKeySpec(
                Base64.getDecoder().decode(refreshTokenSecretKey),
                "HmacSHA256"
        );
    }

    public Jws<Claims> parse(String jwtString) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(jwtString);
    }

    public Jws<Claims> parseRefreshToken(String jwtString) {
        return Jwts.parser()
                .verifyWith(getRefreshTokenKey())
                .build()
                .parseSignedClaims(jwtString);
    }
}
