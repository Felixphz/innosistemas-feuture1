package com.udea.sistemas.innosistemas.repository;
import com.udea.sistemas.innosistemas.models.entity.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = {"role"})
    List<User> findByRole_NameRol(String NameRol);

    @EntityGraph(attributePaths = {"email"})
    User findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"role"})
    List<User> findAll();

    @EntityGraph(attributePaths = {
        "userTeams",
        "userTeams.team",
        "userTeams.team.project"})
    List<User> findAllBy();

}

