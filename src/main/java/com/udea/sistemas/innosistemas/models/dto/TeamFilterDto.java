package com.udea.sistemas.innosistemas.models.dto;

public record TeamFilterDto(
        String nameTeam,
        Integer projectId,
        Integer courseId,
        String status // “FORMADO” | “INCOMPLETO”
) {}
