package com.udea.sistemas.innosistemas.models.entity;

import java.util.HashSet;
import java.util.Set;

import com.udea.sistemas.innosistemas.enums.TeamState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "teams", schema = "public")
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_team", nullable = false)
    private Integer idTeam;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_team", nullable = false)
    private String nameTeam;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyect_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UsersTeam> members = new HashSet<>();

    // Nuevo campo con enum
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private TeamState state = TeamState.INCOMPLETO; // Valor por defecto
    // Getters y Setters (Lombok los genera automáticamente con @Data)
}