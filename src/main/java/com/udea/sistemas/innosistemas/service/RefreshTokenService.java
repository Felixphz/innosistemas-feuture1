package com.udea.sistemas.innosistemas.service;

import com.udea.sistemas.innosistemas.config.JwtConfig;
import com.udea.sistemas.innosistemas.models.entity.RefreshToken;
import com.udea.sistemas.innosistemas.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtConfig jwtConfig) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtConfig = jwtConfig;
    }

    @Transactional
    public void deactivateAllTokensByUserEmail(String userEmail) {
        List<RefreshToken> activeTokens = refreshTokenRepository
            .findByUserEmailAndIsExpiredFalse(userEmail);
        
        activeTokens.forEach(token -> token.setIsExpired(true));

        refreshTokenRepository.saveAll(activeTokens);
    }

    @Transactional
    public int invalidateUserTokensForLogout(String userEmail) {
        List<RefreshToken> activeTokens = refreshTokenRepository
            .findByUserEmailAndIsExpiredFalse(userEmail);
        
        System.out.println("Found " + activeTokens.size() + " active tokens for logout: " + userEmail);
        
        if (activeTokens.isEmpty()) {
            return 0;
        }
        
        // Modificar y guardar cada token individualmente
        for (RefreshToken token : activeTokens) {
            System.out.println("Expiring token: " + token.getToken());
            token.setIsExpired(true);
            refreshTokenRepository.save(token); // Guardar inmediatamente cada uno
        }

        System.out.println("Successfully expired " + activeTokens.size() + " tokens");
        return activeTokens.size();
    }

    @Transactional
    public void deleteExpiredTokens(LocalDateTime now) {
        List<RefreshToken> expiredTokens = refreshTokenRepository
            .findByExpiresAtBefore(now);
        
        refreshTokenRepository.deleteAll(expiredTokens);
    }

    @Transactional
    public RefreshToken createRefreshToken(String userEmail) {
        // Desactivar tokens anteriores del usuario
        deactivateAllTokensByUserEmail(userEmail);
        
        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(jwtConfig.getRefreshTokenExpiration());
        
        RefreshToken refreshToken = new RefreshToken(token, userEmail, expiryDate);
        return refreshTokenRepository.save(refreshToken);
    }

    public List<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByTokenAndIsExpiredFalse(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }

}
