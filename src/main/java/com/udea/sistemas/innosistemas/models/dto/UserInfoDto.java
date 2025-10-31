package com.udea.sistemas.innosistemas.models.dto;
import java.util.List;
import com.udea.sistemas.innosistemas.models.entity.Permission;

public record UserInfoDto(
    String email,
    String name,
    String role,
    List<Permission> permissions
){}