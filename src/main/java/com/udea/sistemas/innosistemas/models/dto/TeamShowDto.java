package com.udea.sistemas.innosistemas.models.dto;
import com.udea.sistemas.innosistemas.enums.TeamState;

import java.util.List;

public record TeamShowDto(
        Integer idTeam,
        String nameTeam,
        Integer projectId,
        String projectName,
        Integer courseId,
        TeamState state, List<UserDto> students
) {}
