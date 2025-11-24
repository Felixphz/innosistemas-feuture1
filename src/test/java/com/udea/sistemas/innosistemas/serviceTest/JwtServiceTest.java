package com.udea.sistemas.innosistemas.serviceTest;

import com.udea.sistemas.innosistemas.authentication.config.JwtConfig;
import com.udea.sistemas.innosistemas.authentication.service.JwtService;
import com.udea.sistemas.innosistemas.models.entity.Permission;
import com.udea.sistemas.innosistemas.models.entity.Role;
import com.udea.sistemas.innosistemas.models.entity.RolesPermission;
import com.udea.sistemas.innosistemas.models.entity.User;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private JwtService jwtService;

    private User testUser;
    private SecretKey testKey;
    private final long ACCESS_TOKEN_EXPIRATION = 3600L; // 1 hora
    private final long REFRESH_TOKEN_EXPIRATION = 86400L; // 24 horas

    @BeforeEach
    void setUp() {
        // 1. Crear una clave secreta real para las pruebas
        String secretString = "unaClaveSecretaMuyLargaParaPruebasDeJunit1234567890";
        testKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

        // 2. Simular el JwtConfig (SOLO la clave, que usan TODOS los tests)
        when(jwtConfig.getSigningKey()).thenReturn(testKey);

        // --- CORRECCIÓN ---
        // Se movieron las simulaciones de expiración a cada test
        // para evitar el UnnecessaryStubbingException

        // 3. Crear un usuario de prueba complejo con rol y permisos
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setNameUser("Test User");

        Role userRole = new Role();
        userRole.setId(1);
        userRole.setNameRol("Admin");

        Permission perm1 = new Permission();
        perm1.setNamePermission("read_user");

        RolesPermission rolesPermission = new RolesPermission();
        rolesPermission.setPermissions(perm1); // Asigna el permiso

        userRole.setRolesPermission(List.of(rolesPermission)); // Asigna la relación al rol

        testUser.setRole(userRole); // Asigna el rol al usuario
    }

    @Test
    void testGenerateAccessToken_And_ExtractAllClaims() {
        // Arrange
        // --- CORRECCIÓN ---
        // Se define la simulación de expiración solo para este test
        when(jwtConfig.getAccessTokenExpiration()).thenReturn(ACCESS_TOKEN_EXPIRATION);

        // Act
        // 1. Generar el token
        String token = jwtService.generateAccessToken(testUser);

        // Assert
        assertNotNull(token);

        // 2. Probar todos los métodos 'extract' usando el token que acabamos de generar
        assertEquals("test@example.com", jwtService.extractEmail(token));
        assertEquals("Test User", jwtService.extractName(token));
        assertEquals("Admin", jwtService.extractRole(token));

        List<String> permissions = jwtService.extractPermissions(token);
        assertNotNull(permissions);
        assertEquals(1, permissions.size());
        assertEquals("read_user", permissions.get(0));

        // 3. Probar la expiración
        Date expiration = jwtService.extractExpiration(token);
        Date expectedExpiration = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION * 1000);

        assertNotNull(expiration);
        // Comprueba que la expiración está dentro de un rango razonable (ej. 10 segundos de margen)
        assertTrue(Math.abs(expectedExpiration.getTime() - expiration.getTime()) < 10000);
    }

    @Test
    void testGenerateRefreshToken() {
        // Arrange
        // --- CORRECCIÓN ---
        // Se define la simulación de expiración solo para este test
        when(jwtConfig.getRefreshTokenExpiration()).thenReturn(REFRESH_TOKEN_EXPIRATION);

        // Act
        String token = jwtService.generateRefreshToken(testUser);

        // Assert
        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractEmail(token));
        assertEquals("refresh", jwtService.extractTokenType(token)); // Verifica el 'type'
    }

    @Test
    void testValidateToken_Success() {
        // Arrange
        // --- CORRECCIÓN ---
        // Se necesita la expiración del token de acceso para generarlo
        when(jwtConfig.getAccessTokenExpiration()).thenReturn(ACCESS_TOKEN_EXPIRATION);
        String token = jwtService.generateAccessToken(testUser);

        // Act
        boolean isValid = jwtService.validateToken(token, "test@example.com");

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_Fail_WrongEmail() {
        // Arrange
        // --- CORRECCIÓN ---
        // Se necesita la expiración del token de acceso para generarlo
        when(jwtConfig.getAccessTokenExpiration()).thenReturn(ACCESS_TOKEN_EXPIRATION);
        String token = jwtService.generateAccessToken(testUser);

        // Act
        boolean isValid = jwtService.validateToken(token, "wrong@example.com");

        // Assert
        assertFalse(isValid);
    }
}