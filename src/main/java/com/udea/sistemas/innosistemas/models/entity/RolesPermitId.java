package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RolesPermitId implements Serializable {
    private static final long serialVersionUID = 7350903811879232292L;
    @NotNull
    @Column(name = "id_role", nullable = false)
    private Integer idRole;

    @NotNull
    @Column(name = "id_permit", nullable = false)
    private Integer idPermit;

    public Integer getIdRole() {
        return idRole;
    }

    public void setIdRole(Integer idRole) {
        this.idRole = idRole;
    }

    public Integer getIdPermit() {
        return idPermit;
    }

    public void setIdPermit(Integer idPermit) {
        this.idPermit = idPermit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RolesPermitId entity = (RolesPermitId) o;
        return Objects.equals(this.idPermit, entity.idPermit) &&
                Objects.equals(this.idRole, entity.idRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPermit, idRole);
    }

}