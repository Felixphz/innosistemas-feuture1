package com.udea.sistemas.innosistemas.models.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "teams", schema="public")
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
    private Proyect project;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsersTeam> members = new HashSet<>();

    public Integer getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(Integer idTeam) {
        this.idTeam = idTeam;
    }

    public String getNameTeam() {
        return nameTeam;
    }

    public void setNameTeam(String nameTeam) {
        this.nameTeam = nameTeam;
    }

    public Proyect getProject() {
        return project;
    }

    public void setProject(Proyect project) {
        this.project = project;
    }

    public Set<UsersTeam> getMembers() {
        return members;
    }

    public void setMembers(Set<UsersTeam> members) {
        this.members = members;
    }
}