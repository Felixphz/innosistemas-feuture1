package com.udea.sistemas.innosistemas.serviceTest; // <-- 1. PAQUETE CORREGIDO

import com.udea.sistemas.innosistemas.authentication.models.dto.LoginDto;
import com.udea.sistemas.innosistemas.authentication.models.dto.TokenResponseDto;
import com.udea.sistemas.innosistemas.authentication.models.entity.RefreshToken;
import com.udea.sistemas.innosistemas.authentication.service.AuthService;
import com.udea.sistemas.innosistemas.authentication.service.JwtService;
// 2. IMPORT CORREGIDO (apunta al servicio real, NO al test)
import com.udea.sistemas.innosistemas.authentication.service.RefreshTokenService;
import com.udea.sistemas.innosistemas.models.entity.Role;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginDto loginDto;

    // Preparamos un usuario de prueba antes de cada test
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setNameUser("Test User");
        testUser.setPassword("encodedPassword");

        Role testRole = new Role();
        testRole.setId(1);
        testRole.setNameRol("Estudiante");
        // Importante simular los permisos, aunque esté vacío, para que JwtService funcione
        testRole.setRolesPermission(List.of());
        testUser.setRole(testRole);

        loginDto = new LoginDto("test@example.com", "password123");
    }

    // --- Pruebas para authenticate ---

    @Test
    void testAuthenticate_Success() {
        // Arrange
        when(userRepository.findByEmail(loginDto.email())).thenReturn(testUser);
        when(passwordEncoder.matches(loginDto.password(), testUser.getPassword())).thenReturn(true);

        // Act
        boolean result = authService.authenticate(loginDto);

        // Assert
        assertTrue(result);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(loginDto.email())).thenReturn(null);

        // Act
        boolean result = authService.authenticate(loginDto);

        // Assert
        assertFalse(result);
        verify(passwordEncoder, never()).matches(anyString(), anyString()); // Nunca debe comparar contraseña
    }

    @Test
    void testAuthenticate_PasswordMismatch() {
        // Arrange
        when(userRepository.findByEmail(loginDto.email())).thenReturn(testUser);
        when(passwordEncoder.matches(loginDto.password(), testUser.getPassword())).thenReturn(false);

        // Act
        boolean result = authService.authenticate(loginDto);

        // Assert
        assertFalse(result);
    }

    // --- Pruebas para GenerateWebToken ---

    @Test
    void testGenerateWebToken_Success() {
        // Arrange
        RefreshToken refreshToken = new RefreshToken("uuid-token", loginDto.email(), LocalDateTime.now().plusDays(1));

        when(userRepository.findByEmail(loginDto.email())).thenReturn(testUser);
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token-generado");
        when(refreshTokenService.createRefreshToken(testUser.getEmail())).thenReturn(refreshToken);

        // Act
        TokenResponseDto response = authService.GenerateWebToken(loginDto);

        // Assert
        assertNotNull(response);
        assertEquals("access-token-generado", response.accessToken());
        assertEquals("uuid-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
    }

    // --- Pruebas para refreshAccessToken ---

    @Test
    void testRefreshAccessToken_Success() {
        // Arrange
        String oldRefreshTokenStr = "old-refresh-token";
        RefreshToken refreshToken = new RefreshToken(oldRefreshTokenStr, testUser.getEmail(), LocalDateTime.now().plusDays(1));

        when(refreshTokenService.findByToken(oldRefreshTokenStr)).thenReturn(List.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(jwtService.generateAccessToken(testUser)).thenReturn("new-access-token");

        // Act
        TokenResponseDto response = authService.refreshAccessToken(oldRefreshTokenStr);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals(oldRefreshTokenStr, response.refreshToken());
    }

    @Test
    void testRefreshAccessToken_Fail_TokenNotFound() {
        // Arrange
        String badToken = "bad-token";
        when(refreshTokenService.findByToken(badToken)).thenReturn(List.of()); // Lista vacía

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshAccessToken(badToken);
        });

        assertTrue(exception.getMessage().contains("Refresh token not found or expired"));
    }

    // --- Pruebas para logout ---

    @Test
    void testLogout_Success() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(testUser);
        when(refreshTokenService.invalidateUserTokensForLogout(email)).thenReturn(1); // 1 token fue invalidado

        // Act
        boolean result = authService.logout(email);

        // Assert
        assertTrue(result);
    }

    @Test
    void testLogout_Fail_NoTokensToExpire() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(testUser);
        when(refreshTokenService.invalidateUserTokensForLogout(email)).thenReturn(0); // 0 tokens fueron invalidados

        // Act
        boolean result = authService.logout(email);

        // Assert
        assertFalse(result); // Si no se invalidó nada, retorna false
    }

    @Test
    void testLogout_Fail_UserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act
        boolean result = authService.logout(email);

        // Assert
        assertFalse(result);
        verify(refreshTokenService, never()).invalidateUserTokensForLogout(anyString());
    }

    @Test
    void testLogout_Fail_NullOrEmptyEmail() {
        // Act & Assert
        assertFalse(authService.logout(null));
        assertFalse(authService.logout(""));
    }

    // --- Pruebas para getUserInfo ---

    @Test
    void testGetUserInfo_Success() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(testUser);

        // Act
        Map<String, Object> userInfo = authService.getUserInfo(email);

        // Assert
        assertNotNull(userInfo);
        assertEquals(testUser.getEmail(), userInfo.get("email"));
        assertEquals(testUser.getNameUser(), userInfo.get("name"));
        assertEquals(testUser.getRole().getNameRol(), userInfo.get("role"));
    }

    @Test
    void testGetUserInfo_Fail_UserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.getUserInfo(email);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }
}