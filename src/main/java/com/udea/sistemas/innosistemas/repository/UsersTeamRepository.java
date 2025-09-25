package com.udea.sistemas.innosistemas.repository;

import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.models.entity.UsersTeamId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersTeamRepository extends JpaRepository<UsersTeam, UsersTeamId> {
}
