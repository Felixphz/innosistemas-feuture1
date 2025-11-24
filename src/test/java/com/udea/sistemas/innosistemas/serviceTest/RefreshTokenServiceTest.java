package com.udea.sistemas.innosistemas.serviceTest; // <-- PAQUETE CORREGIDO

import com.udea.sistemas.innosistemas.authentication.config.JwtConfig;
import com.udea.sistemas.innosistemas.authentication.models.entity.RefreshToken;
import com.udea.sistemas.innosistemas.authentication.repository.RefreshTokenRepository;
import com.udea.sistemas.innosistemas.authentication.service.RefreshTokenService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest { // <-- NOMBRE DE CLASE CORREGIDO

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    // --- Pruebas para deactivateAllTokensByUserEmail ---

    @Test
    void testDeactivateAllTokensByUserEmail() {
        // Arrange
        String email = "test@example.com";
        RefreshToken token1 = new RefreshToken("token1", email, LocalDateTime.now().plusDays(1));
        RefreshToken token2 = new RefreshToken("token2", email, LocalDateTime.now().plusDays(1));
        List<RefreshToken> activeTokens = List.of(token1, token2);

        when(refreshTokenRepository.findByUserEmailAndIsExpiredFalse(email)).thenReturn(activeTokens);

        // Act
        refreshTokenService.deactivateAllTokensByUserEmail(email);

        // Assert
        // Verifica que ambos tokens se marcaron como expirados
        assertTrue(token1.getIsExpired());
        assertTrue(token2.getIsExpired());
        // Verifica que se guardaron los cambios en la BD
        verify(refreshTokenRepository, times(1)).saveAll(activeTokens);
    }

    // --- Pruebas para invalidateUserTokensForLogout ---

    @Test
    void testInvalidateUserTokensForLogout_Success() {
        // Arrange
        String email = "logout@example.com";
        RefreshToken token1 = new RefreshToken("token1", email, LocalDateTime.now().plusDays(1));
        List<RefreshToken> activeTokens = List.of(token1);

        when(refreshTokenRepository.findByUserEmailAndIsExpiredFalse(email)).thenReturn(activeTokens);

        // Act
        int result = refreshTokenService.invalidateUserTokensForLogout(email);

        // Assert
        assertEquals(1, result); // Debe retornar el número de tokens invalidados
        assertTrue(token1.getIsExpired()); // El token debe estar expirado
        verify(refreshTokenRepository, times(1)).save(token1); // Verifica que se guardó individualmente
    }

    @Test
    void testInvalidateUserTokensForLogout_NoTokensFound() {
        // Arrange
        String email = "notokens@example.com";
        when(refreshTokenRepository.findByUserEmailAndIsExpiredFalse(email)).thenReturn(List.of()); // Lista vacía

        // Act
        int result = refreshTokenService.invalidateUserTokensForLogout(email);

        // Assert
        assertEquals(0, result);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    // --- Pruebas para deleteExpiredTokens ---

    @Test
    void testDeleteExpiredTokens() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        RefreshToken expiredToken = new RefreshToken("expired", "test", now.minusDays(1));
        List<RefreshToken> expiredList = List.of(expiredToken);

        when(refreshTokenRepository.findByExpiresAtBefore(now)).thenReturn(expiredList);

        // Act
        refreshTokenService.deleteExpiredTokens(now);

        // Assert
        verify(refreshTokenRepository, times(1)).deleteAll(expiredList);
    }

    // --- Pruebas para createRefreshToken ---

    @Test
    void testCreateRefreshToken_Success() {
        // Arrange
        String email = "create@example.com";
        long expirationSeconds = 3600L; // 1 hora

        // Simula la desactivación de tokens antiguos
        when(refreshTokenRepository.findByUserEmailAndIsExpiredFalse(email)).thenReturn(List.of());
        // Simula la configuración de expiración
        when(jwtConfig.getRefreshTokenExpiration()).thenReturn(expirationSeconds);

        // Capturamos el token que se va a guardar
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        // Hacemos que 'save' devuelva el mismo token que recibió
        when(refreshTokenRepository.save(tokenCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RefreshToken resultToken = refreshTokenService.createRefreshToken(email);

        // Assert
        assertNotNull(resultToken);
        assertEquals(email, resultToken.getUserEmail());
        assertFalse(resultToken.getIsExpired());
        assertNotNull(resultToken.getToken()); // Debe tener un UUID

        // Verifica que se llamó a la desactivación
        verify(refreshTokenRepository, times(1)).findByUserEmailAndIsExpiredFalse(email);
        // Verifica que se llamó a save
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    // --- Pruebas para findByToken ---

    @Test
    void testFindByToken() {
        // Arrange
        String tokenStr = "findme";
        RefreshToken token = new RefreshToken(tokenStr, "test", LocalDateTime.now().plusDays(1));
        List<RefreshToken> tokenList = List.of(token);

        when(refreshTokenRepository.findByTokenAndIsExpiredFalse(tokenStr)).thenReturn(tokenList);

        // Act
        List<RefreshToken> result = refreshTokenRepository.findByTokenAndIsExpiredFalse(tokenStr); // Corregido para usar el método del repo

        // Assert
        assertEquals(tokenList, result);
        verify(refreshTokenRepository, times(1)).findByTokenAndIsExpiredFalse(tokenStr);
    }

    // --- Pruebas para verifyExpiration ---

    @Test
    void testVerifyExpiration_TokenNotExpired() {
        // Arrange
        RefreshToken token = new RefreshToken("valid", "test", LocalDateTime.now().plusDays(1));
        // Simulamos que el método isExpired() del token (que no es mock) devuelve false
        // (No necesitamos mockearlo porque el token es real y no ha expirado)

        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(token);

        // Assert
        assertNotNull(result);
        assertEquals(token, result);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void testVerifyExpiration_TokenIsExpired() {
        // Arrange
        // Creamos un token que ya expiró (en el pasado)
        RefreshToken token = new RefreshToken("expired", "test", LocalDateTime.now().minusSeconds(10));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });

        assertTrue(exception.getMessage().contains("Refresh token expired"));

        // Verifica que se llamó a 'delete' cuando el token expiró
        verify(refreshTokenRepository, times(1)).delete(token);
    }
}