package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_teams", schema="public")
public class UsersTeam {
    @EmbeddedId
    private UsersTeamId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("email") 
    @JoinColumn(name = "email")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId") 
    @JoinColumn(name = "id_team")
    private Team team;

    public UsersTeamId getId() {
        return id;
    }

    public void setId(UsersTeamId id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}