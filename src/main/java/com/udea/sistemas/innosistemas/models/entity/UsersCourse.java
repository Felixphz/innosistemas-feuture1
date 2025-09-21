package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"UsersCourses\"")
public class UsersCourse {
    @SequenceGenerator(name = "UsersCourses_id_gen", sequenceName = "Teams_id_team_seq", allocationSize = 1)
    @EmbeddedId
    private UsersCourseId id;

    public UsersCourseId getId() {
        return id;
    }

    public void setId(UsersCourseId id) {
        this.id = id;
    }

    //TODO [Reverse Engineering] generate columns from DB
}