package com.udea.sistemas.innosistemas.models.dto;

public record TokenResponseDto(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn
) {}

