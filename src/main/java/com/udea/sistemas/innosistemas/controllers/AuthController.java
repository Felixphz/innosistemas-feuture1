package com.udea.sistemas.innosistemas.controllers;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.udea.sistemas.innosistemas.models.dto.LoginDto;
import com.udea.sistemas.innosistemas.models.dto.LogoutDto;
import com.udea.sistemas.innosistemas.models.dto.RefreshTokenRequestDto;
import com.udea.sistemas.innosistemas.models.dto.TokenResponseDto;
import com.udea.sistemas.innosistemas.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            if (authService.authenticate(loginDto)) {
                TokenResponseDto response = authService.GenerateWebToken(loginDto);
                if (response != null) {
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates new access token using refresh token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        try {
            TokenResponseDto response = authService.refreshAccessToken(request.refreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates user's refresh tokens")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutDto logoutDto) {
        try {
            if (authService.logout(logoutDto.email())) {
                return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No active sessions found for this user"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Logout failed: " + e.getMessage()));
        }
    }
}
