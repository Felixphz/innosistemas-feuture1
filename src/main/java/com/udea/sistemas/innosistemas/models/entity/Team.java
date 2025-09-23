package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "teams", schema="public")
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Teams_id_gen")
    @SequenceGenerator(name = "Teams_id_gen", sequenceName = "Teams_id_team_seq", allocationSize = 1)
    @Column(name = "id_team", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "\"Proyect_id\"", nullable = false)
    private Integer proyectId;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_team", nullable = false)
    private String nameTeam;

    @NotNull
    @Column(name = "num_integrantes", nullable = false)
    private Integer numIntegrantes;


}