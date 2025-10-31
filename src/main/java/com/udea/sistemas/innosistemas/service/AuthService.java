package com.udea.sistemas.innosistemas.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.udea.sistemas.innosistemas.models.dto.LoginDto;
import com.udea.sistemas.innosistemas.models.dto.TokenResponseDto;
import com.udea.sistemas.innosistemas.models.entity.RefreshToken;
import com.udea.sistemas.innosistemas.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public boolean authenticate(LoginDto authDto) {
        var user = userRepository.findByEmail(authDto.email());
        if (user == null || !passwordEncoder.matches(authDto.password(), user.getPassword())) {
            return false;
        }
        return true;
    }


    public TokenResponseDto GenerateWebToken(LoginDto authDto) {
        var user = userRepository.findByEmail(authDto.email());
        String accessToken = jwtService.generateAccessToken(user);
            
        // Crear y almacenar refresh token en BD
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user.getEmail());
        
        return new TokenResponseDto(
            accessToken,
            refreshTokenEntity.getToken(),
            "Bearer",
            900L
        );
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .stream()
                .findFirst()
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserEmail)
                .map(userRepository::findByEmail)
                .map(user -> {
                    String accessToken = jwtService.generateAccessToken(user);
            
                    return new TokenResponseDto(
                        accessToken,
                        refreshToken, // Mantener el mismo refresh token
                        "Bearer",
                        900L
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found or expired"));
    }
    
    @Transactional
    public boolean logout(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return false;
        }
        
        if (userRepository.findByEmail(userEmail) == null) {
            return false;
        }
        
        try {
            int tokensExpired = refreshTokenService.invalidateUserTokensForLogout(userEmail);
            return tokensExpired > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
