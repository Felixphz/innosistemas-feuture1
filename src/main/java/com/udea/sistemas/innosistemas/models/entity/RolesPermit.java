package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"RolesPermits\"")
public class RolesPermit {
    @SequenceGenerator(name = "RolesPermits_id_gen", sequenceName = "Roles_id_role_seq", allocationSize = 1)
    @EmbeddedId
    private RolesPermitId id;

    public RolesPermitId getId() {
        return id;
    }

    public void setId(RolesPermitId id) {
        this.id = id;
    }

    //TODO [Reverse Engineering] generate columns from DB
}