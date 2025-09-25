package com.udea.sistemas.innosistemas.models.dto;
import java.util.List;

public record TeamShowDto(Integer idTeam,String nameTeam,Integer projectId,String projectName
,Integer courseId,List<UserDto> students) {

}
