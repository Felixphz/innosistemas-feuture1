package com.udea.sistemas.innosistemas.authentication.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secretT:mySecretKeyForDevelopmentThatIsAtLeast32CharactersLong123456789}")
    private String secretKey;

    @Value("${jwt.expiration:900}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh.expiration:3600}")
    private Long refreshTokenExpiration;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}