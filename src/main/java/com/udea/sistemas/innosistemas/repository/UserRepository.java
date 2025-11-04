package com.udea.sistemas.innosistemas.repository;
import com.udea.sistemas.innosistemas.models.entity.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = {"role"})
    List<User> findByRole_NameRol(String NameRol);

    // EntityGraph que carga User con Role y todos sus permisos
    @EntityGraph(attributePaths = {
        "role", 
        "role.rolesPermission", 
        "role.rolesPermission.permissions"
    })
    
    User findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"role"})
    List<User> findAll();

    @EntityGraph(attributePaths = {
        "userTeams",
        "userTeams.team",
        "userTeams.team.project"
    })
    List<User> findAllBy();

    // Método para eliminar por email (que es el @Id de User)
    @Modifying
    @Transactional
    void deleteByEmail(String email);

    // findByEmail ya está definido arriba con @EntityGraph
    // deleteByEmail ya está definido arriba
}

