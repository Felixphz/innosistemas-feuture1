package com.udea.sistemas.innosistemas.models.dto;

public record CreateUserDto(
    String email,
    String nameUser,
    String password
) {}
