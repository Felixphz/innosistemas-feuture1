package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
@Table(name = "rolespermissions", schema="public")
public class RolesPermission {
    @SequenceGenerator(name = "RolesPermits_id_gen", sequenceName = "Roles_id_role_seq", allocationSize = 1)
    @EmbeddedId
    private RolesPermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idRole") 
    @JoinColumn(name = "id_role")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPermit") 
    @JoinColumn(name = "id_permit")
    private Permission permissions;

    public RolesPermissionId getId() {
        return id;
    }

    public void setId(RolesPermissionId id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission permissions) {
        this.permissions = permissions;
    }

}