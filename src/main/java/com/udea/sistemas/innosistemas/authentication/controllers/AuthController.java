package com.udea.sistemas.innosistemas.authentication.controllers;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.udea.sistemas.innosistemas.authentication.models.dto.LoginDto;
import com.udea.sistemas.innosistemas.authentication.models.dto.LogoutDto;
import com.udea.sistemas.innosistemas.authentication.models.dto.RefreshTokenRequestDto;
import com.udea.sistemas.innosistemas.authentication.models.dto.TokenResponseDto;
import com.udea.sistemas.innosistemas.authentication.service.AuthService;
import com.udea.sistemas.innosistemas.exceptions.InvalidCredentialsException;
import com.udea.sistemas.innosistemas.exceptions.AuthenticationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user with email and password")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        if (!authService.authenticate(loginDto)) {
            throw new InvalidCredentialsException("Credenciales incorrectas. Verifique su email y contraseña.");
        }
        
        TokenResponseDto response = authService.GenerateWebToken(loginDto);
        if (response == null) {
            throw new AuthenticationFailedException("Error al generar el token de autenticación");
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates new access token using refresh token")
    public ResponseEntity<TokenResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        try {
            TokenResponseDto response = authService.refreshAccessToken(request.refreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new AuthenticationFailedException("Token de actualización inválido o expirado");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates user's refresh tokens")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody LogoutDto logoutDto) {
        try {
            if (authService.logout(logoutDto.email())) {
                return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No se encontraron sesiones activas para este usuario"));
            }
        } catch (Exception e) {
            throw new AuthenticationFailedException("Error al cerrar sesión: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener información del usuario actual")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        Map<String, Object> userInfo = authService.getUserInfo(authentication.getName());
        
        // Agregar información de permisos para debugging
        if (authentication != null && authentication.getAuthorities() != null) {
            List<String> permissions = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList();
            userInfo.put("permissions", permissions);
        }
        
        return ResponseEntity.ok(userInfo);
    }
}
