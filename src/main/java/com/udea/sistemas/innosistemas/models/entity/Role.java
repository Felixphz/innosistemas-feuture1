package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "\"Roles\"")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Roles_id_gen")
    @SequenceGenerator(name = "Roles_id_gen", sequenceName = "Roles_id_role_seq", allocationSize = 1)
    @Column(name = "id_role", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_rol", nullable = false)
    private String nameRol;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameRol() {
        return nameRol;
    }

    public void setNameRol(String nameRol) {
        this.nameRol = nameRol;
    }

}