package com.rtvnewsnetwork.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;



@Component
public class JwtFileTokenUtils {

    @Value("${rtv.jwt.lifetime}")
    private long jwtLifeDuration;


    @Value("${rtv.jwt.secret}")
    private String secretKey;

    @Value("${rtv.refreshToken.secret}")
    private String refreshTokenSecretKey;

    public String generateToken() {
        return Jwts.builder()
                .subject("rAFJMm7CWCfVtzch3doHgSzTlmPymeauEM23RPLa80FNdKePHkX90aLKvaz8PewZ")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(jwtLifeDuration)))
                .signWith(getKey())
                .compact();
    }


    public SecretKey getKey() {
        return new SecretKeySpec(
                Base64.getDecoder().decode(secretKey),
                "HmacSHA256"
        );
    }

}

