package com.udea.sistemas.innosistemas.models.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "permissions", schema="public")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Permissions_id_gen")
    @SequenceGenerator(name = "Permissions_id_gen", sequenceName = "Permissions_id_permit_seq", allocationSize = 1)
    @Column(name = "id_permit", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_permission", nullable = false)
    private String namePermission;

    @Size(max = 255)
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "permissions", cascade = CascadeType.ALL, 
    orphanRemoval = true, 
    fetch = FetchType.LAZY)
    private Set<RolesPermission> permissions = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamePermission() {
        return namePermission;
    }

    public void setNamePermission(String namePermission) {
        this.namePermission = namePermission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}