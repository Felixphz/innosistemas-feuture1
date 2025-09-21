package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"UsersTeams\"")
public class UsersTeam {
    @SequenceGenerator(name = "UsersTeams_id_gen", sequenceName = "Teams_id_team_seq", allocationSize = 1)
    @EmbeddedId
    private UsersTeamId id;

    public UsersTeamId getId() {
        return id;
    }

    public void setId(UsersTeamId id) {
        this.id = id;
    }

    //TODO [Reverse Engineering] generate columns from DB
}