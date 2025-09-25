package com.udea.sistemas.innosistemas.models.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {

    private Integer id;

    private Integer proyectId;

    private String nameTeam;

    private Integer numIntegrantes;


    public boolean isNotnull (){
        return id != null && proyectId != null && nameTeam != null && numIntegrantes != null;
    }
}
